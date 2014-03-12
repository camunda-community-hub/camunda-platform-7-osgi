package org.camunda.bpm.extension.osgi.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.extension.osgi.commands.asciitable.ASCIITable;

import java.util.List;

/**
 * List available process deployments.
 */
@Command(scope = "camunda", name = "execution-list", description = "List process executions by process definition.")
public class ExecutionList extends OsgiCommandSupport {

  private static final String[] HEADER = new String[]{"ID", "INSTANCE", "SUSPENDED?", "ENDED?"};


  @Argument(index = 0, name = "process id",
      required = true,
      description = "Id of the process definition",
      multiValued = false)

  private String processId;

  private final ProcessEngine engine;

  public ExecutionList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    try {
      List<Execution> executions = engine.getRuntimeService().createExecutionQuery().processDefinitionId(processId).list();
      String[][] data = new String[executions.size()][HEADER.length];
      int i = 0;
      for (Execution ex : executions) {
        data[i++] = new String[]{
            ex.getId(),
            ex.getProcessInstanceId(),
            "" + ex.isSuspended(),
            "" + ex.isEnded()
        };
      }

      ASCIITable.getInstance().printTable(HEADER, data);

    } catch (Exception ex) {
      ex.printStackTrace();

    }
    return null;
  }
}
