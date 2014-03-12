package org.camunda.bpm.extension.osgi.blueprint;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.javax.el.CompositeELResolver;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.engine.impl.scripting.BeansResolverFactory;
import org.camunda.bpm.engine.impl.scripting.ResolverFactory;
import org.camunda.bpm.engine.impl.scripting.VariableScopeResolverFactory;
import org.camunda.bpm.extension.osgi.OsgiScriptingEngines;
import org.camunda.bpm.extension.osgi.blueprint.BlueprintELResolver;
import org.camunda.bpm.extension.osgi.blueprint.ProcessEngineFactoryWithELResolver;
import org.camunda.bpm.extension.osgi.blueprint.ProcessEngineFactoryWithELResolver.BlueprintExpressionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ProcessEngineFactoryWithELResolverTest {

	private ProcessEngineFactoryWithELResolver factory;

	@Before
	public void setUp() {
		factory = new ProcessEngineFactoryWithELResolver();
	}

	@Test
	public void initWithBlueprintELResolverAndWithoutResolverFactories()
			throws Exception {
		StandaloneProcessEngineConfiguration config = mock(StandaloneProcessEngineConfiguration.class);
		when(config.getResolverFactories()).thenReturn(null);
		factory.setProcessEngineConfiguration(config);
		factory.setBlueprintELResolver(new BlueprintELResolver());
		factory.init();
		// captures
		ArgumentCaptor<BlueprintExpressionManager> elManagerCaptor = ArgumentCaptor
				.forClass(BlueprintExpressionManager.class);
		verify(config).setExpressionManager(elManagerCaptor.capture());
		ArgumentCaptor<OsgiScriptingEngines> scriptCaptor = ArgumentCaptor
				.forClass(OsgiScriptingEngines.class);
		verify(config).setScriptingEngines(scriptCaptor.capture());
		// checks
		checkScriptingEngine(scriptCaptor.getValue());
		checkExpressionManager(elManagerCaptor.getValue());
	}

	private void checkScriptingEngine(OsgiScriptingEngines scriptingEngine) {
		assertThat(scriptingEngine, isA(OsgiScriptingEngines.class));
		assertThat(scriptingEngine.getScriptBindingsFactory()
				.getResolverFactories().size(), is(2));
		assertThat(
				scriptingEngine.getScriptBindingsFactory()
						.getResolverFactories(),
				allOf(hasItem(isA(VariableScopeResolverFactory.class)),
						hasItem(isA(BeansResolverFactory.class))));
	}

	private void checkExpressionManager(BlueprintExpressionManager exprManager) {
		ELResolver elResolver = exprManager.createElResolver(null);
		assertThat(elResolver, is(instanceOf(CompositeELResolver.class)));
		// FIXME gotta find a way to get hold of Resolvers inside
		// compositeResolver to check that the expected ones are set
	}

	@Test(expected = NullPointerException.class)
	public void initWithoutBlueprintELResolverCausesNPEInBlueprintExpressionManager()
			throws Exception {
		StandaloneProcessEngineConfiguration config = mock(StandaloneProcessEngineConfiguration.class);
		factory.setProcessEngineConfiguration(config);
		factory.init();
		// captures
		ArgumentCaptor<BlueprintExpressionManager> elManagerCaptor = ArgumentCaptor
				.forClass(BlueprintExpressionManager.class);
		verify(config).setExpressionManager(elManagerCaptor.capture());
		elManagerCaptor.getValue().createElResolver(null);
	}

	@Test
	public void initWithResolverFactories() throws Exception {
		StandaloneProcessEngineConfiguration config = mock(StandaloneProcessEngineConfiguration.class);
		ResolverFactory resolverFactory = mock(ResolverFactory.class);
		when(config.getResolverFactories()).thenReturn(
				Collections.singletonList(resolverFactory));
		factory.setProcessEngineConfiguration(config);
		factory.init();
		// captures
		ArgumentCaptor<OsgiScriptingEngines> scriptCaptor = ArgumentCaptor
				.forClass(OsgiScriptingEngines.class);
		verify(config).setScriptingEngines(scriptCaptor.capture());
		// checks
		assertThat(scriptCaptor.getValue().getScriptBindingsFactory()
				.getResolverFactories(), hasItem(resolverFactory));
	}
}
