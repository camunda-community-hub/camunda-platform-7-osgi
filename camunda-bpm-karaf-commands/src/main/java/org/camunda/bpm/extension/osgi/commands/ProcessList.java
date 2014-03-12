package org.camunda.bpm.extension.osgi.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.osgi.commands.asciitable.ASCIITable;

/**
 * List available process definitions.
 */
@Command(scope = "camunda", name = "process-list", description = "List process definitions.")
public class ProcessList extends OsgiCommandSupport {

  private final ProcessEngine engine;

  @Argument(index = 0, name = "deploymentId",
      description = "Deployment id",
      multiValued = false)
  private String deploymentId;

  public ProcessList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    if (deploymentId == null) {
      List<Deployment> deployments = engine.getRepositoryService().createDeploymentQuery().orderByDeploymenTime().desc().list();
      for (Deployment d : deployments) {
        System.out.println("\n Deployment " + d.getId() + " " + d.getName() + " " + d.getDeploymentTime().toGMTString());
        printProcessDefinition(d.getId());
      }

    } else {
      printProcessDefinition(deploymentId);
    }
    return null;


  }

  public void printProcessDefinition(String deploymentId) {
    String[] header = new String[]{"ID", "KEY", "NAME", "DEPLOYMENT", "CATEGORY"};

    List<ProcessDefinition> processes = engine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentId).list();
    String[][] data = new String[processes.size()][header.length];
    int i = 0;
    for (ProcessDefinition process : processes) {
      data[i++] = new String[]{
          process.getId(),
          process.getKey(),
          process.getName(),
          process.getDeploymentId(),
          process.getCategory()
      };
    }
    ASCIITable.getInstance().printTable(header, data);


  }
}
