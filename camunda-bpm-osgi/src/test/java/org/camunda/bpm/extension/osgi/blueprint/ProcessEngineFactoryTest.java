package org.camunda.bpm.extension.osgi.blueprint;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.extension.osgi.blueprint.BundleDelegatingClassLoader;
import org.camunda.bpm.extension.osgi.blueprint.ClassLoaderWrapper;
import org.camunda.bpm.extension.osgi.blueprint.ProcessEngineFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Bundle;

public class ProcessEngineFactoryTest {

	private ProcessEngineFactory factory;

	@Before
	public void setUp() {
		factory = new ProcessEngineFactory();
	}

	@Test
	public void initProcessEngine() throws Exception {
		// create mocks
		Bundle bundle = mock(Bundle.class);
		ProcessEngineConfiguration configuration = mock(ProcessEngineConfiguration.class);
		ProcessEngine engine = mock(ProcessEngine.class);
		// mock behaviour
		when(configuration.buildProcessEngine()).thenReturn(engine);
		ArgumentCaptor<ClassLoader> classLoaderCaptor = ArgumentCaptor
				.forClass(ClassLoader.class);
		// call methods
		factory.setBundle(bundle);
		factory.setProcessEngineConfiguration(configuration);
		factory.init();
		// checks
		verify(configuration).setClassLoader(classLoaderCaptor.capture());
		assertThat(classLoaderCaptor.getValue(),
				is(instanceOf(BundleDelegatingClassLoader.class)));
		BundleDelegatingClassLoader bundleDelCl = (BundleDelegatingClassLoader) classLoaderCaptor
				.getValue();
		assertThat(bundleDelCl.getBundle(), is(bundle));
		verify(configuration).buildProcessEngine();
		assertThat(Thread.currentThread().getContextClassLoader(),
				is(not(instanceOf(ClassLoaderWrapper.class))));
		assertThat(factory.getObject(), is(engine));
		assertThat(factory.getBundle(), is(bundle));
		assertThat(factory.getProcessEngineConfiguration(), is(configuration));
		factory.destroy();
		verify(engine).close();
	}

	@Test
	public void destroyWithoutEngineDoesntCauseException() throws Exception {
		factory.destroy();
	}
}
