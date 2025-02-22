package it.luzzetti.lab.keycloak.configs.providers;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.common.util.ResteasyProvider;

/**
 * Occhio dentro la cartella /resources/META-INF/services/ c'è un file chiamato
 * org.jboss.resteasy.spi.ResteasyProvider
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Resteasy3Provider implements ResteasyProvider {

  @Override
  public <R> R getContextData(Class<R> type) {
    return ResteasyProviderFactory.getInstance().getContextData(type);
  }

  @Override
  public void pushDefaultContextObject(Class type, Object instance) {
    ResteasyProviderFactory.getInstance()
        .getContextData(Dispatcher.class)
        .getDefaultContextObjects()
        .put(type, instance);
  }

  @Override
  public void pushContext(Class type, Object instance) {
    ResteasyContext.pushContext(type, instance);
  }

  @Override
  public void clearContextData() {
    ResteasyContext.clearContextData();
  }
}
