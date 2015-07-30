package org.camunda.bpm.extension.osgi.eventing.api;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;

/**
 * This service can be used to activate the publication of OSGi events for the
 * transitions of a BPMN process.
 * 
 * @author Ronny Bräunlich
 */
public interface OSGiEventBridgeActivator extends BpmnParseListener {
}
