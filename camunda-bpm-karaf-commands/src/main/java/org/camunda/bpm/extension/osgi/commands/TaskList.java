package org.camunda.bpm.extension.osgi.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;

/**
 * List activities of an instance.
 */
@Command(scope = "camunda", name = "task-list", description = "List tasks for a specific process definition.")
public class TaskList extends OsgiCommandSupport {

  private static final String[] HEADER = new String[]{"ID", "INSTANCE", "PROCESS_DEF", "ASIGNEE", "NAME"};

  private final ProcessEngine engine;

  @Argument(index = 0,
      name = "process definition id",
      description = "Process definition id",
      multiValued = false)
  private String processId;


  public TaskList(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    TaskQuery taskQuery = engine.getTaskService().createTaskQuery();
    if (processId != null) {
      taskQuery.processDefinitionId(processId);
    }
    List<Task> tasks = taskQuery.orderByTaskCreateTime().asc().list();
    int i = 0;
    String[][] data = new String[tasks.size()][HEADER.length];
    for (Task task : tasks) {
      data[i++] = new String[]{
          task.getId(),
          task.getProcessInstanceId(),
          task.getProcessDefinitionId(),
          task.getAssignee(),
          task.getName()};
    }
    return null;
  }
}
