package kr.kainos.main;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Health check API. */
@RestController
@Slf4j
@Tag(name = "A - Monitoring", description = "모니터링")
public class MainController {

  /**
   * Health check API.
   *
   * @return status=UP JSON
   */
  @GetMapping("/")
  public Map<String, String> home() {
    return Collections.singletonMap("status", "UP");
  }

  /**
   * Health check API.
   *
   * @return status=UP JSON
   */
  @GetMapping("/status/health")
  public Map<String, String> healthCheck() {
    return Collections.singletonMap("status", "UP");
  }
}
