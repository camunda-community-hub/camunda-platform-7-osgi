# Camunda BPM - OSGi Event Bridge

This modules enables it to send process events via the [OSGi EventAdmin](https://osgi.org/javadoc/r4v42/org/osgi/service/event/EventAdmin.html). Basically it works like the [CDI Event Bridge](http://docs.camunda.org/7.2/guides/user-guide/#cdi-and-java-ee-integration-cdi-event-bridge).

To enable the event bridge you have to get the service ``OSGiEventBridgeActivator``. The service itself is a ``BpmnParseListener``, which you have to add to your ``ProcessEngineConfiguration`` as custom PreBpmnParseListener, e.g.:
```

    StandaloneInMemProcessEngineConfiguration configuration = new StandaloneInMemProcessEngineConfiguration();
    configuration.setCustomPreBPMNParseListeners(Collections.<BpmnParseListener>singletonList(eventBridgeActivator));
```

You can then register your own ``org.osgi.service.event.EventHandler`` for one of the following topics:
* org/camunda/bpm/extension/osgi/eventing/TaskEvent
* org/camunda/bpm/extension/osgi/eventing/Execution
* org/camunda/bpm/extension/osgi/eventing/&#42; (wildcard to receive events for both topics)

Both are constants in ``org.camunda.bpm.extension.osgi.eventing.api.Topics``.
The possible keys for the properties inside the event are listed in ``org.camunda.bpm.extension.osgi.eventing.api.BusinessProcessEventProperties``.