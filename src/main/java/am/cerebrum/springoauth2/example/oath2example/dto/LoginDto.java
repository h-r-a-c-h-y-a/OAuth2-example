package am.cerebrum.springoauth2.example.oath2example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDto {
    private String name;
    private String email;
    private String password;

}
