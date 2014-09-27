package org.camunda.bpm.extension.osgi.container.deployment;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.camunda.bpm.extension.osgi.application.OSGiProcessApplication;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class OSGiParseProcessesXmlStepTest {
  @Test
  public void getProcessesXmlUrls() throws Exception {
    OSGiParseProcessesXmlStep step = new OSGiParseProcessesXmlStep();
    Bundle bundle = mock(Bundle.class);
    URL url = new URL("http://localhost");
    when(bundle.getResources(eq("foo"))).thenReturn(Collections.enumeration(Collections.singleton(url)));
    List<URL> urls = step.getProcessesXmlUrls(new String[]{"foo"}, new OSGiProcessApplication(bundle, null));
    assertThat(urls, hasItem(url));
  }
}
