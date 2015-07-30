package org.camunda.bpm.extension.osgi.container.deployment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.camunda.bpm.container.impl.metadata.spi.ProcessEngineXml;
import org.junit.Test;
import org.osgi.framework.BundleContext;

public class OSGiProcessesXmlStartProcessEnginesStepTest {
  @Test
  public void createStartProcessEngineStep() {
    BundleContext context = mock(BundleContext.class);
    OSGiProcessesXmlStartProcessEnginesStep step = new OSGiProcessesXmlStartProcessEnginesStep(context);
    OSGiStartProcessEngineStep engineStep = (OSGiStartProcessEngineStep) step.createStartProcessEngineStep(mock(ProcessEngineXml.class));
    assertThat(engineStep.context, is(context));
  }
}
