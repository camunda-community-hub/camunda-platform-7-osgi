package org.camunda.bpm.extension.osgi.el;

public class TestJavaBean {

	private Object value;
	
	private boolean called = false;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isCalled() {
		return called;
	}

	public void setCalled(boolean called) {
		this.called = called;
	}
	
	
	
}
