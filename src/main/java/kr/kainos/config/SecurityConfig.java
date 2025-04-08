package kr.kainos.config;

import java.util.List;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/** Spring Security 설정 클래스입니다. 개발(local/test)과 운영 환경을 분리하여 인증 정책을 구성합니다. */
@Configuration
public class SecurityConfig {

  private final Environment env;

  /**
   * SecurityConfig 생성자입니다.
   *
   * @param env Spring 환경 변수
   */
  public SecurityConfig(Environment env) {
    this.env = env;
  }

  private static final String[] PUBLIC_URLS = {
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/swagger-resources/**",
    "/webjars/**",
    "/status/**",
    "/"
  };

  /**
   * Spring Security FilterChain 설정을 구성합니다. 개발 환경에서는 모든 요청을 허용하고, 운영 환경에서는 인증을 적용합니다.
   *
   * @param http HttpSecurity 인스턴스
   * @return 구성된 SecurityFilterChain
   * @throws Exception 보안 구성 중 예외
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    List<String> profiles = List.of(env.getActiveProfiles());
    boolean isDev = profiles.contains("local") || profiles.contains("test");

    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(
        auth -> {
          auth.requestMatchers(PUBLIC_URLS).permitAll();
          if (isDev) {
            auth.anyRequest().permitAll();
          } else {
            auth.anyRequest().authenticated();
          }
        });

    if (!isDev) {
      http.httpBasic(Customizer.withDefaults());
    }

    return http.build();
  }

  /**
   * BCrypt 기반의 PasswordEncoder 빈을 생성합니다.
   *
   * @return PasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 운영 환경에서는 관리 계정을 설정하고, 개발/테스트 환경에서는 인증 없이 빈 사용자 저장소를 생성합니다.
   *
   * @param encoder 비밀번호 암호화기
   * @return UserDetailsService
   */
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    List<String> activeProfiles = List.of(env.getActiveProfiles());

    if (activeProfiles.contains("local") || activeProfiles.contains("test")) {
      return new InMemoryUserDetailsManager();
    }

    String username =
        Objects.requireNonNull(env.getProperty("auth.admin.username"), "관리자 계정(username) 설정 누락");
    String rawPassword =
        Objects.requireNonNull(env.getProperty("auth.admin.password"), "관리자 비밀번호 설정 누락");

    var admin =
        User.withUsername(username).password(encoder.encode(rawPassword)).roles("ADMIN").build();

    return new InMemoryUserDetailsManager(admin);
  }
}
