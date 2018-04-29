package org.camunda.bpm.extension.osgi.itest.el;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

public class JustAnotherTaskListener implements TaskListener {

  public boolean called = false;

  @Override
  public void notify(DelegateTask delegateTask) {
    this.called = true;
  }
}
