package dev.okawari.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
}
