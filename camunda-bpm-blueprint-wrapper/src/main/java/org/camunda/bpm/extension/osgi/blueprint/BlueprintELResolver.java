package org.camunda.bpm.extension.osgi.blueprint;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;

/**
 * @see org.camunda.bpm.engine.test.spring.ApplicationContextElResolver
 */
public class BlueprintELResolver extends ELResolver {

	private static final Logger LOGGER = Logger
			.getLogger(BlueprintELResolver.class.getName());
  private Map<String, JavaDelegate> delegateMap = new HashMap<String, JavaDelegate>();
  private Map<String, TaskListener> taskListenerMap = new HashMap<String, TaskListener>();
  private Map<String, ActivityBehavior> activityBehaviourMap = new HashMap<String, ActivityBehavior>();


  @Override
  public Object getValue(ELContext context, Object base, Object property) {
    if (base == null) {
      // according to javadoc, can only be a String
      String key = (String) property;
      for (String name : delegateMap.keySet()) {
        if (name.equalsIgnoreCase(key)) {
          context.setPropertyResolved(true);
          return delegateMap.get(name);
        }
      }
      for (String name : taskListenerMap.keySet()) {
        if (name.equalsIgnoreCase(key)) {
          context.setPropertyResolved(true);
          return taskListenerMap.get(name);
        }
      }

      for (String name : activityBehaviourMap.keySet()) {
        if (name.equalsIgnoreCase(key)) {
          context.setPropertyResolved(true);
          return activityBehaviourMap.get(name);
        }
      }
    }
		return null;
	}

	public void bindService(JavaDelegate delegate, Map<?, ?> props) {
		String name = (String) props.get("osgi.service.blueprint.compname");
		delegateMap.put(name, delegate);
		LOGGER.info("added service to delegate cache " + name);
	}

	/**
   * @param delegate the delegate, necessary parameter because of Blueprint
   */
	public void unbindService(JavaDelegate delegate, Map<?, ?> props) {
		String name = (String) props.get("osgi.service.blueprint.compname");
		if (delegateMap.containsKey(name)) {
			delegateMap.remove(name);
		}
		LOGGER.info("removed service from delegate cache " + name);
	}

  public void bindTaskListenerService(TaskListener delegate, Map props) {
    String name = (String) props.get("osgi.service.blueprint.compname");
    taskListenerMap.put(name, delegate);
    LOGGER.info("added service to taskListener cache " + name);
  }

  public void unbindTaskListenerService(TaskListener delegate, Map props) {
    String name = (String) props.get("osgi.service.blueprint.compname");
    if (taskListenerMap.containsKey(name)) {
      taskListenerMap.remove(name);
    }
    LOGGER.info("removed Camunda service from taskListener cache " + name);
  }

  public void bindActivityBehaviourService(ActivityBehavior delegate, Map props) {
    String name = (String) props.get("osgi.service.blueprint.compname");
    activityBehaviourMap.put(name, delegate);
    LOGGER.info("added service to activityBehaviour cache " + name);
  }

  public void unbindActivityBehaviourService(ActivityBehavior delegate, Map props) {
    String name = (String) props.get("osgi.service.blueprint.compname");
    if (activityBehaviourMap.containsKey(name)) {
      activityBehaviourMap.remove(name);
    }
    LOGGER.info("removed Camunda service from activityBehaviour cache " + name);
  }

	@Override
  public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
  public void setValue(ELContext context, Object base, Object property,
			Object value) {
	}

	@Override
  public Class<?> getCommonPropertyType(ELContext context, Object arg) {
		return Object.class;
	}

	@Override
  public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
			Object arg) {
		return null;
	}

	@Override
  public Class<?> getType(ELContext context, Object arg1, Object arg2) {
		return Object.class;
	}
}
