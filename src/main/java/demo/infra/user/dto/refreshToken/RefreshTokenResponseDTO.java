package demo.infra.user.dto.refreshToken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RefreshTokenResponseDTO(
  String access_token,
  String refresh_toke
) {}
