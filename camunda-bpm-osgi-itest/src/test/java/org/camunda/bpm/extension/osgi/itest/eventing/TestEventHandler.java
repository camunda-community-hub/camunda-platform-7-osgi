package org.camunda.bpm.extension.osgi.itest.eventing;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.osgi.eventing.api.BusinessProcessEventProperties;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Ronny Bräunlich
 */
public class TestEventHandler implements EventHandler {

  private boolean called = false;
  private boolean endCalled = false;


  @Override
  public void handleEvent(Event event) {
    this.called = true;
    if (event.getProperty(BusinessProcessEventProperties.TYPE) != null &&
      event.getProperty(BusinessProcessEventProperties.TYPE).equals(ExecutionListener.EVENTNAME_END)) {
      endCalled = true;
    }
  }

  public boolean isCalled() {
    return called;
  }

  public boolean endCalled() {
    return endCalled;
  }
}
