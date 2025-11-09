package dev.okawari.service;

import dev.okawari.dto.UserDTO;
import dev.okawari.entity.User;
import dev.okawari.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 만들어줌
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션
public class UserService {

    private final UserRepository userRepository;

    /**
     * 로그인 로직
     * @param loginRequest (email, password)
     * @return 로그인 성공 시 SessionUser DTO, 실패 시 null
     */
    public UserDTO.SessionUser login(UserDTO.LoginRequest loginRequest) {

        // 1. 이메일로 사용자를 조회
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        // 2. 이메일이 존재하지 않으면 null 반환
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // 3. 비밀번호가 일치하는지 검사 (평문 비교)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return null; // 비밀번호 불일치
        }

        // 4. 로그인 성공. 세션에 저장할 DTO를 생성하여 반환
        return new UserDTO.SessionUser(user);
    }

    /**
     * 회원가입 로직
     * @param request (email, password, passwordConfirm, nickname)
     * @return 에러 메시지 키 (성공 시 null)
     */
    @Transactional // 쓰기(INSERT) 작업이므로 트랜잭션 적용
    public String signup(UserDTO.SignupRequest request) {

        // 1. 비밀번호 확인 일치 여부
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            return "passwordMismatch"; // 비밀번호 불일치
        }

        // 2. 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            return "duplicateEmail"; // 이메일 중복
        }

        // 3. 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            return "duplicateNickname"; // 닉네임 중복
        }

        // 4. 모든 검증 통과. User 엔티티 생성
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword()); // (시연용) 평문 저장
        newUser.setNickname(request.getNickname());
        // (createdAt은 User 엔티티의 @PrePersist에 의해 자동 생성됨)

        // 5. DB에 저장
        userRepository.save(newUser);

        return null; // 회원가입 성공
    }
}