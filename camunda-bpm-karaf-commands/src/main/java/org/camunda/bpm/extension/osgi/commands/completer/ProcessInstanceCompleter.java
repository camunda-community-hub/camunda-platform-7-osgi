package org.camunda.bpm.extension.osgi.commands.completer;

import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;

import java.util.List;

/**
 * Autocomplete helper lists available process instance ids.
 */
public class ProcessInstanceCompleter implements Completer {
  private final ProcessEngine engine;

  public ProcessInstanceCompleter(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  public int complete(String s, int i, List<String> strings) {
    StringsCompleter delegate = new StringsCompleter();
    try {
      List<HistoricProcessInstance> definitions = engine.getHistoryService().createHistoricProcessInstanceQuery()
          .orderByProcessInstanceStartTime().desc().list();

      for (HistoricProcessInstance d : definitions) {
        delegate.getStrings().add(d.getId());
      }

    } catch (Exception ex) {
      ex.printStackTrace();

    }
    return delegate.complete(s, i, strings);
  }
}
