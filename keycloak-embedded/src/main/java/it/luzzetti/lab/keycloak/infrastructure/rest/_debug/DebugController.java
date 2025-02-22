package it.luzzetti.lab.keycloak.infrastructure.rest._debug;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

  @GetMapping
  public ResponseEntity<Void> debug() {
    return ResponseEntity.ok().build();
  }
}
