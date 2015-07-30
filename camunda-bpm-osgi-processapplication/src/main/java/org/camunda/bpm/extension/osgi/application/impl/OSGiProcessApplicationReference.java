package org.camunda.bpm.extension.osgi.application.impl;

import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;

public class OSGiProcessApplicationReference implements ProcessApplicationReference {

  protected ProcessApplicationInterface application;
  protected String name;

  public OSGiProcessApplicationReference(ProcessApplicationInterface application, String name) {
    this.application = application;
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ProcessApplicationInterface getProcessApplication() throws ProcessApplicationUnavailableException {
    return application;
  }

}
