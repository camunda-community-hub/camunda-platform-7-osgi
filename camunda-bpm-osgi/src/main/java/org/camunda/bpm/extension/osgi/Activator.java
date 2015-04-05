/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.osgi;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.extension.osgi.application.ProcessApplicationDeployer;
import org.camunda.bpm.extension.osgi.container.OSGiRuntimeContainerDelegate;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * OSGi Activator
 * 
 * @author <a href="gnodet@gmail.com">Guillaume Nodet</a>
 * @author Ronny Bräunlich
 * @author Daniel Meyer
 * @author Roman Smirnov
 */
public class Activator implements BundleActivator {

  private List<Runnable> callbacks = new ArrayList<Runnable>();

  @Override
  public void start(BundleContext context) throws Exception {

    RuntimeContainerDelegate.INSTANCE.set(new OSGiRuntimeContainerDelegate(context));

    callbacks.add(new Tracker(new Extender(context)));

    ServiceTracker<ProcessApplicationInterface, ProcessApplicationInterface> paDeployer = new ServiceTracker<ProcessApplicationInterface, ProcessApplicationInterface>(context, ProcessApplicationInterface.class, new ProcessApplicationDeployer());
    callbacks.add(new CloseTrackerCallback<ProcessApplicationInterface, ProcessApplicationInterface>(paDeployer, true));

  }

  @Override
  public void stop(BundleContext context) throws Exception {
    for (Runnable r : callbacks) {
      r.run();
    }
  }

  private static class Tracker implements Runnable {

    private final Extender extender;

    private Tracker(Extender extender) {
      this.extender = extender;
      this.extender.open();
    }

    @Override
    public void run() {
      extender.close();
    }
  }

  private static class CloseTrackerCallback<S, T> implements Runnable {

    protected ServiceTracker<S, T> tracker;

    public CloseTrackerCallback(ServiceTracker<S, T> tracker, boolean isTrackAllServices) {
      this.tracker = tracker;
      tracker.open(isTrackAllServices);
    }

    @Override
    public void run() {
      tracker.close();
    }

  }

}
