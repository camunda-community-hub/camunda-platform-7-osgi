package org.camunda.bpm.extension.osgi.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;

/**
 * List activities of an instance.
 */
@Command(scope = "camunda", name = "export-diagram", description = "Export definition of a diagram")
public class ExportDiagram extends OsgiCommandSupport {

  private final ProcessEngine engine;

  @Argument(index = 0, name = "processDefinitionId",
      description = "Process definition id",
      required = true, multiValued = false)
  private String processDefinitionId;

  public ExportDiagram(ProcessEngine engine) {
    this.engine = engine;
  }

  @Override
  protected Object doExecute() throws Exception {
    InputStream is = engine.getRepositoryService().getProcessDiagram(processDefinitionId);
    ProcessDefinition def = engine.getRepositoryService().getProcessDefinition(processDefinitionId);
    File exportFIle = File.createTempFile("camunda", def.getName());
    FileOutputStream fis = null;
    try {
      fis = new FileOutputStream(exportFIle);
      byte[] buffer = new byte[1024];
      int len;
      while ((len = is.read(buffer)) > -1) {
        fis.write(buffer, 0, len);
      }
      fis.flush();
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (fis != null) fis.close();
    }
    System.out.println("Process graph exported to " + exportFIle.getAbsolutePath());
    return null;

  }
}
