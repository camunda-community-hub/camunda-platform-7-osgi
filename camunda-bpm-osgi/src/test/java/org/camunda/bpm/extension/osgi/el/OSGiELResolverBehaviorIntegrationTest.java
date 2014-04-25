package org.camunda.bpm.extension.osgi.el;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.TestActivityBehaviour;
import org.junit.Ignore;
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
 @Ignore(value="Until CAM-1481 got fixed")
public class OSGiELResolverBehaviorIntegrationTest extends
		AbstractOSGiELResolverIntegrationTest {

	@Override
	protected File getProcessDefinition() {
		return new File(
				"src/test/resources/org/camunda/bpm/extension/osgi/el/behaviortestprocess.bpmn");
	}

	@Test
	public void runProcess() throws Exception {
		TestActivityBehaviour behaviour = new TestActivityBehaviour();
		ctx.registerService(ActivityBehavior.class.getName(), behaviour, null);
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey("delegate");
		assertThat(behaviour.getCalled(), is(true));
		assertThat(processInstance.isEnded(), is(true));
	}

}
