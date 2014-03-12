package org.camunda.bpm.extension.osgi.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.extension.osgi.commands.asciitable.ASCIITable;

import java.util.List;

/**
 * List available process deployments.
 */
@Command(scope = "camunda", name = "variable-list", description = "List variables on a specific instance.")
public class VariableList extends OsgiCommandSupport {

  private static final String[] HEADER = new String[]{"ID", "INSTANCE_ID", "NAME", "TYPE", "VALUE"};


  @Argument(index = 0, name = "process instance id",
      required = false,
      description = "Id of the process definition",
      multiValued = false)

  private String instanceId;

  private final ProcessEngine engine;

  public VariableList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    try {
      List<HistoricVariableInstance> variables = engine.getHistoryService().createHistoricVariableInstanceQuery().list();
      String[][] data = new String[variables.size()][HEADER.length];
      int i = 0;
      for (HistoricVariableInstance variable : variables) {
        String value;
        try {
          value = "" + variable.getValue();
        } catch (Exception ex) {
          value = "<unparsable variable>";
        }
        data[i++] = new String[]{
            variable.getId(),
            variable.getProcessInstanceId(),
            variable.getVariableName(),
            variable.getVariableTypeName(),
            value
        };
      }

      ASCIITable.getInstance().printTable(HEADER, data);

    } catch (Exception ex) {
      ex.printStackTrace();

    }
    return null;
  }
}
