package demo.infra.user.config.auth;

import demo.infra.user.config.jwt.JwtAuthenticationEntryPoint;
import demo.infra.user.config.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtRequestFilter jwtRequestFilter;
  private final MyAuthenticationProvider myAuthenticationProvider;
  private final JwtAuthenticationEntryPoint unauthorizedHandler;

  public SecurityConfig(
    JwtRequestFilter jwtRequestFilter,
    MyAuthenticationProvider myAuthenticationProvider,
    JwtAuthenticationEntryPoint unauthorizedHandler
  ) {
    this.jwtRequestFilter = jwtRequestFilter;
    this.myAuthenticationProvider = myAuthenticationProvider;
    this.unauthorizedHandler = unauthorizedHandler;
  }

  @Value("${api.path}")
  private String apiPath;


  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
      .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/" + apiPath + "/public-key").permitAll()
        .requestMatchers("/" + apiPath + "/auth/register").permitAll()
        .requestMatchers("/" + apiPath + "/auth/login").permitAll()
        .anyRequest().authenticated()
      )
        .authenticationProvider(myAuthenticationProvider)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
    configuration.setAllowedHeaders(List.of(
      "authorization",
      "Access-Control-Allow-Headers",
      "Access-Control-Request-Method",
      "Access-Control-Request-Headers",
      "content-type",
      "x-auth-token",
      "origin",
      "x-requested-with",
      "cache-control"
    ));
    configuration.setAllowCredentials(false);
    configuration.setExposedHeaders(List.of("x-auth-token"));
    configuration.addAllowedOrigin("http://localhost:5173");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
