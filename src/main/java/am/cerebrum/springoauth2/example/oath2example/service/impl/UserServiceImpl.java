package am.cerebrum.springoauth2.example.oath2example.service.impl;

import am.cerebrum.springoauth2.example.oath2example.model.Role;
import am.cerebrum.springoauth2.example.oath2example.model.User;
import am.cerebrum.springoauth2.example.oath2example.model.UserType;
import am.cerebrum.springoauth2.example.oath2example.repository.UserRepository;
import am.cerebrum.springoauth2.example.oath2example.security.JwtTokenUtil;
import am.cerebrum.springoauth2.example.oath2example.service.TokenRedis;
import am.cerebrum.springoauth2.example.oath2example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static am.cerebrum.springoauth2.example.oath2example.model.Role.ROLE_USER;
import static am.cerebrum.springoauth2.example.oath2example.model.UserType.USUAL;

@Service
public class UserServiceImpl implements UserService {

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    private final TokenRedis redis;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(JwtTokenUtil jwtTokenUtil, TokenRedis redis, PasswordEncoder passwordEncoder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.redis = redis;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String register(User user) throws Exception {
        boolean isExist = userRepository.existsByEmail(user.getEmail());
        if (isExist) {
            throw new Exception("User already exist.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        user.setRole(ROLE_USER);
        user.setUserType(USUAL);
        userRepository.save(user);
        String token = jwtTokenUtil.generateToken(user);
        redis.add(user.getEmail(), token, 300); //(int) (System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 15)
        return token;
    }

    @Override
    public Map<String, Object> signup(String email, String password) throws Exception {
        User dbUser = userRepository.findByEmail(email);
        if (!passwordEncoder.matches(password.trim(), dbUser.getPassword())) {
            throw new Exception("user not exist");
        }
        String token = jwtTokenUtil.generateToken(dbUser);
        redis.add(dbUser.getEmail(), token, 300); //(int) (System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 15)
        return new HashMap<String, Object>(){{
            put("user", dbUser);
            put("token", token);
        }};
    }

    @Override
    public User createUserFromOAuth(DefaultOAuth2User oAuth2User, String userType) {
        Map<String, Object> attr = oAuth2User.getAttributes();
        User user = new User();
        user.setUsername((String) attr.get("name"));
        user.setEmail((String) attr.get("email"));
        user.setImageUrl((String) attr.get("picture"));
        user.setUserType(UserType.valueOf(userType.toUpperCase()));
        user.setRole(Role.valueOf(oAuth2User.getAuthorities().stream().findAny().get().getAuthority()));
        user = userRepository.save(user);
        return user;
    }


}
