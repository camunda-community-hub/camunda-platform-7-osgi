package org.camunda.bpm.extension.osgi.eventing;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;

import java.util.Date;
import java.util.Dictionary;

/**
 * @author Ronny Bräunlich
 */
public enum BusinessProcessEventProperties {
		/**
		 * the id of the process definition in which the event is happening / has
		 * happened or null the event was not related to a process definition
		 */
		PROCESS_DEFINITION("processDefinitionId") {
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
		 * the id of the activity the process is currently in / was in at the
		 * moment the event was fired.
		 */
		ACTIVITY_ID("activityId") {
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
		TRANSITION_ID("transitionId") {
				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
						if (execution.getCurrentTransitionId() != null) {
								dictionary.put(getPropertyKey(), execution.getCurrentTransitionId());
						}
				}

				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
						dictionary.put(getPropertyKey(), task.getExecution().getCurrentTransitionId());
				}
		},
		/**
		 * the id of the {@link org.camunda.bpm.engine.runtime.ProcessInstance} this event corresponds to
		 */
		PROCESS_INSTANCE_ID("processInstanceId") {
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
		 * the id of the {@link org.camunda.bpm.engine.runtime.Execution} this event corresponds to
		 */
		EXECUTION_ID("executionId") {
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
		 * the type of the event, one of the constants in {@link org.camunda.bpm.engine.delegate.TaskListener} or {@link org.camunda.bpm.engine.delegate.ExecutionListener}
		 */
		TYPE("type") {
				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {

				}

				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {

				}
		},
		/**
		 * the timestamp indicating the local time at which the event was
		 * fired.
		 */
		TIMESTAMP("timestamp") {
				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
						dictionary.put(getPropertyKey(), new Date().toString());
				}

				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
						dictionary.put(getPropertyKey(), new Date().toString());
				}
		},
		/**
		 * the task id of the current task or null if this is not a task event.
		 */
		TASK_ID("taskId") {
				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
						dictionary.put(getPropertyKey(), task.getId());
				}
		},
		/**
		 * the id of the task in the process definition (BPMN XML) or null if this is not a task event.
		 */
		TASK_DEFINITION_KEY("taskDefinitionKey") {
				@Override
				void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
						dictionary.put(getPropertyKey(), task.getTaskDefinitionKey());
				}
		};

		private final String propertyKey;


		BusinessProcessEventProperties(String propertyKey) {
				this.propertyKey = propertyKey;
		}

		public String getPropertyKey() {
				return propertyKey;
		}

		void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {

		}

		void setValueIntoDictionary(Dictionary<String, String> dictionary, DelegateTask task) {

		}

		public static void fillDictionary(Dictionary<String, String> dictionary, DelegateExecution execution) {
				for (BusinessProcessEventProperties prop : values()) {
						prop.setValueIntoDictionary(dictionary, execution);
				}
		}

		public static void fillDictionary(Dictionary<String, String> dictionary, DelegateTask task) {
				for (BusinessProcessEventProperties prop : values()) {
						prop.setValueIntoDictionary(dictionary, task);
				}
		}

}
