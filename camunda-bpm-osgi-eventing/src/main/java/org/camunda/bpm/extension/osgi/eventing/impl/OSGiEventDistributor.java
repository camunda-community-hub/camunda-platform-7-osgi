package org.camunda.bpm.extension.osgi.eventing.impl;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.extension.osgi.eventing.BusinessProcessEventProperties;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author Ronny Bräunlich
 */
public class OSGiEventDistributor implements TaskListener, ExecutionListener, Serializable {

		private volatile EventAdmin eventAdmin;

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
				BusinessProcessEventProperties.fillDictionary(properties, delegateTask);
				return new Event(Task.class.getName().replace('.', '/'), properties);
		}

		private Event createEvent(DelegateExecution execution) {
				Dictionary<String, String> properties = new Hashtable<String, String>();
				BusinessProcessEventProperties.fillDictionary(properties, execution);
				return new Event(Execution.class.getName().replace('.', '/'), properties);
		}
}
