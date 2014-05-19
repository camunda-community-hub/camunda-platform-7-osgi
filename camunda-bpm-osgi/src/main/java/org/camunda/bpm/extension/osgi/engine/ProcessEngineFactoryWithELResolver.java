package org.camunda.bpm.extension.osgi.engine;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.scripting.BeansResolverFactory;
import org.camunda.bpm.engine.impl.scripting.ResolverFactory;
import org.camunda.bpm.engine.impl.scripting.ScriptBindingsFactory;
import org.camunda.bpm.engine.impl.scripting.VariableScopeResolverFactory;
import org.camunda.bpm.extension.osgi.scripting.impl.OsgiScriptingEngines;


public class ProcessEngineFactoryWithELResolver extends ProcessEngineFactory {

    private ExpressionManager expressionManager;

    @Override
    public void init() {
      ProcessEngineConfigurationImpl configImpl = (ProcessEngineConfigurationImpl) getProcessEngineConfiguration();
      configImpl.setExpressionManager(expressionManager);
      
      List<ResolverFactory> resolverFactories = configImpl.getResolverFactories();
      if (resolverFactories == null) {
        resolverFactories = new ArrayList<ResolverFactory>();
        resolverFactories.add(new VariableScopeResolverFactory());
        resolverFactories.add(new BeansResolverFactory());
      }
      
      configImpl.setScriptingEngines(new OsgiScriptingEngines(new ScriptBindingsFactory(resolverFactories)));
      super.init();
    }

    public void setExpressionManager(ExpressionManager manager){
      this.expressionManager = manager;
    }
}
