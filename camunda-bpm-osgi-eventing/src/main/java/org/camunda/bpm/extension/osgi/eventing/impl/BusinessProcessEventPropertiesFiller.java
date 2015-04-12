package org.camunda.bpm.extension.osgi.eventing.impl;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.extension.osgi.eventing.api.BusinessProcessEventProperties;

import java.util.Date;
import java.util.Dictionary;

/**
 * This enum defines all the possible properties that can be placed inside the
 * dictionary of a event.
 * 
 * @author Ronny Bräunlich
 */
public enum BusinessProcessEventPropertiesFiller {
  /**
   * the id of the process definition in which the event is happening / has
   * happened or null the event was not related to a process definition
   */
  PROCESS_DEFINITION(BusinessProcessEventProperties.PROCESS_DEFINITION) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      dictionary.put(this.getPropertyKey(), execution.getProcessDefinitionId());
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), task.getProcessDefinitionId());
    }
  },
  /**
   * the id of the activity the process is currently in / was in at the moment
   * the event was fired.
   */
  ACTIVITY_ID(BusinessProcessEventProperties.ACTIVITY_ID) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      dictionary.put(getPropertyKey(), execution.getCurrentActivityId());
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), task.getExecution().getCurrentActivityId());
    }
  },
  /**
   * the id of the transition being taken / that was taken
   */
  TRANSITION_ID(BusinessProcessEventProperties.TRANSITION_ID) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      if (execution.getCurrentTransitionId() != null) {
        dictionary.put(getPropertyKey(), execution.getCurrentTransitionId());
      }
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      if (task.getExecution().getCurrentTransitionId() != null) {
        dictionary.put(getPropertyKey(), task.getExecution().getCurrentTransitionId());
      }
    }
  },
  /**
   * the id of the {@link org.camunda.bpm.engine.runtime.ProcessInstance} this
   * event corresponds to
   */
  PROCESS_INSTANCE_ID(BusinessProcessEventProperties.PROCESS_INSTANCE_ID) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      dictionary.put(getPropertyKey(), execution.getProcessInstanceId());
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), task.getProcessInstanceId());
    }
  },
  /**
   * the id of the {@link org.camunda.bpm.engine.runtime.Execution} this event
   * corresponds to
   */
  EXECUTION_ID(BusinessProcessEventProperties.EXECUTION_ID) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      dictionary.put(getPropertyKey(), execution.getId());
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), task.getExecutionId());
    }
  },
  /**
   * the type of the event, one of the constants in
   * {@link org.camunda.bpm.engine.delegate.TaskListener} or
   * {@link org.camunda.bpm.engine.delegate.ExecutionListener}
   */
  TYPE(BusinessProcessEventProperties.TYPE) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      dictionary.put(getPropertyKey(), execution.getEventName());
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      if (task.getEventName() != null) {
        dictionary.put(getPropertyKey(), task.getEventName());
      }
    }
  },
  /**
   * the timestamp indicating the local time at which the event was fired.
   */
  TIMESTAMP(BusinessProcessEventProperties.TIMESTAMP) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
      dictionary.put(getPropertyKey(), String.valueOf(new Date().getTime()));
    }

    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), String.valueOf(new Date().getTime()));
    }
  },
  /**
   * the task id of the current task or null if this is not a task event.
   */
  TASK_ID(BusinessProcessEventProperties.TASK_ID) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), task.getId());
    }
  },
  /**
   * the id of the task in the process definition (BPMN XML) or null if this is
   * not a task event.
   */
  TASK_DEFINITION_KEY(BusinessProcessEventProperties.TASK_DEFINITION_KEY) {
    @Override
    void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
      dictionary.put(getPropertyKey(), task.getTaskDefinitionKey());
    }
  };

  private final String propertyKey;

  BusinessProcessEventPropertiesFiller(String propertyKey) {
    this.propertyKey = propertyKey;
  }

  public String getPropertyKey() {
    return propertyKey;
  }

  /**
   * 
   * @param dictionary
   *          the dictionary with the properties for the event
   * @param execution
   *          the execution that was executed
   */
  void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {

  }

  /**
   * 
   * @param dictionary
   *          the dictionary with the properties for the event
   * @param task
   *          the task that was executed
   */
  void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {

  }

  public static void fillDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
    for (BusinessProcessEventPropertiesFiller prop : values()) {
      prop.setValueIntoDictionary(dictionary, execution);
    }
  }

  public static void fillDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
    for (BusinessProcessEventPropertiesFiller prop : values()) {
      prop.setValueIntoDictionary(dictionary, task);
    }
  }

}
