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
package org.camunda.bpm.extension.osgi.itest.application;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.application.impl.BlueprintBundleLocalELResolver;
import org.camunda.bpm.extension.osgi.itest.OSGiBlueprintTestEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * Test to see if the {@link BlueprintBundleLocalELResolver} can resolve an
 * EL-expression by finding a bean from the context.xml.
 *
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BlueprintBundleLocalELResolverIntegrationTest extends OSGiBlueprintTestEnvironment {

  @Inject
  protected BundleContext bundleContext;

  @Inject
  protected BlueprintContainer blueprintContainer;

  @Inject
  @Filter(timeout = 30000L)
  protected ProcessEngine engine;



  @Override
  protected InputStream createTestBundle() {
    try {
      return TinyBundles.bundle().add("OSGI-INF/blueprint/context.xml", new FileInputStream(new File("src/test/resources/application/testprocessapplicationcontext.xml")))
          .set(Constants.BUNDLE_SYMBOLICNAME, "org.camunda.bpm.osgi.example")
          .add("META-INF/processes.xml", new FileInputStream(new File("src/test/resources/application/testprocesses.xml")))
          .add(TestBean.class)
          .add(MyProcessApplication.class).set(Constants.DYNAMICIMPORT_PACKAGE, "*").set(Constants.EXPORT_PACKAGE, "*")
          .build();
    } catch (FileNotFoundException fnfe) {
      fail(fnfe.toString());
      return null;
    }
  }

  @Test(timeout = 35000L)
  public void shouldBeAbleToResolveBean() throws InterruptedException {
    RepositoryService repositoryService = engine.getRepositoryService();
    ProcessDefinition processDefinition = null;
    //it can take a while to deploy the process -> it's the step after registering the engine
    do {
      Thread.sleep(500L);
      ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
      processDefinition = query.processDefinitionKey("foo").singleResult();
    } while (processDefinition == null);
    ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceByKey("foo");
    assertThat(processInstance.isEnded(), is(true));
  }

}
