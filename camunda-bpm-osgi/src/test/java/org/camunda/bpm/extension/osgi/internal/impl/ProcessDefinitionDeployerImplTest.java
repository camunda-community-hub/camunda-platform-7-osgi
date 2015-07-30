package org.camunda.bpm.extension.osgi.internal.impl;

import static org.mockito.Matchers.any;
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

public class ProcessDefinitionDeployerImplTest {

	private DeploymentBuilder deploymentBuilder;

	@Test
	public void deployEmptyPathList() {
		ProcessDefinitionDeployer deployer = new ProcessDefinitionDeployerImpl(
				createProcessEngineMock());
		deployer.deployProcessDefinitions("test bundle",
				Collections.<URL> emptyList());
		verify(deploymentBuilder).deploy();
	}

	@Test
	public void deploySingleProcess() throws MalformedURLException {
		ProcessDefinitionDeployer deployer = new ProcessDefinitionDeployerImpl(
				createProcessEngineMock());
		URL url = new File("src/test/resources/testprocess.bpmn").toURI()
				.toURL();
		deployer.deployProcessDefinitions("test bundle",
				Collections.singletonList(url));
		verify(deploymentBuilder).deploy();
		verify(deploymentBuilder).addInputStream(eq(url.toString()),
				any(InputStream.class));
	}

	private ProcessEngine createProcessEngineMock() {
		ProcessEngine processEngine = mock(ProcessEngine.class);
		RepositoryService repositoryService = mock(RepositoryService.class);
		when(processEngine.getRepositoryService())
				.thenReturn(repositoryService);
		deploymentBuilder = mock(DeploymentBuilder.class);
		when(repositoryService.createDeployment())
				.thenReturn(deploymentBuilder);
		return processEngine;
	}

	@Test
	public void throwsExceptionWhenProcessEngineNotFound() {
		ProcessDefinitionDeployer deployer = new ProcessDefinitionDeployerImpl(
				null);
		deployer.deployProcessDefinitions("test bundle", null);
		// nothing should happen because the exception get's caught
	}

}
