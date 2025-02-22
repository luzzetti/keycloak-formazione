package it.luzzetti.lab.keycloak.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.server")
public record ServerProperties(String contextPath, String username, String password) {}
