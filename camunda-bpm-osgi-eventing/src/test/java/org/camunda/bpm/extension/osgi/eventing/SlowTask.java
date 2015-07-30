package org.camunda.bpm.extension.osgi.eventing;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.concurrent.TimeUnit;

/**
 * @author Ronny Bräunlich
 */
public class SlowTask implements JavaDelegate {
  @Override
  public void execute(DelegateExecution execution) throws Exception {
    Thread.sleep(TimeUnit.MILLISECONDS.convert(5L, TimeUnit.SECONDS));
  }
}
