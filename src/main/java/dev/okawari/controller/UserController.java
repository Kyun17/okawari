package dev.okawari.controller;

<<<<<<< HEAD
import dev.okawari.dto.LoginRequest;
import dev.okawari.dto.UserDTO;
import dev.okawari.entity.User;
import dev.okawari.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest req, HttpSession session) {
        User user = userService.login(req.getEmail(), req.getPassword());
        session.setAttribute("user", user);
        return ResponseEntity.ok(UserDTO.fromUser(user));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@SessionAttribute(value = "user", required = false) User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(UserDTO.fromUser(user));
    }
}


