package org.camunda.bpm.extension.osgi.container.deployment;

import java.net.URL;
import java.util.Map;

import org.camunda.bpm.application.impl.metadata.spi.ProcessArchiveXml;
import org.camunda.bpm.container.impl.deployment.DeployProcessArchiveStep;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;

/**
 * 
 * @author Ronny Bräunlich
 * 
 */
public class OSGiDeployProcessArchiveStep extends DeployProcessArchiveStep {

  public OSGiDeployProcessArchiveStep(ProcessArchiveXml parsedProcessArchive, URL url) {
    super(parsedProcessArchive, url);
  }

  @Override
  protected Map<String, byte[]> findResources(ClassLoader processApplicationClassloader, String paResourceRoot, String[] additionalResourceSuffixes) {
    return new OSGiProcessApplicationScanner(((BundleDelegatingClassLoader) processApplicationClassloader).getBundle()).findResources(
        processApplicationClassloader, paResourceRoot, metaFileUrl, additionalResourceSuffixes);
  }

}
