package org.camunda.bpm.extension.osgi.eventing.api;

/**
 * This enum contains the constants for the topics of the OSGi events that will
 * be published. The constants have to be used for the EventHandler as value for
 * org.osgi.service.event.EventConstants.EVENT_TOPIC.
 * 
 * @author Ronny Bräunlich
 *
 */
public enum Topics {
  ;

  public static final String TASK_EVENT_TOPIC = "org/camunda/bpm/extension/osgi/eventing/TaskEvent";

  public static final String EXECUTION_EVENT_TOPIC = "org/camunda/bpm/extension/osgi/eventing/ExecutionEvent";
}
