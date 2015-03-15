package org.camunda.bpm.extension.osgi.internal.impl;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.extension.osgi.internal.ProcessDefinitionDeployer;
import org.junit.Test;
import org.osgi.util.tracker.ServiceTracker;

public class ProcessDefinitionDeployerImplTest {

	private DeploymentBuilder deploymentBuilder;

	@Test
	public void deployEmptyPathList() {
		ServiceTracker<ProcessEngine, ProcessEngine> serviceTracker = createProcessEngineServiceTrackerMock();
		ProcessDefinitionDeployer deployer = new ProcessDefinitionDeployerImpl(
				serviceTracker);
		deployer.deployProcessDefinitions("test bundle", Collections.<URL> emptyList());
		verify(deploymentBuilder).deploy();
	}

	@Test
	public void deploySingleProcess() throws MalformedURLException {
		ServiceTracker<ProcessEngine, ProcessEngine> serviceTracker = createProcessEngineServiceTrackerMock();
		ProcessDefinitionDeployer deployer = new ProcessDefinitionDeployerImpl(
				serviceTracker);
		URL url = new File("src/test/resources/testprocess.bpmn").toURI()
				.toURL();
		deployer.deployProcessDefinitions("test bundle",
				Collections.singletonList(url));
		verify(deploymentBuilder).deploy();
		verify(deploymentBuilder).addInputStream(eq(url.toString()),
				any(InputStream.class));
	}

	@SuppressWarnings("unchecked")
  private ServiceTracker<ProcessEngine, ProcessEngine> createProcessEngineServiceTrackerMock() {
		ServiceTracker<ProcessEngine, ProcessEngine> serviceTracker = mock(ServiceTracker.class);
		ProcessEngine processEngine = mock(ProcessEngine.class);
		RepositoryService repositoryService = mock(RepositoryService.class);
		when(processEngine.getRepositoryService())
				.thenReturn(repositoryService);
		deploymentBuilder = mock(DeploymentBuilder.class);
		when(repositoryService.createDeployment())
				.thenReturn(deploymentBuilder);
		try {
			when(serviceTracker.waitForService(anyLong())).thenReturn(
					processEngine);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		return serviceTracker;
	}

	@SuppressWarnings("unchecked")
  @Test
	public void throwsExceptionWhenProcessEngineNotFound() {
		ServiceTracker<ProcessEngine, ProcessEngine> serviceTracker = mock(ServiceTracker.class);
		try {
			when(serviceTracker.waitForService(anyLong())).thenReturn(null);
		} catch (InterruptedException e) {
			fail(e.toString());
		}
		ProcessDefinitionDeployer deployer = new ProcessDefinitionDeployerImpl(
				serviceTracker);
		deployer.deployProcessDefinitions("test bundle", null);
		// nothing should happen because the exception get's caught
	}

}
