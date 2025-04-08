package kr.kainos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web MVC 설정 클래스입니다. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * 모든 경로에 대해 CORS를 허용합니다.
   *
   * @param registry CORS 등록 객체
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST")
        .allowedHeaders("*")
        .allowCredentials(true);
  }

  /**
   * ObjectMapper를 등록하여 Jackson 메시지 컨버터를 설정합니다.
   *
   * @return MappingJackson2HttpMessageConverter
   */
  @Bean
  public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    return new MappingJackson2HttpMessageConverter(objectMapper);
  }
}
