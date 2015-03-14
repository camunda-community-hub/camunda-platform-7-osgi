package org.camunda.bpm.extension.osgi.eventing.impl;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.core.model.CoreModelElement;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.impl.variable.VariableDeclaration;
import org.camunda.bpm.extension.osgi.eventing.api.OSGiEventBridgeActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author Ronny Bräunlich
 */
public class GlobalOSGiEventBridgeActivator extends AbstractBpmnParseListener implements OSGiEventBridgeActivator {

  private volatile EventAdmin eventAdmin;

  private volatile Bundle bundle;

  protected void addEndEventListener(ActivityImpl activity) {
    activity.addExecutionListener(ExecutionListener.EVENTNAME_END, createExecutionListener(activity));
  }

  protected void addStartEventListener(ActivityImpl activity) {
    activity.addExecutionListener(ExecutionListener.EVENTNAME_START, createExecutionListener(activity));
  }

  protected void addTakeEventListener(TransitionImpl transition) {
    transition.addExecutionListener(createExecutionListener(transition));
  }

  protected void addTaskAssignmentListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_ASSIGNMENT, createTaskListener(taskDefinition));
  }

  protected void addTaskCreateListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, createTaskListener(taskDefinition));
  }

  protected void addTaskCompleteListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, createTaskListener(taskDefinition));
  }

  protected void addTaskDeleteListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_DELETE, createTaskListener(taskDefinition));
  }

  private TaskListener createTaskListener(TaskDefinition taskDefinition) {
    SelfDestructingOSGiEventDistributor distributor = new SelfDestructingOSGiEventDistributor(eventAdmin, taskDefinition);
    bundle.getBundleContext().addBundleListener(distributor);
    return distributor;
//    return (TaskListener) Proxy.newProxyInstance(taskDefinition.getClass().getClassLoader(), new Class[]{TaskListener.class}, new EventDistributorHandler(bundle.getBundleContext()));
  }

  private ExecutionListener createExecutionListener(CoreModelElement activity) {
    SelfDestructingOSGiEventDistributor distributor = new SelfDestructingOSGiEventDistributor(eventAdmin, activity);
    bundle.getBundleContext().addBundleListener(distributor);
    return distributor;
//    return (ExecutionListener) Proxy.newProxyInstance(activity.getClass().getClassLoader(), new Class[]{ExecutionListener.class}, new EventDistributorHandler(bundle.getBundleContext()));
  }

  // BpmnParseListener implementation /////////////////////////////////////////////////////////
  @Override
  public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
  }

  @Override
  public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl startEventActivity) {
    addStartEventListener(startEventActivity);
    addEndEventListener(startEventActivity);
  }

  @Override
  public void parseExclusiveGateway(Element exclusiveGwElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseInclusiveGateway(Element inclusiveGwElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseParallelGateway(Element parallelGwElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseScriptTask(Element scriptTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseServiceTask(Element serviceTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseBusinessRuleTask(Element businessRuleTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseTask(Element taskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseManualTask(Element manualTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
    UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) activity.getActivityBehavior();
    TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
    addTaskCreateListeners(taskDefinition);
    addTaskAssignmentListeners(taskDefinition);
    addTaskCompleteListeners(taskDefinition);
    addTaskDeleteListeners(taskDefinition);
  }

  @Override
  public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseBoundaryTimerEventDefinition(Element timerEventDefinition, boolean interrupting, ActivityImpl timerActivity) {
// start and end event listener are set by parseBoundaryEvent()
  }

  @Override
  public void parseBoundaryErrorEventDefinition(Element errorEventDefinition, boolean interrupting, ActivityImpl activity, ActivityImpl nestedErrorEventActivity) {
// start and end event listener are set by parseBoundaryEvent()
  }

  @Override
  public void parseSubProcess(Element subProcessElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseCallActivity(Element callActivityElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseProperty(Element propertyElement, VariableDeclaration variableDeclaration, ActivityImpl activity) {
  }

  @Override
  public void parseSequenceFlow(Element sequenceFlowElement, ScopeImpl scopeElement, TransitionImpl transition) {
    addTakeEventListener(transition);
  }

  @Override
  public void parseSendTask(Element sendTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseMultiInstanceLoopCharacteristics(Element activityElement, Element multiInstanceLoopCharacteristicsElement, ActivityImpl activity) {
  }

  @Override
  public void parseRootElement(Element rootElement, List<ProcessDefinitionEntity> processDefinitions) {
  }

  @Override
  public void parseIntermediateTimerEventDefinition(Element timerEventDefinition, ActivityImpl timerActivity) {
    addStartEventListener(timerActivity);
    addEndEventListener(timerActivity);
  }

  @Override
  public void parseReceiveTask(Element receiveTaskElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseIntermediateSignalCatchEventDefinition(Element signalEventDefinition, ActivityImpl signalActivity) {
    addStartEventListener(signalActivity);
    addEndEventListener(signalActivity);
  }

  @Override
  public void parseBoundarySignalEventDefinition(Element signalEventDefinition, boolean interrupting, ActivityImpl signalActivity) {
// start and end event listener are set by parseBoundaryEvent()
  }

  @Override
  public void parseEventBasedGateway(Element eventBasedGwElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseTransaction(Element transactionElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseCompensateEventDefinition(Element compensateEventDefinition, ActivityImpl compensationActivity) {
  }

  @Override
  public void parseIntermediateThrowEvent(Element intermediateEventElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseIntermediateCatchEvent(Element intermediateEventElement, ScopeImpl scope, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseBoundaryEvent(Element boundaryEventElement, ScopeImpl scopeElement, ActivityImpl activity) {
    addStartEventListener(activity);
    addEndEventListener(activity);
  }

  @Override
  public void parseIntermediateMessageCatchEventDefinition(Element messageEventDefinition, ActivityImpl nestedActivity) {
  }

  @Override
  public void parseBoundaryMessageEventDefinition(Element element, boolean interrupting, ActivityImpl messageActivity) {
  }

}
