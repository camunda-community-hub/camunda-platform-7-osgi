package org.camunda.bpm.extension.osgi.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.osgi.commands.asciitable.ASCIITable;

/**
 * List instances of a process definition.
 */
@Command(scope = "camunda", name = "instance-list", description = "List process instances of a specific process definition.")
public class InstanceList extends OsgiCommandSupport {

  private final ProcessEngine engine;

  @Argument(index = 0, name = "process definition id",
      description = "Id of the process definition. If all process definition will be printed from the latest deployment.",
      multiValued = false)
  private String processDefinitionId;

  private static final String[] HEADER = new String[]{"ID", "DURATION", "BUSINESS_KEY", "START_USER_ID"};


  public InstanceList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    if (processDefinitionId != null) {
      printProcessInfo(processDefinitionId);
    } else {
      //search for the last deployment
      List<Deployment> deployments = engine.getRepositoryService().createDeploymentQuery().orderByDeploymenTime().desc().listPage(0,
          1);

      Deployment lastDeployment = deployments.get(0);
      System.out.println("Process instance for the last deployment: " + lastDeployment.getId());

      //iterate over the process definitions
      for (ProcessDefinition process : engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(lastDeployment.getId())
          .list()) {
        System.out.println("\nInstances for the process definition: " + process.getId());
        printProcessInfo(process.getId());

      }

    }
    return null;


  }

  public void printProcessInfo(String id) {

    List<HistoricProcessInstance> executions = engine.getHistoryService().createHistoricProcessInstanceQuery().processDefinitionId(id).list();
    String[][] data = new String[executions.size()][HEADER.length];
    int i = 0;
    for (HistoricProcessInstance instance : executions) {
      data[i++] = new String[]{
          instance.getId(),
          instance.getDurationInMillis() + " ms",
          instance.getBusinessKey(),
          instance.getStartUserId()
      };
    }
    ASCIITable.getInstance().printTable(HEADER, data);
  }
}
