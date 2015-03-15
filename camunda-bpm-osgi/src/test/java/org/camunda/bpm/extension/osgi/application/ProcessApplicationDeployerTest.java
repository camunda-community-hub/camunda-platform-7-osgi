package org.camunda.bpm.extension.osgi.application;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.application.ProcessApplicationInterface;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ProcessApplicationDeployerTest {

  @SuppressWarnings("unchecked")
  @Test
  public void addingService() {
    BundleContext bundleContext = mock(BundleContext.class);
    ServiceReference<ProcessApplicationInterface> ref = mock(ServiceReference.class);
    Bundle bundle = mock(Bundle.class);
    when(ref.getBundle()).thenReturn(bundle);
    when(bundle.getBundleContext()).thenReturn(bundleContext);
    ProcessApplicationInterface app = mock(ProcessApplicationInterface.class);
    when(bundleContext.getService(ref)).thenReturn(app);
    new ProcessApplicationDeployer().addingService(ref);
    verify(app, atLeastOnce()).deploy();
  }
  
  @Test
  public void removedService(){
    ProcessApplicationDeployer deployer = new ProcessApplicationDeployer();
    ProcessApplicationInterface app = mock(ProcessApplicationInterface.class);
    deployer.removedService(null, app);
    verify(app, atLeastOnce()).undeploy();
  }
}
