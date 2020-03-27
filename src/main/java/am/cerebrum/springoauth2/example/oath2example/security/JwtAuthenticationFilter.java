package am.cerebrum.springoauth2.example.oath2example.security;

import am.cerebrum.springoauth2.example.oath2example.service.TokenRedis;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static am.cerebrum.springoauth2.example.oath2example.common.Constants.TOKEN_PARAM;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenRedis tokenRedis;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String token = req.getParameter(TOKEN_PARAM);
        if (token == null) {
            token = req.getHeader("Authorization");
        }
        String username = null;
        if (token != null && (SecurityContextHolder.getContext().getAuthentication()) != null) {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOAuth2User) {
                username = ((DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAttribute("email");
            } else {
                username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            }
            token = tokenRedis.get(username);
            if (token == null) {
                token = jwtTokenUtil.refreshToken(username);
                tokenRedis.add(username, token, 300); //(int) (System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 15)
                res.setHeader("Authorization", "Bearer " + token);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                logger.warn("the token is expired and not valid anymore");
            }
        } else {
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
                } catch (IllegalArgumentException e) {
                    logger.error("an error occured during getting username from token", e);
                } catch (ExpiredJwtException e) {
                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String refreshToken = jwtTokenUtil.refreshToken(userDetails.getUsername());
                    res.setHeader("Authorization", "Bearer " + refreshToken);
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    logger.warn("the token is expired and not valid anymore", e);
                }
            } else {
                logger.warn("couldn't find bearer string, will ignore the header");
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(token.substring(7), userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(req, res);
    }
}
