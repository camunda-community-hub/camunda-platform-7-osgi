package org.camunda.bpm.extension.osgi.commands.completer;

import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.Deployment;

import java.util.Collections;
import java.util.List;

/**
 * Autocomplete helper lists available deployments.
 */
public class DeploymentCompleter implements Completer {
  private final ProcessEngine engine;

  public DeploymentCompleter(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  public int complete(String s, int i, List<String> strings) {
    StringsCompleter delegate = new StringsCompleter();
    try {
      List<Deployment> deployments = engine.getRepositoryService().createDeploymentQuery().list();

      for (Deployment d : deployments) {
        delegate.getStrings().add(d.getId());
      }

    } catch (Exception ex) {
      ex.printStackTrace();

    }
    return delegate.complete(s, i, strings);
  }
}
