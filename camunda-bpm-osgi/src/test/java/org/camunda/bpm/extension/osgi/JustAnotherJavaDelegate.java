package org.camunda.bpm.extension.osgi;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class JustAnotherJavaDelegate implements JavaDelegate {

	public boolean called = false;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		called = true;
	}
	
	public void toggleCalled(){
		called = true;
	}
	
}
