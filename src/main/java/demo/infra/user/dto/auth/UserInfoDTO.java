package demo.infra.user.dto.auth;

import demo.infra.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
  private Long user_id;
  private String login;
  private String lastname;
  private String firstname;

  public UserInfoDTO toDTO(User user) {
    var dto = new UserInfoDTO();

    dto.user_id = user.getId();
    dto.login = user.getLogin();
    dto.lastname = user.getLastname();
    dto.firstname = user.getFirstname();

    return dto;
  }
}