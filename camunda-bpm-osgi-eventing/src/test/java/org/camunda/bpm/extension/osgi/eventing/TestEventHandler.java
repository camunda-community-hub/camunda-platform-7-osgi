package org.camunda.bpm.extension.osgi.eventing;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Ronny Bräunlich
 */
public class TestEventHandler implements EventHandler {

		private boolean called = false;

		@Override
		public void handleEvent(Event event) {
			this.called = true;
		}

		public boolean isCalled(){
				return called;
		}
}
