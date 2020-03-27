package am.cerebrum.springoauth2.example.oath2example.dto;

import lombok.Data;

@Data
public class GoogleOAuth2UserInfo {
    private String id;
    private String name;
    private String email;
    private String imageUrl;
}
