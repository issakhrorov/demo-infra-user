package demo.infra.user.service;

import demo.infra.user.config.jwt.JwtTokenUtil;
import demo.infra.user.dto.auth.AuthResponseDTO;
import demo.infra.user.dto.auth.UserInfoDTO;
import demo.infra.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {
  public final AuthService authService;
  public final JwtTokenUtil jwtTokenUtil;

  public UserInfoService(AuthService authService, JwtTokenUtil jwtTokenUtil) {
    this.authService = authService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  public AuthResponseDTO getTokenAndUserInfo(User user) {
    var token = jwtTokenUtil.generateAccessToken(user);
    var refreshToken = jwtTokenUtil.generateRefreshToken(user);
    var dto = new UserInfoDTO().toDTO(user);

    return new AuthResponseDTO(dto, token, refreshToken);
  }
}
