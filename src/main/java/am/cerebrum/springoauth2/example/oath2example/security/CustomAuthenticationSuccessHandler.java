package am.cerebrum.springoauth2.example.oath2example.security;

import am.cerebrum.springoauth2.example.oath2example.model.User;
import am.cerebrum.springoauth2.example.oath2example.repository.UserRepository;
import am.cerebrum.springoauth2.example.oath2example.service.TokenRedis;
import am.cerebrum.springoauth2.example.oath2example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static am.cerebrum.springoauth2.example.oath2example.common.Constants.HOME_URL;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    @Autowired
    private UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenRedis redis;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, TokenRedis redis) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.redis = redis;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        String userType = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = userService.createUserFromOAuth(oAuth2User, userType);
        }
        String token = jwtTokenUtil.generateToken(user);
        redis.add(user.getEmail(), token, 300); //(int) (System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 15)
        String redirectionUrl = UriComponentsBuilder.fromUriString(HOME_URL)
                .queryParam("auth_token", "Bearer " + token)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, redirectionUrl);
    }

}

