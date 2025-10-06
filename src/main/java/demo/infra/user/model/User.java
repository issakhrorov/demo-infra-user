package demo.infra.user.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
  @Id private Long id = null;
  private String login;
  private String firstname;
  private String lastname;
  private String password;
  private String email = null;
  LocalDateTime createdAt = LocalDateTime.now();
  LocalDateTime updatedAt = LocalDateTime.now();
}
