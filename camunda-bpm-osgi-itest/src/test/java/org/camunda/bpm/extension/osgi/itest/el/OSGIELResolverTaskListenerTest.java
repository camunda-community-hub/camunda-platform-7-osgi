package org.camunda.bpm.extension.osgi.itest.el;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import java.io.File;
import java.util.Hashtable;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGIELResolverTaskListenerTest extends AbstractOSGiELResolverIntegrationTest{
  @Override
  protected File getProcessDefinition() {
    return new File("src/test/resources/el/testTaskListener.bpmn");
  }

  @Test
  public void runProcess() throws Exception {
    JustAnotherTaskListener service = new JustAnotherTaskListener();
    ctx.registerService(TaskListener.class, service, null);
    ProcessInstance processInstance = processEngine.getRuntimeService()
      .startProcessInstanceByKey("taskListenerTestProcess");
    assertThat(service.called, is(true));
  }
}
