package org.camunda.bpm.extension.osgi.container.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.container.impl.deployment.scanning.spi.ProcessApplicationScanner;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.bpmn.deployer.BpmnDeployer;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.osgi.framework.Bundle;

/**
 * 
 * @author Ronny Bräunlich
 * 
 */
public class OSGiProcessApplicationScanner implements ProcessApplicationScanner {

  private Bundle bundle;

  private static Logger log = Logger.getLogger(OSGiProcessApplicationScanner.class.getName());

  public OSGiProcessApplicationScanner(Bundle bundle) {
    this.bundle = bundle;
  }

  @Override
  public Map<String, byte[]> findResources(ClassLoader classLoader, String paResourceRootPath, URL metaFileUrl) {
    return findResources(classLoader, paResourceRootPath, metaFileUrl, null);
  }

  @Override
  public Map<String, byte[]> findResources(ClassLoader classLoader, String paResourceRootPath, URL metaFileUrl, String[] additionalResourceSuffixes) {
    final Map<String, byte[]> resourceMap = new HashMap<String, byte[]>();
    List<String> suffixes = Arrays.asList(BpmnDeployer.BPMN_RESOURCE_SUFFIXES);
    if (additionalResourceSuffixes != null) {
      suffixes.addAll(Arrays.asList(additionalResourceSuffixes));
    }
    if (paResourceRootPath != null && !paResourceRootPath.startsWith("pa:")) {
      // 1. CASE: paResourceRootPath specified AND it is a "classpath:" resource
      // root
      String strippedPath = paResourceRootPath.replace("classpath:", "");
      scanPathInBundle(suffixes, resourceMap, strippedPath);

    } else if (paResourceRootPath == null) {
      // 3. CASE: paResourceRootPath not specified
      scanPathInBundle(suffixes, resourceMap, "/");
    } else {
      // 2nd. CASE: paResourceRootPath is PA-local
      //FIXME fix this by using BundleWiring.listResources() when moving to OSGi 4.3
      throw new ProcessEngineException("PA-loca resourceRootPaths are not supported in an OSGi-environment");
    }
    return resourceMap;
  }

  private void scanPathInBundle(List<String> suffixes, final Map<String, byte[]> resourceMap, String strippedPath) {
    for (String suffix : suffixes) {
      Enumeration<URL> entries = bundle.findEntries(strippedPath, "*." + suffix, true);
      if (entries != null) {
        for (URL entry : Collections.list(entries)) {
          addResource(entry, resourceMap, strippedPath, entry.getPath());
        }
      }
    }
  }

  protected Enumeration<URL> loadClasspathResourceRoots(final ClassLoader classLoader, String strippedPaResourceRootPath) {
    Enumeration<URL> resourceRoots;
    try {
      resourceRoots = classLoader.getResources(strippedPaResourceRootPath);
    } catch (IOException e) {
      throw new ProcessEngineException("Could not load resources at '" + strippedPaResourceRootPath + "' using classloaded '" + classLoader + "'", e);
    }
    return resourceRoots;
  }

  protected void addResource(URL source, Map<String, byte[]> resourceMap, String resourceRootPath, String resourceName) {

    String resourcePath = (resourceRootPath == null ? "" : resourceRootPath).concat(resourceName);

    log.log(Level.FINEST, "discovered process resource {0}", resourcePath);

    InputStream inputStream = null;

    try {
      inputStream = source.openStream();
      byte[] bytes = IoUtil.readInputStream(inputStream, resourcePath);

      resourceMap.put(resourcePath, bytes);

    } catch (IOException e) {
      throw new ProcessEngineException("Could not open file for reading " + source + ". " + e.getMessage(), e);
    } finally {
      if (inputStream != null) {
        IoUtil.closeSilently(inputStream);
      }
    }
  }
}
