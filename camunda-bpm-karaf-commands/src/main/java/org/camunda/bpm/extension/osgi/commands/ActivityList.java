package org.camunda.bpm.extension.osgi.commands;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.extension.osgi.commands.asciitable.ASCIITable;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

/**
 * List activities of an instance.
 */
@Command(scope = "camunda", name = "activity-list", description = "List activities")
public class ActivityList extends OsgiCommandSupport {

  private static final String[] HEADER = new String[]{"ID", "DURATION", "ACTIVITY_ID", "ACTIVITY_NAME", "ACTIVITY_TYPE"};

  private final ProcessEngine engine;

  @Argument(index = 0, name = "instanceId",
      description = "Process instance id",
      required = true, multiValued = false)
  private String instanceId;


  public ActivityList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    HistoricProcessInstance processInstance = engine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
    System.out.println("Process instance   : " + processInstance.getId());
    System.out.println("Process definition : " + processInstance.getProcessDefinitionId());
    System.out.println("");
    List<HistoricActivityInstance> activities = engine.getHistoryService().createHistoricActivityInstanceQuery().
        processInstanceId(instanceId).orderByHistoricActivityInstanceStartTime().asc().list();

    String[][] data = new String[activities.size()][HEADER.length];
    int i = 0;
    for (HistoricActivityInstance activity : activities) {
      data[i++] = new String[]{
          activity.getId(),
          activity.getDurationInMillis() + " ms",
          activity.getActivityId(),
          activity.getActivityName(),
          activity.getActivityType()
      };
    }
    ASCIITable.getInstance().printTable(HEADER, data);
    return null;
  }
}
