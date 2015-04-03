package org.camunda.bpm.extension.osgi.fileinstall;

import java.util.Collections;
import java.util.Hashtable;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.fileinstall.ArtifactListener;
import org.apache.felix.fileinstall.ArtifactUrlTransformer;
import org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnDeploymentListener;
import org.camunda.bpm.extension.osgi.fileinstall.impl.BpmnURLHandler;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLStreamHandlerService;

public class Activator extends DependencyActivatorBase {

  @Override
  public void init(BundleContext context, DependencyManager manager) throws Exception {
    manager.add(createComponent().setInterface(new String[] { ArtifactUrlTransformer.class.getName(), ArtifactListener.class.getName() }, null)
        .setImplementation(BpmnDeploymentListener.class));
    manager.add(createComponent().setInterface(URLStreamHandlerService.class.getName(),
        new Hashtable<String, String>(Collections.singletonMap("url.handler.protocol", "bpmn"))).setImplementation(BpmnURLHandler.class));
  }

}
