/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.osgi.application;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import javax.inject.Inject;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.ProcessApplicationService;
import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.osgi.OSGiTestCase;
import org.camunda.bpm.extension.osgi.TestBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * Test to check if a {@link ProcessApplicationInterface} will be found an
 * registered and if the scanner for process definitions works.
 * 
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ProcessApplicationDeployerWithScanIntegrationTest extends OSGiTestCase {

  @Inject
  protected BundleContext bundleContext;

  @Inject
  protected BlueprintContainer blueprintContainer;

  @Inject
  @Filter(timeout = 20000L)
  protected ProcessEngine engine;

  @Configuration
  @Override
  public Option[] createConfiguration() {
    Option[] blueprintEnv = options(mavenBundle().groupId("org.assertj").artifactId("assertj-core").version("1.5.0"),
        mavenBundle().groupId("org.apache.aries.blueprint").artifactId("org.apache.aries.blueprint.core").version("1.0.0"),
        mavenBundle().groupId("org.apache.aries.proxy").artifactId("org.apache.aries.proxy").version("1.0.0"), mavenBundle().groupId("org.apache.aries")
            .artifactId("org.apache.aries.util").version("1.0.0"));
    Option testBundle = provision(createTestBundle());
    return OptionUtils.combine(OptionUtils.combine(super.createConfiguration(), blueprintEnv), testBundle);
  }

  private InputStream createTestBundle() {
    try {
      File bpmn = new File("src/test/resources/testprocess.bpmn");
      return TinyBundles.bundle().add("OSGI-INF/blueprint/context.xml", new FileInputStream(new File("src/test/resources/testprocessapplicationcontext.xml")))
          .set(Constants.BUNDLE_SYMBOLICNAME, "org.camunda.bpm.osgi.example").set(Constants.BUNDLE_CLASSPATH, ".")
          .add("META-INF/processes.xml", new FileInputStream(new File("src/test/resources/testprocesseswithscan.xml"))).add(TestBean.class)
          .add(MyProcessApplication.class).set(Constants.DYNAMICIMPORT_PACKAGE, "*").set(Constants.EXPORT_PACKAGE, "*")
          .add("org/camunda/process.bpmn", new FileInputStream(bpmn)).build();
    } catch (FileNotFoundException fnfe) {
      fail(fnfe.toString());
      return null;
    }
  }

  @Test(timeout = 20000L)
  public void shouldBeAbleToDeploy() throws InterruptedException {
    String processApplicationName = "yo!";
    // It could take a second to register the process application
    Set<String> processApplicationNames = null;
    ProcessApplicationService processApplicationService = BpmPlatform.getProcessApplicationService();
    do {
      Thread.sleep(500L);
      processApplicationNames = processApplicationService.getProcessApplicationNames();
    } while (processApplicationNames.isEmpty());
    assertThat(processApplicationNames, hasItem(processApplicationName));
  }

  @Test
  public void shouldRegisterDefaultProcessEngine() throws InterruptedException {
    assertThat(engine, is(notNullValue()));
    assertThat(engine.getName(), is("default"));
  }

  @Test(timeout = 20000L)
  public void shouldDeployProcessAutomatically() {
    RepositoryService repositoryService = engine.getRepositoryService();
    ProcessDefinition result;
    do {
      result = repositoryService.createProcessDefinitionQuery().processDefinitionKey("Process_1").singleResult();
    } while (result == null);
    assertThat(result, is(notNullValue()));
  }

}
