package org.camunda.bpm.extension.osgi.eventing.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.camunda.bpm.extension.osgi.eventing.api.OSGiEventBridgeActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ronny Bräunlich
 */
public class EventDistributorHandler implements InvocationHandler {

  /**
   * We are just fine with the system bundle because all we do is service lookups
   */
  private final Bundle systemBundle;

  public EventDistributorHandler(BundleContext context) {
    this.systemBundle = context.getBundle(0);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (isObjectMethod(method)) {
      return handleObjectMethod(proxy, method, args);
    }
    if (isEventBridgeActivatorPresent(systemBundle.getBundleContext())) {
      EventAdmin eventAdmin = findEventAdmin(systemBundle.getBundleContext());
      if (eventAdmin != null) {
        OSGiEventDistributor distributor = new OSGiEventDistributor(eventAdmin);
        return method.invoke(distributor, args);
      }
    }
    return null;
  }

  private boolean isObjectMethod(Method method) {
    return Object.class.equals(method.getDeclaringClass());
  }

  private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
    String name = method.getName();
    if ("equals".equals(name)) {
      return proxy == args[0];
    } else if ("hashCode".equals(name)) {
      return System.identityHashCode(proxy);
    } else if ("toString".equals(name)) {
      return proxy.getClass().getName() + "@" +
        Integer.toHexString(System.identityHashCode(proxy)) +
        ", with InvocationHandler " + this;
    } else {
      throw new IllegalStateException(String.valueOf(method));
    }
  }

  private boolean isEventBridgeActivatorPresent(BundleContext ctx) {
    ServiceReference<OSGiEventBridgeActivator> reference = ctx.getServiceReference(OSGiEventBridgeActivator.class);
    if (reference != null) {
      return ctx.getService(reference) != null;
    }
    return false;
  }

  private EventAdmin findEventAdmin(BundleContext ctx) {
    ServiceReference<EventAdmin> ref = ctx.getServiceReference(EventAdmin.class);
    EventAdmin eventAdmin = null;
    if (ref != null) {
      eventAdmin = ctx.getService(ref);
    }
    return eventAdmin;
  }
}
