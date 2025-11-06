package dev.okawari.dto;

import dev.okawari.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String nickname;
    private String password;
    private String email;

    public static UserDTO fromUser(User user){
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        return dto;
    }

    public User toUser(){
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
