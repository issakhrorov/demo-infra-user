package demo.infra.user.config.auth;

import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider, Serializable {
  @SneakyThrows
  @Override
  public Authentication authenticate(Authentication authentication) {
    if (
      authentication != null &&
        authentication.getPrincipal() != null &&
        authentication.getAuthorities() != null &&
        !authentication.getAuthorities().isEmpty()
    ) {
      try {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        return new UsernamePasswordAuthenticationToken(
          username,
          password,
          authentication.getAuthorities()
        );
      } catch (AuthenticationException e) {
        throw new Exception("TODO// ADD NEW CUSTOM EXCEPTION");
      }
    }
    else if (authentication != null && authentication.getPrincipal() != null) {
      try {
        String username = authentication.getPrincipal().toString();
        String password= authentication.getCredentials().toString();
        return new UsernamePasswordAuthenticationToken(
          username,
          password
        );
      } catch (AuthenticationException e) {
        throw new Exception("TODO// ADD NEW CUSTOM EXCEPTION");
      }
    }
    else throw new Exception("TODO// ADD NEW CUSTOM EXCEPTION");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}