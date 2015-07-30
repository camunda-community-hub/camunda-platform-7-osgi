package org.camunda.bpm.extension.osgi.eventing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.osgi.eventing.api.BusinessProcessEventProperties;
import org.camunda.bpm.extension.osgi.eventing.api.Topics;
import org.camunda.bpm.extension.osgi.eventing.impl.OSGiEventDistributor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class OSGiEventDistributorTest {

  @Test
  public void notifyTask() {
    String processDefinitionId = "123";
    String currActivityId = "Act1234";
    String processInstanceId = "Inst1234";
    String executionId = "Exe4711";
    String taskId = "Task42";
    String taskDefinitionKey = "TaskDef";
    String transitionId = "Trans1";
    DelegateTask task = mock(DelegateTask.class);
    when(task.getProcessDefinitionId()).thenReturn(processDefinitionId);
    when(task.getProcessInstanceId()).thenReturn(processInstanceId);
    when(task.getExecutionId()).thenReturn(executionId);
    when(task.getId()).thenReturn(taskId);
    when(task.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
    when(task.getEventName()).thenReturn(TaskListener.EVENTNAME_CREATE);
    DelegateExecution execution = mock(DelegateExecution.class);
    when(execution.getCurrentActivityId()).thenReturn(currActivityId);
    when(execution.getCurrentTransitionId()).thenReturn(transitionId);
    when(task.getExecution()).thenReturn(execution);
    EventAdmin eventAdminMock = mock(EventAdmin.class);
    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    OSGiEventDistributor distributor = new OSGiEventDistributor(eventAdminMock);
    distributor.notify(task);

    verify(eventAdminMock).postEvent(eventCaptor.capture());
    Event event = eventCaptor.getValue();
    assertThat(event.getTopic(), is(Topics.TASK_EVENT_TOPIC));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.ACTIVITY_ID), is(currActivityId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.EXECUTION_ID), is(executionId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.PROCESS_DEFINITION), is(processDefinitionId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.PROCESS_INSTANCE_ID), is(processInstanceId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TASK_DEFINITION_KEY), is(taskDefinitionKey));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TASK_ID), is(taskId));
    String timestamp = (String) event.getProperty(BusinessProcessEventProperties.TIMESTAMP);
    assertThat(new Date(Long.parseLong(timestamp)), is(beforeOrEqual(new Date())));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TRANSITION_ID), is(transitionId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TYPE), is(TaskListener.EVENTNAME_CREATE));
  }

  @Test
  public void notifyExecution() throws Exception {
    String processDefinitionId = "123";
    String currActivityId = "Act1234";
    String processInstanceId = "Inst1234";
    String executionId = "Exe4711";
    String transitionId = "Trans1";
    DelegateExecution execution = mock(DelegateExecution.class);
    when(execution.getCurrentActivityId()).thenReturn(currActivityId);
    when(execution.getCurrentTransitionId()).thenReturn(transitionId);
    when(execution.getProcessDefinitionId()).thenReturn(processDefinitionId);
    when(execution.getProcessInstanceId()).thenReturn(processInstanceId);
    when(execution.getId()).thenReturn(executionId);
    when(execution.getEventName()).thenReturn(ExecutionListener.EVENTNAME_START);
    EventAdmin eventAdminMock = mock(EventAdmin.class);
    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    OSGiEventDistributor distributor = new OSGiEventDistributor(eventAdminMock);
    distributor.notify(execution);

    verify(eventAdminMock).postEvent(eventCaptor.capture());
    Event event = eventCaptor.getValue();
    assertThat(event.getTopic(), is(Topics.EXECUTION_EVENT_TOPIC));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.ACTIVITY_ID), is(currActivityId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.EXECUTION_ID), is(executionId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.PROCESS_DEFINITION), is(processDefinitionId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.PROCESS_INSTANCE_ID), is(processInstanceId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TASK_DEFINITION_KEY), is(nullValue()));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TASK_ID), is(nullValue()));
    String timestamp = (String) event.getProperty(BusinessProcessEventProperties.TIMESTAMP);
    assertThat(new Date(Long.parseLong(timestamp)), is(beforeOrEqual(new Date())));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TRANSITION_ID), is(transitionId));
    assertThat((String) event.getProperty(BusinessProcessEventProperties.TYPE), is(ExecutionListener.EVENTNAME_START));
  }
  
  private BaseMatcher<Date> beforeOrEqual(final Date date) {
    return new TypeSafeMatcher<Date>() {

      @Override
      public void describeTo(Description description) {
        description.appendText("before").appendValue(date);
      }

      @Override
      protected boolean matchesSafely(Date item) {
        return item.before(date) || item.equals(item);
      }
    };
  }

}
