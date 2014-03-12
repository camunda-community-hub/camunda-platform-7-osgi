package org.camunda.bpm.extension.osgi;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Constants;

/**
 * This test checks that the deployment listener will parse a process definition
 * which is placed at the default location defined by
 * <p>
 * <code>
 * org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT
 * </code>
 * <p>
 * inside a bundle.
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DeploymentListenerBundleLocationTest extends
		AbstractDeploymentListenerTest {

	protected InputStream createTestBundleWithProcessDefinition() {
		try {
			return TinyBundles
					.bundle()
					.add(org.camunda.bpm.extension.osgi.Constants.BUNDLE_PROCESS_DEFINTIONS_DEFAULT
							+ "testprocess.bpmn",
							new FileInputStream(new File(
									"src/test/resources/testprocess.bpmn")))
					.set(Constants.BUNDLE_SYMBOLICNAME,
							"org.camunda.bpm.osgi.example").build();
		} catch (FileNotFoundException fnfe) {
			fail(fnfe.toString());
			return null;
		}
	}
}
