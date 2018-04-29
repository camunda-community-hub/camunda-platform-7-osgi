package org.camunda.bpm.extension.osgi.el;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.MethodNotFoundException;
import org.camunda.bpm.engine.impl.javax.el.PropertyNotFoundException;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.extension.osgi.TestActivityBehaviour;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OSGiELResolverTest {

  private TestOSGiELResolver resolver;

  private ELContext elContext;

  private ArgumentCaptor<Boolean> booleanCaptor;

  @Before
  public void setUp() {
    resolver = new TestOSGiELResolver();
    resolver.ctx = mock(BundleContext.class);
    elContext = mock(ELContext.class);
    booleanCaptor = ArgumentCaptor.forClass(Boolean.class);
  }

  @Test
  public void getValueThatCantBeResolved() {
    Object value = resolver.getValue(elContext, null, "myServiceTask");
    assertThat(value, is(nullValue()));
    // make sure nobody called setPropertyResolved()
    verifyZeroInteractions(elContext);
  }

  @Test
  public void getValueForBaseNotNull() {
    Object value = resolver.getValue(elContext, new Object(), "myServiceTask");
    assertThat(value, is(nullValue()));
    // make sure nobody called setPropertyResolved()
    verifyZeroInteractions(elContext);
  }

  @Test
  public void getValueForMatchingLdapFilter() throws InvalidSyntaxException {
    String lookupName = "myServiceTask";
    String ldapFilter = "(processExpression=" + lookupName + ")";
    Object serviceObject = new Object();
    ServiceReference<Object> reference = mockService(serviceObject);
    registerServiceRefsAtContext((Class<Object>) null, ldapFilter, reference);
    Object value = resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void getValueForTooManyMatchingLdapFilter() throws InvalidSyntaxException {
    String lookupName = "everybodysServiceTask";
    String ldapFilter = "(processExpression=" + lookupName + ")";
    registerServiceRefsAtContext(null, ldapFilter, mock(ServiceReference.class), mock(ServiceReference.class));
    resolver.getValue(elContext, null, lookupName);
  }

  @Test
  public void getValueForSingleJavaDelegate() throws InvalidSyntaxException {
    String lookupName = "testJavaDelegate";
    JavaDelegate serviceObject = new TestJavaDelegate();
    ServiceReference<JavaDelegate> reference = mockService(serviceObject);
    registerServiceRefsAtContext(JavaDelegate.class, null, reference);
    JavaDelegate value = (JavaDelegate) resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getValueForTwoJavaDelegates() throws InvalidSyntaxException {
    String lookupName = "testJavaDelegate";
    JavaDelegate serviceObject = new TestJavaDelegate();
    JavaDelegate anotherServiceObject = mock(JavaDelegate.class);
    ServiceReference<JavaDelegate> reference1 = mockService(serviceObject);
    ServiceReference<JavaDelegate> reference2 = mockService(anotherServiceObject);
    registerServiceRefsAtContext(JavaDelegate.class, null, reference1, reference2);
    JavaDelegate value = (JavaDelegate) resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void getValueForTwoJavaDelegatesWithSameName() throws InvalidSyntaxException {
    String lookupName = "testJavaDelegate";
    ServiceReference<JavaDelegate> reference1 = mockService((JavaDelegate) new TestJavaDelegate());
    ServiceReference<JavaDelegate> reference2 = mockService((JavaDelegate) new TestJavaDelegate());
    registerServiceRefsAtContext(JavaDelegate.class, null, reference1, reference2);
    resolver.getValue(elContext, null, lookupName);
  }

  @Test
  public void getValueForSingleActivityBehavior() throws InvalidSyntaxException {
    String lookupName = "testActivityBehaviour";
    ActivityBehavior serviceObject = new TestActivityBehaviour();
    ServiceReference<ActivityBehavior> reference = mockService(serviceObject);
    registerServiceRefsAtContext(ActivityBehavior.class, null, reference);
    ActivityBehavior value = (ActivityBehavior) resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getValueForTwoActivityBehaviors() throws InvalidSyntaxException {
    String lookupName = "testActivityBehaviour";
    ActivityBehavior serviceObject = new TestActivityBehaviour();
    ActivityBehavior anotherServiceObject = mock(ActivityBehavior.class);
    ServiceReference<ActivityBehavior> reference1 = mockService(serviceObject);
    ServiceReference<ActivityBehavior> reference2 = mockService(anotherServiceObject);
    registerServiceRefsAtContext(ActivityBehavior.class, null, reference1, reference2);
    ActivityBehavior value = (ActivityBehavior) resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void getValueForTwoActivityBehaviorsWithSameName() throws InvalidSyntaxException {
    String lookupName = "testActivityBehaviour";
    ActivityBehavior behaviour1 = new TestActivityBehaviour();
    ActivityBehavior behaviour2 = new TestActivityBehaviour();
    ServiceReference<ActivityBehavior> reference1 = mockService(behaviour1);
    ServiceReference<ActivityBehavior> reference2 = mockService(behaviour2);
    registerServiceRefsAtContext(ActivityBehavior.class, null, reference1, reference2);
    resolver.getValue(elContext, null, lookupName);
  }

  @Test
  public void getValueForSingleTaskService() throws InvalidSyntaxException {
    String lookupName = "testTaskListener";
    TaskListener serviceObject = new TestTaskListener();
    ServiceReference<TaskListener> reference = mockService(serviceObject);
    registerServiceRefsAtContext(TaskListener.class, null, reference);
    TaskListener value = (TaskListener) resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getValueForTwoTaskServices() throws InvalidSyntaxException {
    String lookupName = "testTaskListener";
    TaskListener serviceObject = new TestTaskListener();
    TaskListener anotherServiceObject = mock(TaskListener.class);
    ServiceReference<TaskListener> reference1 = mockService(serviceObject);
    ServiceReference<TaskListener> reference2 = mockService(anotherServiceObject);
    registerServiceRefsAtContext(TaskListener.class, null, reference1, reference2);
    TaskListener value = (TaskListener) resolver.getValue(elContext, null, lookupName);
    assertThat(value, is(sameInstance(serviceObject)));
    wasPropertyResolved();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void getValueForTaskServicesWithSameName() throws InvalidSyntaxException {
    String lookupName = "testTaskListener";
    TaskListener behaviour1 = new TestTaskListener();
    TaskListener behaviour2 = new TestTaskListener();
    ServiceReference<TaskListener> reference1 = mockService(behaviour1);
    ServiceReference<TaskListener> reference2 = mockService(behaviour2);
    registerServiceRefsAtContext(TaskListener.class, null, reference1, reference2);
    resolver.getValue(elContext, null, lookupName);
  }

  @SuppressWarnings("unchecked")
  private <T> void registerServiceRefsAtContext(Class<T> clazz, String ldapFilter, ServiceReference<T> ref) {
    registerServiceRefsAtContext(clazz, ldapFilter, new ServiceReference[] { ref });
  }

  private <T> void registerServiceRefsAtContext(Class<T> clazz, String ldapFilter, ServiceReference<T>... refs) {
    try {
      if (clazz != null) {
        when(resolver.ctx.getServiceReferences(clazz, ldapFilter)).thenReturn(Arrays.asList(refs));
      } else {
        when(resolver.ctx.getServiceReferences((String) null, ldapFilter)).thenReturn(refs);
      }
    } catch (InvalidSyntaxException e) {
      // wont happen
      fail(e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private <T> ServiceReference<T> mockService(T service) {
    ServiceReference<T> ref = mock(ServiceReference.class);
    when(resolver.ctx.getService(eq(ref))).thenReturn(service);
    return ref;
  }

  private void wasPropertyResolved() {
    verify(elContext).setPropertyResolved(booleanCaptor.capture());
    assertThat(booleanCaptor.getValue(), is(true));
  }

  private class TestOSGiELResolver extends OSGiELResolver {

    private BundleContext ctx;

    @Override
    protected BundleContext getBundleContext() {
      return ctx;
    }

  }

  @Test
  public void isReadonly() {
    assertThat(resolver.isReadOnly(null, null, null), is(true));
  }

  @Test
  public void getCommonPropertyType() {
    assertThat(resolver.getCommonPropertyType(null, new Object()), isA(Object.class));
  }

  @Test
  public void getFeatureDescriptors() {
    assertThat(resolver.getFeatureDescriptors(null, null), is(nullValue()));
  }

  @Test
  public void getValueForGetterProperty() {
    TestJavaBean bean = new TestJavaBean();
    Object value = new Object();
    bean.setValue(value);
    Object object = resolver.getValue(elContext, bean, "value");
    assertThat(object, is(sameInstance(value)));
    wasPropertyResolved();
  }

  @Test
  public void getValueForIsProperty() {
    TestJavaBean bean = new TestJavaBean();
    bean.setCalled(true);
    Boolean object = (Boolean) resolver.getValue(elContext, bean, "called");
    assertThat(object, is(true));
    wasPropertyResolved();
  }

  @Test
  public void getValueForAbsentProperty() {
    TestJavaBean bean = new TestJavaBean();
    resolver.getValue(elContext, bean, "foo");
    verifyZeroInteractions(elContext);
  }

  @Test
  public void setValue() {
    TestJavaBean bean = new TestJavaBean();
    Object value = new Object();
    resolver.setValue(elContext, bean, "value", value);
    assertThat(bean.getValue(), is(sameInstance(value)));
    wasPropertyResolved();
  }

  @Test(expected = PropertyNotFoundException.class)
  public void setValueForAbsentProperty() {
    TestJavaBean bean = new TestJavaBean();
    resolver.setValue(elContext, bean, "bar", new Object());
    verifyZeroInteractions(elContext);
  }

  @Test
  public void invoke() {
    TestJavaBean bean = new TestJavaBean();
    Object value = new Object();
    bean.setValue(value);
    Object invoke = resolver.invoke(elContext, bean, "getValue", new Class[0], null);
    assertThat(invoke, is(sameInstance(value)));
  }

  @Test(expected = MethodNotFoundException.class)
  public void invokeAbsentMethod() {
    TestJavaBean bean = new TestJavaBean();
    resolver.invoke(elContext, bean, "method", new Class[0], null);
    verifyZeroInteractions(elContext);
  }
}
