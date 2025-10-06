package demo.infra.user.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignUpDTO(
  String login,
  String lastname,
  String firstname,
  String email,
  String password
) {}
