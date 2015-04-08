package org.camunda.bpm.extension.osgi.eventing.impl;


import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.osgi.eventing.api.Topics;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ronny Bräunlich
 */
public class OSGiEventDistributor implements TaskListener, ExecutionListener, Serializable {

  private static final long serialVersionUID = -3778622638807349820L;
  
  private EventAdmin eventAdmin;

  public OSGiEventDistributor(EventAdmin eventAdmin) {
    this.eventAdmin = eventAdmin;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception {
    Event event = createEvent(execution);
    eventAdmin.postEvent(event);
  }


  @Override
  public void notify(DelegateTask delegateTask) {
    Event event = createEvent(delegateTask);
    eventAdmin.postEvent(event);
  }

  private Event createEvent(DelegateTask delegateTask) {
    Dictionary<String, String> properties = new Hashtable<String, String>();
    BusinessProcessEventPropertiesFiller.fillDictionary(properties, delegateTask);
    return new Event(Topics.TASK_EVENT_TOPIC, properties);
  }

  private Event createEvent(DelegateExecution execution) {
    Dictionary<String, String> properties = new Hashtable<String, String>();
    BusinessProcessEventPropertiesFiller.fillDictionary(properties, execution);
    return new Event(Topics.EXECUTION_EVENT_TOPIC, properties);
  }
}
