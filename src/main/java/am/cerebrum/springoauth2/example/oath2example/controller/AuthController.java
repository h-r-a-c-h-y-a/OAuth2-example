package am.cerebrum.springoauth2.example.oath2example.controller;

import am.cerebrum.springoauth2.example.oath2example.dto.LoginDto;
import am.cerebrum.springoauth2.example.oath2example.dto.UserDto;
import am.cerebrum.springoauth2.example.oath2example.model.User;
import am.cerebrum.springoauth2.example.oath2example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView loadLoginPage(ModelAndView model) {
        model.setViewName("login");
        return model;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(LoginDto loginDto) {
        User user = new User();
        user.setUsername(loginDto.getName());
        user.setEmail(loginDto.getEmail());
        user.setPassword(loginDto.getPassword());
        String token = null;
        try {
            token = userService.register(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user already exist");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", new UserDto(user));
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> login(LoginDto loginDto) {
        String token = null;
        Map<String, Object> map = null;
        try {
        map = userService.signup(loginDto.getEmail(), loginDto.getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user not found exist");
        }
        map.put("token", map.get("token"));
        map.put("user", new UserDto((User) map.get("user")));
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }
}
