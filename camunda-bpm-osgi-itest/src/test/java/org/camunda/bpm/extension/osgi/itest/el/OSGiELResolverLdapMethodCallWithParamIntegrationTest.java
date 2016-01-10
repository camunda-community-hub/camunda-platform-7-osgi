package org.camunda.bpm.extension.osgi.itest.el;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Hashtable;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

/**
 * Integration test to check if the OSGiELResolver finds a JavaDelegate via LDAP
 * filter.
 * 
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiELResolverLdapMethodCallWithParamIntegrationTest extends
		AbstractOSGiELResolverIntegrationTest {

	@Override
	protected File getProcessDefinition() {
		return new File(
				"src/test/resources/el/ldapmethodcallwithparamtestprocess.bpmn");
	}

	@Test
	public void runProcess() throws Exception {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put("processExpression", "thisIsAReallyNeatFeature");
		JustAnotherJavaDelegate service = new JustAnotherJavaDelegate();
		ctx.registerService(JavaDelegate.class, service, properties);
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey("ldap");
		assertThat(service.called, is(true));
		assertThat(processInstance.isEnded(), is(true));
	}

}
