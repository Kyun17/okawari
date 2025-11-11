package dev.okawari.service;

import dev.okawari.entity.User;
import dev.okawari.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void signup(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword()) // 나중에 BCrypt로 암호화 추천
                .email(userDTO.getEmail())
                .build();

        userRepository.save(user);
    }
}