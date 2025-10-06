package demo.infra.user.controller;

import demo.infra.user.config.jwt.JwtTokenUtil;
import demo.infra.user.dto.auth.AuthResponseDTO;
import demo.infra.user.dto.auth.SignInDTO;
import demo.infra.user.dto.auth.SignUpDTO;
import demo.infra.user.dto.refreshToken.RefreshTokenDTO;
import demo.infra.user.dto.refreshToken.RefreshTokenResponseDTO;
import demo.infra.user.service.AuthService;
import demo.infra.user.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/${api.path}/auth")
public class AuthController {
  private final AuthService authService;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserInfoService userInfoService;

  public AuthController(
    AuthService authService,
    JwtTokenUtil jwtTokenUtil,
    UserInfoService userInfoService
  ) {
    this.authService = authService;
    this.jwtTokenUtil = jwtTokenUtil;
    this.userInfoService = userInfoService;
  }

  @PostMapping("/register")
  public AuthResponseDTO registerUser(@RequestBody SignUpDTO dto) throws Exception {
    var entity = authService.signUp(dto);
    return userInfoService.getTokenAndUserInfo(entity);
  }

  @PostMapping("/login")
  public AuthResponseDTO login(@RequestBody SignInDTO request) {
    var user = authService.signIn(request);
    return userInfoService.getTokenAndUserInfo(user);
  }

  @PostMapping("/access_token")
  public RefreshTokenResponseDTO refreshAccessToken(Principal principal) {
    var user = authService.getUserById(Long.getLong(principal.getName()));

    var newToken = jwtTokenUtil.generateRefreshToken(user);
    return new RefreshTokenResponseDTO(newToken, null);
  }

  @PostMapping("/refresh_token")
  public RefreshTokenResponseDTO extendRefreshToken(Principal principal) {
    var user = authService.getUserById(Long.getLong(principal.getName()));

    var newAccessToken = jwtTokenUtil.generateRefreshToken(user);
    var newRefreshToken = jwtTokenUtil.generateRefreshToken(user);
    return new RefreshTokenResponseDTO(newAccessToken, newRefreshToken);
  }

  @DeleteMapping("/logout")
  public Boolean logout(@RequestBody RefreshTokenDTO dto) {
    return true;
  }

}
