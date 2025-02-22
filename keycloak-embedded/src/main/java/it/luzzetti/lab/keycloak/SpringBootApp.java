package it.luzzetti.lab.keycloak;

import it.luzzetti.lab.keycloak.configs.ServerProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties(ServerProperties.class)
@Log4j2
public class SpringBootApp {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootApp.class, args);
  }
}
