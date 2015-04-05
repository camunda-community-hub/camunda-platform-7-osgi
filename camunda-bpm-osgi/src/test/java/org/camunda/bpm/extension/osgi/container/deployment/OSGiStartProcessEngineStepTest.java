package org.camunda.bpm.extension.osgi.container.deployment;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.camunda.bpm.container.impl.jmx.services.JmxManagedProcessEngineController;
import org.camunda.bpm.container.impl.metadata.spi.ProcessEngineXml;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.junit.Test;
import org.osgi.framework.BundleContext;

public class OSGiStartProcessEngineStepTest {
  @Test
  public void createProcessEngineControllerInstance() {
    OSGiStartProcessEngineStep step = new OSGiStartProcessEngineStep(mock(ProcessEngineXml.class), mock(BundleContext.class));
    JmxManagedProcessEngineController controller = step.createProcessEngineControllerInstance(new StandaloneInMemProcessEngineConfiguration());
    assertThat(controller, is(instanceOf(BundleClassloaderAwareProcessEngineController.class)));
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void loadClassWithCustomClassloader() throws ClassNotFoundException{
    OSGiStartProcessEngineStep step = new OSGiStartProcessEngineStep(mock(ProcessEngineXml.class), mock(BundleContext.class));
    ClassLoader classLoader = mock(ClassLoader.class);
    String clazz = "org.foo.Bar";
    when(classLoader.loadClass(clazz)).thenReturn((Class) this.getClass());
    Class<? extends Object> loadedClazz = step.loadClass(clazz, classLoader, null);
    assertThat(loadedClazz.getName(), is(this.getClass().getName()));
  }
  
  @Test
  public void loadClassWithNullCustomClassloader() throws ClassNotFoundException{
    OSGiStartProcessEngineStep step = new OSGiStartProcessEngineStep(mock(ProcessEngineXml.class), mock(BundleContext.class));
    String clazz = "java.lang.Object";
    Class<? extends Object> loadedClazz = step.loadClass(clazz, null, null);
    assertThat(loadedClazz.getName(), is(Object.class.getName()));
  }
  
  @Test
  public void loadClassWithExceptionInCustomClassloader() throws ClassNotFoundException{
    OSGiStartProcessEngineStep step = new OSGiStartProcessEngineStep(mock(ProcessEngineXml.class), mock(BundleContext.class));
    String clazz = "java.lang.Object";
    ClassLoader classLoader = mock(ClassLoader.class);
    when(classLoader.loadClass(clazz)).thenThrow(new ClassNotFoundException());
    Class<? extends Object> loadedClazz = step.loadClass(clazz, null, null);
    assertThat(loadedClazz.getName(), is(Object.class.getName()));
  }
}
