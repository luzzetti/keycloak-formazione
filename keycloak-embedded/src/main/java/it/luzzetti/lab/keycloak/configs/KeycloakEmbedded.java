package it.luzzetti.lab.keycloak.configs;

import java.util.NoSuchElementException;

import it.luzzetti.lab.keycloak.configs.providers.RegularJsonConfigProviderFactory;
import jakarta.ws.rs.ApplicationPath;
import lombok.extern.log4j.Log4j2;
import org.keycloak.Config;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;

@Log4j2
@ApplicationPath("/")
public class KeycloakEmbedded extends KeycloakApplication {

  static ServerProperties properties;

  @Override
  protected void loadConfig() {
    JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
    Config.init(factory.create().orElseThrow(() -> new NoSuchElementException("No value present")));
  }

  @Override
  protected ExportImportManager bootstrap() {
    final ExportImportManager exportImportManager = super.bootstrap();
    createMasterRealmAdminUser();
    return exportImportManager;
  }

  private void createMasterRealmAdminUser() {
    try (KeycloakSession session = getSessionFactory().create()) {
      ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);

      try {
        session.getTransactionManager().begin();
        applianceBootstrap.createMasterRealmUser(properties.username(), properties.password());
        session.getTransactionManager().commit();
      } catch (Exception ex) {
        log.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
        session.getTransactionManager().rollback();
      }
    }
  }
}
