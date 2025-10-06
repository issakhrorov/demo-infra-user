package demo.infra.user.dto.auth;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignInDTO(
  String login,
  String password
) {}
