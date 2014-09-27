package org.camunda.bpm.extension.osgi.container.deployment;

import java.net.URL;

import org.camunda.bpm.application.impl.metadata.spi.ProcessArchiveXml;
import org.camunda.bpm.container.impl.deployment.DeployProcessArchiveStep;
import org.camunda.bpm.container.impl.deployment.DeployProcessArchivesStep;

public class OSGiDeployProcessArchivesStep extends DeployProcessArchivesStep {


  @Override
  protected DeployProcessArchiveStep createDeployProcessArchiveStep(ProcessArchiveXml parsedProcessArchive, URL url) {
    return new OSGiDeployProcessArchiveStep( parsedProcessArchive, url);
  }

}
