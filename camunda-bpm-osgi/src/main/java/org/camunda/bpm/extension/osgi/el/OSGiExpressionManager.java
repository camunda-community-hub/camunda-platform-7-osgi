package org.camunda.bpm.extension.osgi.el;

import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.VariableScopeElResolver;
import org.camunda.bpm.engine.impl.javax.el.ArrayELResolver;
import org.camunda.bpm.engine.impl.javax.el.CompositeELResolver;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.engine.impl.javax.el.ListELResolver;
import org.camunda.bpm.engine.impl.javax.el.MapELResolver;

public class OSGiExpressionManager extends ExpressionManager {
	@Override
	protected ELResolver createElResolver(VariableScope variableScope) {
		CompositeELResolver compositeElResolver = new CompositeELResolver();
		compositeElResolver.add(new VariableScopeElResolver(variableScope));
		compositeElResolver.add(new OSGiELResolver());
		compositeElResolver.add(new ArrayELResolver());
		compositeElResolver.add(new ListELResolver());
		compositeElResolver.add(new MapELResolver());
		return compositeElResolver;
	}
}
