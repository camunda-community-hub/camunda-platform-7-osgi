package org.camunda.bpm.extension.osgi;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Bundle;

public class ProcessDefinitionCheckerImplTest {

	@Test
	public void checkBundleWithoutProcesses() {
		ProcessDefinitionDeployer deployer = mock(ProcessDefinitionDeployer.class);
		ProcessDefinitionCheckerImpl checker = new ProcessDefinitionCheckerImpl(
				deployer);
		Bundle bundle = mock(Bundle.class);
		when(bundle.getEntry(anyString())).thenReturn(null);
		when(bundle.getHeaders()).thenReturn(new Properties());
		checker.checkBundle(bundle);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void checkBundleWithProcesses() throws MalformedURLException {
		ProcessDefinitionDeployer deployer = mock(ProcessDefinitionDeployer.class);
		ProcessDefinitionCheckerImpl checker = new ProcessDefinitionCheckerImpl(
				deployer);
		Bundle bundle = mock(Bundle.class);
		URL url = new URL("file://foo.bpmn");
		when(bundle.findEntries(anyString(), anyString(), anyBoolean())).thenReturn(Collections.enumeration(Collections.singletonList(url)));
		when(bundle.getHeaders()).thenReturn(new Properties());
		checker.checkBundle(bundle);
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(deployer).deployProcessDefinitions(eq(bundle), captor.capture());
		assertThat((URL) captor.getValue().get(0), is(url));
	}
}
