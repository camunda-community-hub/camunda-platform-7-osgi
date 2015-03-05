package org.camunda.bpm.extension.osgi.eventing;

import org.camunda.bpm.extension.osgi.eventing.api.BusinessProcessEventProperties;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ronny Bräunlich
 */
public class TestEventHandler implements EventHandler {

  private boolean called = false;

  @Override
  public void handleEvent(Event event) {
    this.called = true;
    assertThat((String) event.getProperty(BusinessProcessEventProperties.PROCESS_DEFINITION), is("Process_1"));
  }

  public boolean isCalled() {
    return called;
  }
}
