package org.camunda.bpm.extension.osgi.commands;

import org.camunda.bpm.engine.ProcessEngine;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.extension.osgi.commands.asciitable.ASCIITable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * List available process deployments.
 */
@Command(scope = "camunda", name = "deployment-list", description = "List camunda deployments.")
public class DeploymentList extends OsgiCommandSupport {

  private static final String[] HEADER = new String[]{"ID", "NAME", "DEPLOYMENT_TIME"};

  private final ProcessEngine engine;

  public DeploymentList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    try {
      List<Deployment> deployments = engine.getRepositoryService().createDeploymentQuery().list();
      Collections.sort(deployments, new Comparator<Deployment>() {
        @Override
        public int compare(Deployment d1, Deployment d2) {
          return d1.getDeploymentTime().compareTo(d2.getDeploymentTime());
        }
      });

      if (deployments.size() > 0) {
        String[][] data = new String[deployments.size()][HEADER.length];
        int i = 0;
        for (Deployment d : deployments) {
          data[i++] = new String[]{
              d.getId(),
              d.getName(),
              d.getDeploymentTime().toString()};
        }

        ASCIITable.getInstance().printTable(HEADER, data);
      } else {
        System.out.println("There is no active deployment");
      }
    } catch (Exception ex) {
      ex.printStackTrace();

    }
    return null;
  }
}
