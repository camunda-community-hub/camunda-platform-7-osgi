package org.camunda.bpm.extension.osgi.container;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.container.impl.deployment.Attachments;
import org.camunda.bpm.container.impl.deployment.DeployProcessArchivesStep;
import org.camunda.bpm.container.impl.deployment.PostDeployInvocationStep;
import org.camunda.bpm.container.impl.deployment.StartProcessApplicationServiceStep;
import org.camunda.bpm.container.impl.spi.DeploymentOperation.DeploymentOperationBuilder;
import org.camunda.bpm.container.impl.spi.DeploymentOperationStep;
import org.camunda.bpm.container.impl.spi.PlatformServiceContainer;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.extension.osgi.container.deployment.OSGiParseProcessesXmlStep;
import org.camunda.bpm.extension.osgi.container.deployment.OSGiProcessesXmlStartProcessEnginesStep;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

public class OSGiRuntimeContainerDelegateTest {

  private MyTestDeploymentBuilder builder = new MyTestDeploymentBuilder();

  @Before
  public void setUp() {

  }

  @Test(expected = ProcessEngineException.class)
  public void deployNullProcessApplication() {
    new TestOSGiRuntimeContainerDelegate(null).deployProcessApplication(null);
  }

  @Test
  public void deployProcessApplication() {
    AbstractProcessApplication app = mock(AbstractProcessApplication.class);
    new TestOSGiRuntimeContainerDelegate(null).deployProcessApplication(app);
    Map<String, Object> attachments = builder.getAttachments();
    Set<Entry<String, Object>> entrySet = attachments.entrySet();
    assertThat(entrySet.size(), is(1));
    Entry<String, Object> entry = entrySet.iterator().next();
    assertThat(entry.getKey(), is(Attachments.PROCESS_APPLICATION));
    assertThat((AbstractProcessApplication) entry.getValue(), is(theInstance(app)));

    List<DeploymentOperationStep> steps = builder.getSteps();
    for (DeploymentOperationStep step : steps) {
      assertThat(
          step,
          is(anyOf(instanceOf(OSGiParseProcessesXmlStep.class), instanceOf(OSGiProcessesXmlStartProcessEnginesStep.class),
              instanceOf(DeployProcessArchivesStep.class), instanceOf(StartProcessApplicationServiceStep.class), instanceOf(PostDeployInvocationStep.class))));
    }
  }

  private static class MyTestDeploymentBuilder extends DeploymentOperationBuilder {

    public MyTestDeploymentBuilder() {
      super(mock(PlatformServiceContainer.class), "foo");
    }

    Map<String, Object> getAttachments() {
      return initialAttachments;
    }

    List<DeploymentOperationStep> getSteps() {
      return steps;
    }
  }

  private class TestOSGiRuntimeContainerDelegate extends OSGiRuntimeContainerDelegate {

    public TestOSGiRuntimeContainerDelegate(BundleContext context) {
      super(context);
    }

    @Override
    public PlatformServiceContainer getServiceContainer() {
      PlatformServiceContainer mock = mock(PlatformServiceContainer.class);
      when(mock.createDeploymentOperation(anyString())).thenReturn(builder);
      return mock;
    }

  }
}
