package it.luzzetti.lab.keycloak.configs.providers.prototype;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;

/**
 * Eh... mi sono ispirato a:
 * https://github.com/dasniko/keycloak-extensions-demo/blob/1427dfbea8e74d8eb74ed0266aa7802e07728c96/flintstones-userprovider/src/main/java/dasniko/keycloak/user/flintstones/FlintstoneUserRoleModel.java
 */
@RequiredArgsConstructor
public class StoopidRoleAdapter implements RoleModel {

  private final String aRole;

  /**
   * This will be used to link the role to a specific realm. Check 'Realm-roles vs Client-roles' in
   * the docs
   */
  private final RealmModel realm;

  @Override
  public String getName() {
    return aRole;
  }

  @Override
  public String getDescription() {
    return aRole;
  }

  @Override
  public void setDescription(String description) {
    throw new UnsupportedOperationException("Not implemented in the Innen Person SPI");
  }

  @Override
  public String getId() {
    return aRole;
  }

  @Override
  public void setName(String name) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public boolean isComposite() {
    return false;
  }

  @Override
  public void addCompositeRole(RoleModel role) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void removeCompositeRole(RoleModel role) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Stream<RoleModel> getCompositesStream(String search, Integer first, Integer max) {
    return Stream.empty();
  }

  @Override
  public boolean isClientRole() {
    return false;
  }

  @Override
  public String getContainerId() {
    return realm.getId();
  }

  @Override
  public RoleContainerModel getContainer() {
    return realm;
  }

  @Override
  public boolean hasRole(RoleModel role) {
    return this.equals(role) || this.aRole.equals(role.getName());
  }

  @Override
  public void setSingleAttribute(String name, String value) {}

  @Override
  public void setAttribute(String name, List<String> values) {}

  @Override
  public void removeAttribute(String name) {}

  @Override
  public Stream<String> getAttributeStream(String name) {
    return Stream.empty();
  }

  @Override
  public Map<String, List<String>> getAttributes() {
    return Map.of();
  }
}
