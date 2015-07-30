package org.camunda.bpm.extension.osgi.eventing.api;

/**
 * This enum contains constants for the properties that can appear inside an event dictionary.
 *
 * @author Ronny Bräunlich
 */
public enum BusinessProcessEventProperties {

		;
		/**
		 * the id of the process definition in which the event is happening / has
		 * happened or null the event was not related to a process definition
		 */
		public static final String PROCESS_DEFINITION = "processDefinitionId";
		/**
		 * the id of the activity the process is currently in / was in at the
		 * moment the event was fired.
		 */
		public static final String ACTIVITY_ID = "activityId";
		/**
		 * the id of the transition being taken / that was taken
		 */
		public static final String TRANSITION_ID = "transitionId";
		/**
		 * the id of the {@link org.camunda.bpm.engine.runtime.ProcessInstance} this event corresponds to
		 */
		public static final String PROCESS_INSTANCE_ID = "processInstanceId";
		/**
		 * the id of the {@link org.camunda.bpm.engine.runtime.Execution} this event corresponds to
		 */
		public static final String EXECUTION_ID = "executionId";
		/**
		 * the type of the event, one of the constants in {@link org.camunda.bpm.engine.delegate.TaskListener} or {@link org.camunda.bpm.engine.delegate.ExecutionListener}
		 */
		public static final String TYPE = "type";
		/**
		 * the timestamp indicating the local time at which the event was
		 * fired.
		 */
		public static final String TIMESTAMP = "timestamp";
		/**
		 * the task id of the current task or null if this is not a task event.
		 */
		public static final String TASK_ID = "taskId";

		/**
		 * the id of the task in the process definition (BPMN XML) or null if this is not a task event.
		 */
		public static final String TASK_DEFINITION_KEY = "taskDefinitionKey";

}
