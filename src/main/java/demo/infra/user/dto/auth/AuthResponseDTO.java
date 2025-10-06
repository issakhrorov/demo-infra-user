package demo.infra.user.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponseDTO(
  UserInfoDTO user_info,
  String token,
  String refresh_token
) {}