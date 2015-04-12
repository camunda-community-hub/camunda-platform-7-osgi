package org.camunda.bpm.extension.osgi.eventing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.osgi.eventing.api.OSGiEventBridgeActivator;
import org.camunda.bpm.extension.osgi.eventing.impl.EventDistributorHandler;
import org.camunda.bpm.extension.osgi.eventing.impl.OSGiEventDistributor;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class EventDistributorHandlerTest {

  @Test
  public void inkoveWithoutEventBridgeActivatorReference() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    when(bundleContext.getServiceReference(eq(OSGiEventBridgeActivator.class))).thenReturn(null);

    Object invoke = new EventDistributorHandler(bundleContext).invoke(new Object(), OSGiEventDistributor.class.getMethod("notify", DelegateExecution.class),
        null);
    assertThat(invoke, is(nullValue()));
  }

  @Test
  public void inkoveWithoutEventBridgeActivator() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    @SuppressWarnings("unchecked")
    ServiceReference<OSGiEventBridgeActivator> refMock = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(eq(OSGiEventBridgeActivator.class))).thenReturn(refMock);
    when(bundleContext.getService(eq(refMock))).thenReturn(null);

    Object invoke = new EventDistributorHandler(bundleContext).invoke(new Object(), OSGiEventDistributor.class.getMethod("notify", DelegateExecution.class),
        null);
    assertThat(invoke, is(nullValue()));
  }

  private BundleContext createBundleContext() {
    BundleContext bundleContext = mock(BundleContext.class);
    Bundle bundleMock = mock(Bundle.class);
    when(bundleContext.getBundle(eq(0L))).thenReturn(bundleMock);
    when(bundleMock.getBundleContext()).thenReturn(bundleContext);
    return bundleContext;
  }

  @Test
  public void invokeWithoutEventAdminRef() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    registerEventBridgeActivator(bundleContext);
    when(bundleContext.getServiceReference(eq(EventAdmin.class))).thenReturn(null);

    Object invoke = new EventDistributorHandler(bundleContext).invoke(new Object(), OSGiEventDistributor.class.getMethod("notify", DelegateExecution.class),
        null);
    assertThat(invoke, is(nullValue()));
  }

  @Test
  public void invokeWithoutEventAdmin() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    registerEventBridgeActivator(bundleContext);
    @SuppressWarnings("unchecked")
    ServiceReference<EventAdmin> refMock = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(eq(EventAdmin.class))).thenReturn(refMock);
    when(bundleContext.getService(eq(refMock))).thenReturn(null);

    Object invoke = new EventDistributorHandler(bundleContext).invoke(new Object(), OSGiEventDistributor.class.getMethod("notify", DelegateExecution.class),
        null);
    assertThat(invoke, is(nullValue()));
  }

  @Test
  public void invoke() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    registerEventBridgeActivator(bundleContext);
    EventAdmin eventAdmin = registerEventAdmin(bundleContext);
    DelegateExecution execution = createExecutionMock();
    Object invoke = new EventDistributorHandler(bundleContext).invoke(new Object(), OSGiEventDistributor.class.getMethod("notify", DelegateExecution.class),
        new Object[] { execution });

    assertThat(invoke, is(nullValue()));
    verify(eventAdmin, times(1)).postEvent(any(Event.class));
  }

  @Test
  public void testEquals() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    
    Object proxy = new Object();
    Object invoke = new EventDistributorHandler(bundleContext).invoke(proxy, Object.class.getMethod("equals", Object.class),
        new Object[] { proxy });
    assertThat(invoke, is((Object)Boolean.TRUE));
  }

  @Test
  public void testHashCode() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    
    Object proxy = new Object();
    Object invoke = new EventDistributorHandler(bundleContext).invoke(proxy, Object.class.getMethod("hashCode"),
        null);
    assertThat(invoke, is((Object)Integer.valueOf(proxy.hashCode())));
  }
  
  @Test
  public void testToString() throws Throwable {
    BundleContext bundleContext = createBundleContext();
    
    Object proxy = new Object();
    EventDistributorHandler handler = new EventDistributorHandler(bundleContext);
    Object invoke = handler.invoke(proxy, Object.class.getMethod("toString"),
        null);
    assertThat(invoke, is((Object)(proxy.toString()+", with InvocationHandler " + handler)));
  }
  
  private DelegateExecution createExecutionMock() {
    DelegateExecution execution = mock(DelegateExecution.class);
    when(execution.getCurrentActivityId()).thenReturn("");
    when(execution.getCurrentTransitionId()).thenReturn("");
    when(execution.getProcessDefinitionId()).thenReturn("");
    when(execution.getProcessInstanceId()).thenReturn("");
    when(execution.getId()).thenReturn("");
    when(execution.getEventName()).thenReturn(ExecutionListener.EVENTNAME_START);
    return execution;
  }

  private EventAdmin registerEventAdmin(BundleContext bundleContext) {
    @SuppressWarnings("unchecked")
    ServiceReference<EventAdmin> refMock = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(eq(EventAdmin.class))).thenReturn(refMock);
    EventAdmin eventAdmin = mock(EventAdmin.class);
    when(bundleContext.getService(eq(refMock))).thenReturn(eventAdmin);
    return eventAdmin;
  }

  private void registerEventBridgeActivator(BundleContext bundleContext) {
    @SuppressWarnings("unchecked")
    ServiceReference<OSGiEventBridgeActivator> refMock = mock(ServiceReference.class);
    when(bundleContext.getServiceReference(eq(OSGiEventBridgeActivator.class))).thenReturn(refMock);
    when(bundleContext.getService(eq(refMock))).thenReturn(mock(OSGiEventBridgeActivator.class));
  }
}
