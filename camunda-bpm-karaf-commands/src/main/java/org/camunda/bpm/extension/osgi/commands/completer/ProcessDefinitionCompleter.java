package org.camunda.bpm.extension.osgi.commands.completer;

import java.util.List;

import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;

/**
 * Autocomplete helper lists available process definition ids..
 */
public class ProcessDefinitionCompleter implements Completer {
  private final ProcessEngine engine;

  public ProcessDefinitionCompleter(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  public int complete(String s, int i, List<String> strings) {
    StringsCompleter delegate = new StringsCompleter();
    try {
      List<ProcessDefinition> definitions = engine.getRepositoryService().createProcessDefinitionQuery().list();

      for (ProcessDefinition d : definitions) {
        delegate.getStrings().add(d.getId());
      }

    } catch (Exception ex) {
      ex.printStackTrace();

    }
    return delegate.complete(s, i, strings);
  }
}
