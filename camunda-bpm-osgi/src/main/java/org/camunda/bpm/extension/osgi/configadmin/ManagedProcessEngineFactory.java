package org.camunda.bpm.extension.osgi.configadmin;

import org.osgi.service.cm.ManagedServiceFactory;

public interface ManagedProcessEngineFactory extends ManagedServiceFactory {

  public static final String SERVICE_PID = "org.camunda.bpm.extension.osgi.configadmin.ManagedProcessEngineFactory";

}
