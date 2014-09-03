package org.camunda.bpm.extension.osgi.container.deployment;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import org.camunda.bpm.container.impl.spi.PlatformServiceContainer;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.blueprint.ProcessEngineFactory;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class BundleClassloaderAwareProcessEngineControllerTest {

  @Test
  public void start(){
    BundleContext context = mock(BundleContext.class);
    when(context.getBundle()).thenReturn(mock(Bundle.class));
    BundleClassloaderAwareProcessEngineController controller = new TestBundleClassloaderAwareProcessEngineController(new StandaloneInMemProcessEngineConfiguration(), context);
    controller.start(mock(PlatformServiceContainer.class));
    verify(context, atLeastOnce()).registerService(eq(ProcessEngine.class.getName()), any(ProcessEngine.class), any(Dictionary.class));
  }
  
  @Test
  public void stop(){
    BundleContext context = mock(BundleContext.class);
    when(context.getBundle()).thenReturn(mock(Bundle.class));
    TestBundleClassloaderAwareProcessEngineController controller = new TestBundleClassloaderAwareProcessEngineController(new StandaloneInMemProcessEngineConfiguration(), context);
    ServiceRegistration reg = mock(ServiceRegistration.class);
    controller.setRegistration(reg);
    controller.stop(null);
    verify(reg, atLeastOnce()).unregister();
    verify(controller.getFactory(), atLeastOnce()).destroy();
  }
  
  private static class TestBundleClassloaderAwareProcessEngineController extends BundleClassloaderAwareProcessEngineController{

    public TestBundleClassloaderAwareProcessEngineController(ProcessEngineConfiguration processEngineConfiguration, BundleContext context) {
      super(processEngineConfiguration, context);
      this.processEngineFactory = mock(ProcessEngineFactory.class);
      when(processEngineFactory.getObject()).thenReturn(mock(ProcessEngine.class));
    }
    
    void setRegistration(ServiceRegistration reg){
      this.registration = reg;
    }
    
    ProcessEngineFactory getFactory(){
      return processEngineFactory;
    }
  }
}
