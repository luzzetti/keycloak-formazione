package it.luzzetti.lab.keycloak.configs.providers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.log4j.Log4j2;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.common.profile.PropertiesFileProfileConfigResolver;
import org.keycloak.common.profile.PropertiesProfileConfigResolver;
import org.keycloak.platform.PlatformProvider;

/**
 * Occhio dentro la cartella /resources/META-INF/services/ c'è un file chiamato
 * org.keycloak.platform.PlatformProvider
 */
@Log4j2
public class SimplePlatformProvider implements PlatformProvider {

  private File tmpDir;
  private Runnable shutdownHook;

  public SimplePlatformProvider() {
    Profile.configure(
        new PropertiesProfileConfigResolver(System.getProperties()),
        new PropertiesFileProfileConfigResolver());
  }

  @Override
  public String name() {
    return "springBootPlatform";
  }

  @Override
  public void onStartup(Runnable startupHook) {
    startupHook.run();
  }

  @Override
  public void onShutdown(Runnable shutdownHook) {
    //    this.shutdownHook = shutdownHook;
  }

  @Override
  public void exit(Throwable cause) {
    log.fatal("Error during startup!", cause);
    exit(1);
  }

  private void exit(int status) {
    new Thread(() -> System.exit(status)).start();
  }

  @Override
  public File getTmpDirectory() {
    if (tmpDir == null) {
      final var projectBuildDir = System.getProperty("project.build.directory");
      File tmpDir;
      if (projectBuildDir != null) {
        tmpDir = new File(projectBuildDir, "server-tmp");
        tmpDir.mkdir();
      } else {
        try {
          tmpDir = Files.createTempDirectory("keycloak-server-").toFile();
          tmpDir.deleteOnExit();
        } catch (IOException ioe) {
          throw new RuntimeException("Could not create temporary directory", ioe);
        }
      }
      if (tmpDir.isDirectory()) {
        this.tmpDir = tmpDir;
        log.info("Using server tmp directory: {}", tmpDir.getAbsolutePath());
      } else {
        throw new RuntimeException("Directory " + tmpDir + " was not created and does not exists");
      }
    }
    return tmpDir;
  }

  @Override
  public ClassLoader getScriptEngineClassLoader(Config.Scope scriptProviderConfig) {
    return null;
  }
}
