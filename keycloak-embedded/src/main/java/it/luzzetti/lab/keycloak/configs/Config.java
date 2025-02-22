package it.luzzetti.lab.keycloak.configs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;

import it.luzzetti.lab.keycloak.configs.providers.SimplePlatformProvider;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keycloak è sviluppato con quarkus e non con spring boot. Ma a noi spring-boot piace tanto
 * tanto...quindi, andiamo a smanacciare un po' per far partire un keycloak dentro un'applicazione
 * spring-boot.
 *
 * <p>(Questo ci servirà per sviluppare temi ed estensioni in maniera umana)
 *
 * <p>As we can see here, we first configured Keycloak as a JAX-RS application with ServerProperties
 * for persistent storage of Keycloak properties as specified in our realm definition file.
 *
 * <p>We then added a session management filter and mocked a JNDI environment to use a
 * spring/datasource, which is our in-memory H2 database.
 */
@Configuration
@RequiredArgsConstructor
public class Config {

  private final ServerProperties properties;
  private final DataSource dataSource;

  @Bean
  @ConditionalOnMissingBean(name = "springBootPlatform")
  protected SimplePlatformProvider springBootPlatform() {
    return (SimplePlatformProvider) Platform.getPlatform();
  }

  @Bean
  ServletRegistrationBean<HttpServlet30Dispatcher> keycloakJaxRsApplication() {
    try {
      mockJndiEnvironment();
    } catch (NamingException ex) {
      Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
    }
    KeycloakEmbedded.properties = properties;
    final var servlet = new ServletRegistrationBean<>(new HttpServlet30Dispatcher());
    servlet.addInitParameter("jakarta.ws.rs.Application", KeycloakEmbedded.class.getName());
    servlet.addInitParameter(
        ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, properties.contextPath());
    servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true");
    servlet.addUrlMappings(properties.contextPath() + "/*");
    servlet.setLoadOnStartup(2);
    servlet.setAsyncSupported(true);
    return servlet;
  }

  /**
   * As you can see, we use a KeycloakSession to perform all the transactions, and for this to work
   * properly, we had to create a custom AbstractRequestFilter (RequestFilter)
   */
  @Bean
  FilterRegistrationBean<RequestFilter> keycloakSessionManagement(
      ServerProperties serverProperties) {

    var filter = new FilterRegistrationBean<RequestFilter>();
    filter.setName("Keycloak Session Management");
    filter.setFilter(new RequestFilter());
    filter.addUrlPatterns(properties.contextPath() + "/*");

    return filter;
  }

  private void mockJndiEnvironment() throws NamingException {
    NamingManager.setInitialContextFactoryBuilder(
        env ->
            environment ->
                new InitialContext() {

                  @Override
                  public Object lookup(Name name) {
                    return lookup(name.toString());
                  }

                  @Override
                  public Object lookup(String name) {
                    if ("spring/datasource".equals(name)) {
                      return dataSource;
                    } else if (name.startsWith("java:jboss/ee/concurrency/executor/")) {
                      return fixedThreadPool();
                    }
                    return null;
                  }

                  @Override
                  public NameParser getNameParser(String name) {
                    return CompositeName::new;
                  }

                  @Override
                  public void close() {
                    // NOOP
                  }
                });
  }

  @Bean("fixedThreadPool")
  public ExecutorService fixedThreadPool() {
    return Executors.newFixedThreadPool(5);
  }
}
