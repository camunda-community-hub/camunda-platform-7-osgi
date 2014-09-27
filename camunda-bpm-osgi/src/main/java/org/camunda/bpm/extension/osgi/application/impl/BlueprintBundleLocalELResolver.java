package org.camunda.bpm.extension.osgi.application.impl;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Set;

import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.osgi.service.blueprint.container.BlueprintContainer;

public class BlueprintBundleLocalELResolver extends ELResolver {

  private BlueprintContainer blueprintContainer;

  @Override
  public Class<?> getCommonPropertyType(ELContext context, Object base) {
    return Object.class;
  }

  @Override
  public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
    return null;
  }

  @Override
  public Class<?> getType(ELContext context, Object base, Object property) {
    return Object.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object getValue(ELContext context, Object base, Object property) {
    if(base == null){
      Set<String> componentIds = getBlueprintContainer().getComponentIds();
      if(componentIds.contains(property.toString())){
        context.setPropertyResolved(true);
        return getBlueprintContainer().getComponentInstance(property.toString());
      }
    }
    return null;
  }

  @Override
  public boolean isReadOnly(ELContext context, Object base, Object property) {
    return false;
  }

  @Override
  public void setValue(ELContext context, Object base, Object property, Object value) {
  }

  public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
    this.blueprintContainer = blueprintContainer;
  }
  
  public BlueprintContainer getBlueprintContainer(){
    return blueprintContainer;
  }

}
