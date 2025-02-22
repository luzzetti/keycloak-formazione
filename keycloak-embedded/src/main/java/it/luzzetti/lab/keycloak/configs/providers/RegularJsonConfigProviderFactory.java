package it.luzzetti.lab.keycloak.configs.providers;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.keycloak.common.util.Resteasy;
import org.keycloak.common.util.SystemEnvProperties;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;

/*
 * Note that the values defined in the keycloack-server.json file are just placeholders.
 * It is not possible to overwrite them through environment variables unless
 * a key’s name starts with the ‘env.*’ prefix.
 *
 * To overwrite a variable with an environment variable
 * regardless of the prefix, one has to implement a custom
 * ProviderFactory and override getProperties method.
 *
 * Importantly, information about these custom providers should be included
 * in the project’s META-INF/services folder so that they are picked up at runtime.
 */
@Log4j2
public class RegularJsonConfigProviderFactory extends JsonConfigProviderFactory {

  public static final String SERVER_CONTEXT_CONFIG_PROPERTY_OVERRIDES =
      "keycloak.server.context.config.property-overrides";

  @Override
  protected Properties getProperties() {
    return new SystemEnvProperties(System.getenv());
  }

  /** Non ancora implementato */
  private Map<String, String> getPropertyOverrides() {
    final var context = Resteasy.getContextData(ServletContext.class);
    final var propertyOverridesMap = new HashMap<String, String>();
    final var propertyOverrides =
        context.getInitParameter(SERVER_CONTEXT_CONFIG_PROPERTY_OVERRIDES);
    try {
      if (context.getInitParameter(SERVER_CONTEXT_CONFIG_PROPERTY_OVERRIDES) != null) {
        final var jsonObj = JsonSerialization.mapper.readTree(propertyOverrides);
        jsonObj
            .fields()
            .forEachRemaining(e -> propertyOverridesMap.put(e.getKey(), e.getValue().asText()));
      }
    } catch (IOException e) {
      log.fatal("Unexpected error reading property overrides: " + e.getMessage());
    }
    return propertyOverridesMap;
  }
}
