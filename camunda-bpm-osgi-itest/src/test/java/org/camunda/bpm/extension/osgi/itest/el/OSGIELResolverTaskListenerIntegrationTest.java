package org.camunda.bpm.extension.osgi.itest.el;

import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGIELResolverTaskListenerIntegrationTest extends AbstractOSGiELResolverIntegrationTest{
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
