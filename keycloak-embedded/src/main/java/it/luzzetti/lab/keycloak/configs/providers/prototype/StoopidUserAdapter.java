package it.luzzetti.lab.keycloak.configs.providers.prototype;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

@Log
public class StoopidUserAdapter extends AbstractUserAdapterFederatedStorage {

  protected String keycloakId;
  protected String username;

  public StoopidUserAdapter(
      KeycloakSession session, RealmModel realm, ComponentModel model, String username) {
    super(session, realm, model);

    String aRandomId = UUID.randomUUID().toString();
    this.keycloakId = StorageId.keycloakId(model, aRandomId);
    this.username = username;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public void setUsername(String s) {
    throw new UnsupportedOperationException("This federation is read-only");
  }

  /* ********************************************************************************
   *                                ROLE METHODS
   * If you need some more specific methods, they're present in the superclass.
   * Check which method you can override from the parent class
   ******************************************************************************** */

  /**
   * This could override the current roles, and returns ALL the roles presents in the keycloak
   * realm. Example implementation to do that: return
   * this.realm.getRolesStream().collect(Collectors.toSet());
   */
  @Override
  protected Set<RoleModel> getRoleMappingsInternal() {
    // NoOp
    Set<RoleModel> roleMappingsInternal = super.getRoleMappingsInternal();
    var mutableSet = new HashSet<>(roleMappingsInternal);

    // Da Rimuovere, aggiungo tutti i ruoli del realm
    Set<RoleModel> inheritedRealmRoles = this.realm.getRolesStream().collect(Collectors.toSet());
    mutableSet.addAll(inheritedRealmRoles);
    // .Da Rimuovere

    mutableSet.add(new StoopidRoleAdapter("MAESTÀ", this.realm));

    return mutableSet;
  }

  @Override
  public Stream<RoleModel> getRealmRoleMappingsStream() {
    // NoOp
    return super.getRealmRoleMappingsStream();
  }

  @Override
  public Stream<RoleModel> getClientRoleMappingsStream(ClientModel app) {
    // NoOp
    return super.getClientRoleMappingsStream(app);
  }

  @Override
  public boolean hasRole(RoleModel role) {
    // NoOp
    return super.hasRole(role);
  }

  @Override
  protected Stream<RoleModel> getFederatedRoleMappingsStream() {
    // NoOp
    return super.getFederatedRoleMappingsStream();
  }

  /**
   * Appends (Keycloak's) default roles to the InnenUser loaded from the external DB. Javadoc says
   * to return true if we're not handling default roles from our custom DB. <a
   * href="https://keycloak.discourse.group/t/custom-user-storage-spi-with-groups-and-roles/1750">Info</a>
   */
  @Override
  protected boolean appendDefaultRolesToRoleMappings() {
    log.info("Appending default roles to the External user");
    return true;
  }

  /**
   * Appends (Keycloak's) default roles to the InnenUser loaded from the external DB. JavaDoc says
   * to return true if we're not handling groups from our custom DB
   */
  @Override
  protected boolean appendDefaultGroups() {
    return true;
  }

  /** Other UserModel basic attributes, showed in the KC web console */
  @Override
  public String getFirstName() {
    return "Christian";
  }

  @Override
  public String getLastName() {
    return "Luzzetti";
  }

  @Override
  public String getEmail() {
    return "c.luzzetti@innovationengineering.eu";
  }

  @Override
  public boolean isEmailVerified() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Long getCreatedTimestamp() {
    LocalDateTime aCasaccio = LocalDateTime.of(1989, 5, 8, 10, 00, 00);
    return aCasaccio.toInstant(ZoneOffset.UTC).toEpochMilli();
  }

  /* ********************************************************************************
   *                                CUSTOM ATTRIBUTES
   * Here we'll handle the custom attributes that will go on keycloak.
   * These are used from our custom kc's MAPPERS
   ******************************************************************************** */

  @Override
  public Map<String, List<String>> getAttributes() {
    Map<String, List<String>> defaultAttributes = super.getAttributes();
    defaultAttributes.put("LUOGO-DI-NASCITA", Collections.singletonList("Viterbo"));
    return defaultAttributes;
  }
}
