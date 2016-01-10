package org.camunda.bpm.extension.osgi.itest.el;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

/**
 * Integration test to check if the OSGiELResolver finds a ActivityBehavior via
 * its class name.
 * 
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiELResolverBehaviorIntegrationTest extends AbstractOSGiELResolverIntegrationTest {

  @Override
  protected File getProcessDefinition() {
    return new File("src/test/resources/el/behaviortestprocess.bpmn");
  }

  @Test
  public void runProcess() throws Exception {
    TestActivityBehaviour behaviour = new TestActivityBehaviour();
    ctx.registerService(ActivityBehavior.class, behaviour, null);
    processEngine.getRuntimeService().startProcessInstanceByKey("delegate");
    assertThat(behaviour.getCalled(), is(true));
  }

}
