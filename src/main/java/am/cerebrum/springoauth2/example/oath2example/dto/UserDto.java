package am.cerebrum.springoauth2.example.oath2example.dto;

import am.cerebrum.springoauth2.example.oath2example.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private long id;
    private String username;
    private String email;
    @JsonProperty(value = "image_url")
    private String imageUrl;
    private String role;
    @JsonProperty(value = "user_type")
    private String userType;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.userType = user.getUserType().name();
    }
}
