# camunda BPM OSGi Fileinstall

This module enables you to transform BPMN files automatically into bundles with the help of [Apache Felix Fileinstall](http://felix.apache.org/documentation/subprojects/apache-felix-file-install.html).

If your OSGi runtime supports Fileinstall you can drop process definitions in the directory watched by Fileinstall. The files will be parsed and automatically transformed into an OSGi bundle.

Every file that has either a ".bpmn" or ".xml" suffix and has the root element ``defintions``with the namespace URI ``http://www.omg.org/spec/BPMN/20100524/MODEL`` will be considered for transformation.

Also, you can add a version to the file name. For example the following schemes are supported:

| filename               | bunde symbolic name | version   |
|------------------------|---------------------|-----------|
| process.bpmn           | process             | 0.0.0     |
| process-1.0.0.bpmn     | process             | 1.0.0     |
| process-2.bpmn         | process             | 2         |
| process-2-RC1.bpmn     | process             | 2.0.0.RC1 |
| process-1.4.5.RC2.bpmn | process             | 1.4.5.RC2 |

