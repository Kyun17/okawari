package dev.okawari.dto;

import lombok.Getter;
import lombok.Setter;

// DTO를 하나의 파일에서 관리 (Inner Class)
public class UserDTO {

    /**
     * 로그인 폼(login.html)에서 컨트롤러로 전송될 때 사용할 DTO
     */
    @Getter
    @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    /**
     * 로그인 성공 시, HttpSession에 저장할 사용자 정보 DTO
     * (비밀번호 등 민감 정보를 제외하고 필요한 정보만 담음)
     */
    @Getter
    @Setter
    public static class SessionUser {
        private Long userId;
        private String nickname;
        private String email;

        // User 엔티티를 SessionUser DTO로 변환하는 생성자
        public SessionUser(dev.okawari.entity.User user) {
            this.userId = user.getId(); // User 엔티티의 getId()
            this.nickname = user.getNickname(); // User 엔티티의 getNickname()
            this.email = user.getEmail();
        }
    }

    /**
     *  회원가입 폼(signup.html)에서 컨트롤러로 전송될 때 사용할 DTO
     */
    @Getter
    @Setter
    public static class SignupRequest {
        private String email;
        private String password;
        private String passwordConfirm; // 비밀번호 확인 필드
        private String nickname;
    }
}