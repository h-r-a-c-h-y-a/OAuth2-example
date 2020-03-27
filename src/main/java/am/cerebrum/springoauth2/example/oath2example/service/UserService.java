package am.cerebrum.springoauth2.example.oath2example.service;

import am.cerebrum.springoauth2.example.oath2example.model.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Map;

public interface UserService {

    String register(User user) throws Exception;

    Map<String, Object> signup(String email, String password) throws Exception;

    User createUserFromOAuth(DefaultOAuth2User oAuth2User, String userType);
}
