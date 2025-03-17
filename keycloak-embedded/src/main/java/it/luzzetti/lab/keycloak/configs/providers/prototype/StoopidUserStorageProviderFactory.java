package it.luzzetti.lab.keycloak.configs.providers.prototype;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.*;
import lombok.extern.java.Log;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

@Log
public class StoopidUserStorageProviderFactory
    implements UserStorageProviderFactory<StoopidUserStorageProvider> {

  private static final String PROVIDER_NAME = "Stoopid-Provider";


  @Override
  public void init(Config.Scope config) {
    log.info("Initializing Stoopid-Provider");
  }

  /***
   * Responsible for allocating an instance of the provider class.
   * @param keycloakSession can be used to look up other information and metadata as well as provide
   *                        access to various other components within the runtime.
   * @param componentModel represents how the provider was enabled and configured within a specific realm.
   *                       It contains the instance id of the enabled provider as well as any configuration
   *                       you may have specified for it when you enabled through the admin console.
   * @return a UserStorageProvider implementation, valid for this session/transaction
   */
  @Override
  public StoopidUserStorageProvider create(
      KeycloakSession keycloakSession, ComponentModel componentModel) {

    log.info("Creating Stoopid-Provider");

    try {
      return new StoopidUserStorageProvider(keycloakSession, componentModel);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /***
   * Here we’ll get everything we need to validate the parameters passed on when our provided was added to a Realm.
   * In our case, we use this information to establish a database connection and execute the validation query.
   * @throws ComponentValidationException signaling Keycloak that the parameters are invalid.
   */
  @Override
  public void validateConfiguration(
      KeycloakSession session, RealmModel realm, ComponentModel config)
      throws ComponentValidationException {

    // no-op
    log.info("Validating Configurations");
  }


  /***
   * This id will be displayed in the admin console’s User Federation page
   * when you want to enable the provider for a specific realm.
   *
   * @return the name of the User Storage provider.
   */
  @Override
  public String getId() {
    return PROVIDER_NAME;
  }

  @Override
  public String getHelpText() {
    return "This is a Stoopid Provider with in-memory users";
  }

  @Override
  public void close() {
    log.info("Closing the Stoopid-Provider");
  }
}
