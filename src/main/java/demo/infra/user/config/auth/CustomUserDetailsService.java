package demo.infra.user.config.auth;

import demo.infra.user.model.User;
import demo.infra.user.service.AuthService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class CustomUserDetailsService implements UserDetailsService {
  private final AuthService userService;

  public CustomUserDetailsService(AuthService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    User user = userService.getUserById(Long.getLong(userId));
    if (user == null)
      throw new UsernameNotFoundException(userId);

    var userBuilder = org.springframework.security.core.userdetails.User.withUsername(userId);

    userBuilder.password(user.getPassword());
    userBuilder.roles("USER");

    return null;
  }
}
