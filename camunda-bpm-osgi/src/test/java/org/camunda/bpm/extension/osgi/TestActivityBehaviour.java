package org.camunda.bpm.extension.osgi;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

public class TestActivityBehaviour implements ActivityBehavior {

	private boolean called;

	@Override
	public void execute(ActivityExecution execution) throws Exception {
		called = true;
	}
	
	public boolean getCalled(){
		return called;
	}

}
