package it.luzzetti.lab.keycloak.configs.providers.prototype;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

@Log
@RequiredArgsConstructor
public class StoopidUserStorageProvider
    implements UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider,
        CredentialInputValidator {

  protected final KeycloakSession session;
  protected final ComponentModel model;



  @Override
  public void close() {
    log.info("Pulizia risorse");
    // Eventuale chiusura delle risorse aperte
  }

  /* ***************************************************************************************************
   *                                UserLookupProvider methods
   * This interface is required if you want to be able to log in with users from this external store.
   * Most (all?) providers implement this interface.
   * ***************************************************************************************************/

  @Override
  public UserModel getUserById(RealmModel realm, String id) {
    log.info("getUserById: " + id);

    StorageId storageId = new StorageId(id);
    String username = storageId.getExternalId();
    return getUserByUsername(realm, username);
  }

  @Override
  public UserModel getUserByUsername(RealmModel realmModel, String username) {
    log.info("getUserByUsername: " + username);

    // Cerco la persona nel db...

    // Returning a dummy user object
    return generaFintoUtente(realmModel, username);
  }

  private UserModel generaFintoUtente(RealmModel realmModel, String username) {
    return new StoopidUserAdapter(session, realmModel, model, username);
  }

  @Override
  public UserModel getUserByEmail(RealmModel realmModel, String email) {
    log.info("getPersonByEmail: " + email);
    return generaFintoUtente(realmModel, email);
  }

  /* ***************************************************************************************************
   *                                UserQueryProvider methods
   * Without implementing UserQueryMethodsProvider the Admin Console would not be able to view
   * and manage users that were loaded by our custom provider
   *************************************************************************************************** */

  @Override
  public int getUsersCount(RealmModel realm) {
    // Nella realtà dovrei contare nel DB
    return 1;
  }

  /***
   * This is just a typical call to a repository, to get a list of all the users.
   * It just happens to be PAGINATED, and the result is returned as a Stream instead of a List
   */
  @Override
  public Stream<UserModel> searchForUserStream(
      RealmModel realmModel,
      Map<String, String> extraParams,
      Integer firstResult,
      Integer maxResults) {

    log.info("searchForPersonStream: " + extraParams);

    return Stream.of(generaFintoUtente(realmModel, "dummyUser"));
  }

  @Override
  public Stream<UserModel> getGroupMembersStream(
      RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {

    log.severe(".getGroupMembersStream() has NOT BEEN IMPLEMENTED");
    return Stream.empty();
  }

  @Override
  public Stream<UserModel> searchForUserByUserAttributeStream(
      RealmModel realm, String attrName, String attrValue) {

    log.severe(".searchForUserByUserAttributeStream() has NOT BEEN IMPLEMENTED");
    return Stream.empty();
  }

  /* ***************************************************************************************************
   *                                CredentialInputValidator methods
   * These methods are related to the password validation mechanism.
   * In its simplest form, it asks:
   *    a. Is this particular method TYPE allowed to log in? (password/cert/...)
   *    b. Is this provided challenge (es: the plain-text password) valid for that specific user?
   *
   * Of course this can be complicated as much as you want
   *************************************************************************************************** */

  @Override
  public boolean supportsCredentialType(String credentialType) {
    return PasswordCredentialModel.TYPE.endsWith(credentialType);
  }

  @Override
  public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
    return supportsCredentialType(credentialType);
  }

  @Override
  public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {

    if (!supportsCredentialType(credentialInput.getType())) return false;

    String aPassword = credentialInput.getChallengeResponse();
    if (aPassword == null) {
      return false;
    }

    return aPassword.equalsIgnoreCase("christian");
  }
}
