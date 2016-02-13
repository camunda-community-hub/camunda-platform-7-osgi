# Camunda BPM OSGi - Blueprint Wrapper

To ease the use of camunda BPM OSGi inside a Blueprint environment this module provides some helpful classes.

## Creating a process engine

The creation of a process engine can basically be done without any help of this module, if the Blueprint implementation supports non-void setters. In this case your can reference the setters of a ``ProcessEngineConfiguration`` inside the ``context.xml`` in the usual way. If it is not the case a small workaround is needed.

### Using the ConfigurationFactory (deprecated)

The use of the ``ConfigurationFactory`` is not encouraged since it is a workaround for a problem inside a Blueprint implementation and does not directly concern this project. To see the reason why the `ConfigurationFactory` is deprecated see [here](https://groups.google.com/forum/#!topic/camunda-bpm-dev/toZEYMzUJpQ).

To see an example of a context.xml that makes use of the ``ConfigurationFactory`` you can take a look [here](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/camunda-bpm-osgi-itest/src/test/resources/blueprint/context.xml).

### Referencing inside processes

The `BlueprintELResolver` can be used with `JavaDelegates`. You'll have to use the `BlueprintELResolver` as ELResolver and register it to listen for `JavaDelegates`. The `BlueprintELResolver` then tries to match the expression with the Blueprint component name (the id in the context.xml). You can take a look at the [example](https://github.com/camunda/camunda-bpm-platform-osgi/blob/master/camunda-bpm-osgi-itest/src/test/resources/blueprint/context.xml) to see how the ``BlueprintELResolver`` has to be registered.
