package org.camunda.bpm.extension.osgi.eventing.impl;


import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateListener;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.core.model.CoreModelElement;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.extension.osgi.eventing.api.Topics;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ronny Bräunlich
 */
public class SelfDestructingOSGiEventDistributor implements TaskListener, ExecutionListener, SynchronousBundleListener, Serializable {

  private static final long serialVersionUID = -6407460127392050346L;
  /**
   * The task element this listener has been added to
   */
  private TaskDefinition taskDefinition;
  private EventAdmin eventAdmin;
  /**
   * The BPMN element this listener has been added to
   */
  private CoreModelElement element;

  public SelfDestructingOSGiEventDistributor(EventAdmin eventAdmin, CoreModelElement element) {
    this.eventAdmin = eventAdmin;
    this.element = element;
  }

  public SelfDestructingOSGiEventDistributor(EventAdmin eventAdmin, TaskDefinition taskDefinition) {
    this.eventAdmin = eventAdmin;
    this.taskDefinition = taskDefinition;
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

  @Override
  public void bundleChanged(BundleEvent event) {
    //just to be sure check the symbolic name
    if (event.getBundle().getSymbolicName().equals("org.camunda.bpm.extension.osgi.eventing") && event.getType() == BundleEvent.STOPPING) {
      // bundle is shutting down, self destruct
      eventAdmin = null;
      if (element != null) {
        Map<String, List<DelegateListener<?>>> listeners = element.getListeners();
        for (List<DelegateListener<?>> list : listeners.values()) {
          if (list.contains(this)) {
            list.remove(this);
          }
        }
      }
      if (taskDefinition != null) {
        Map<String, List<TaskListener>> listeners = taskDefinition.getTaskListeners();
        for (List<TaskListener> list : listeners.values()) {
          if (list.contains(this)) {
            list.remove(this);
          }
        }
      }
      element = null;
      taskDefinition = null;
    }
  }
}
