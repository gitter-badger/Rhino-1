/*
  Copyright 2018 Ryos.io.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package io.ryos.rhino.sdk;

import io.ryos.rhino.sdk.data.Context;
import io.ryos.rhino.sdk.data.ContextImpl;
import io.ryos.rhino.sdk.data.Pair;
import io.ryos.rhino.sdk.exceptions.ExceptionUtils;
import io.ryos.rhino.sdk.exceptions.ProfileNotFoundException;
import io.ryos.rhino.sdk.exceptions.SimulationNotFoundException;
import io.ryos.rhino.sdk.runners.SimulationRunner;
import io.ryos.rhino.sdk.utils.Environment;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supervisor which manages set up and run benchmark tests. The class follows the steps required to
 * initiate a test execution i.e configure and search for benchmark jobs by using {@link
 * SimulationJobsScanner}. Once jobs are ready to execute, the implementation starts each one while
 * providing a context to them.
 *
 * <p>A {@link Context} instance is a storage associated with each
 * benchmark job.
 *
 * @author Erhan Bagdemir
 * @see SimulationConfig
 * @see SimulationJobsScanner
 * @see Context
 * @since 1.0
 */
public class SimulationImpl implements Simulation {

  private static final String KEY_PROFILE = "profile";
  private static final String JOB = "job";

  /**
   * A list of simulation runner instances.
   * <p>
   */
  private List<SimulationRunner> simulationRunners;

  /**
   * Constructs a new instance of {@link SimulationImpl}.
   * <p>
   *
   * @param path Path to properties file.
   * @param simulationName Simulation name.
   */
  SimulationImpl(final String path, final String simulationName) {

    try {
      Application.showBranding();

      var environment = getEnvironment();
      var simulationConfig = SimulationConfig.newInstance(path, environment);
      var jobs = SimulationJobsScanner.create().scan(simulationName,
          simulationConfig.getPackageToScan());
      this.simulationRunners = jobs
          .stream()
          .map(simulation -> new Pair<SimulationMetadata, Context>(simulation,
              getContext(simulation)))
          .map(pair -> getRunner(
              pair.getFirst().getRunner(), pair.getSecond()))
          .collect(Collectors.toList());

    } catch (Throwable pe) {
      System.out.println(pe.getMessage());
      System.exit(-1);
    }
  }

  private SimulationRunner getRunner(
      final Class<? extends SimulationRunner> runnerClass,
      final Context context) {

    try {
      var declaredConstructor = runnerClass.getDeclaredConstructor(Context.class);
      return declaredConstructor.newInstance(context);
    } catch (NoSuchMethodException nsm) {
      // implement default constructor creation.
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
      System.err.println(e.getMessage());
    }
    throw new IllegalArgumentException("Runner class: " + runnerClass.getName() + " is invalid.");
  }

  private Environment getEnvironment() {

    var profile = System.getProperty(KEY_PROFILE, Environment.DEV.toString());

    try {
      return Environment.valueOf(profile.toUpperCase());
    } catch (IllegalArgumentException e) {
      ExceptionUtils.rethrow(e, ProfileNotFoundException.class, "ERROR: Environment profile '"
          + profile + "' not found. Dev, Stage, Prod are known environment profiles. Pass "
          + "a valid VM argument e.g -Dprofile=dev");
    }
    return null;
  }

  private ContextImpl getContext(final SimulationMetadata job) {
    var context = new ContextImpl();
    context.add(JOB, job);
    return context;
  }

  @Override
  public void start() {
    try {
      if (simulationRunners.isEmpty()) {
        throw new SimulationNotFoundException(
            "ERROR: No simulation found in '" + SimulationConfig.getPackage() + "'.");
      }
      simulationRunners.forEach(SimulationRunner::start);
    } catch (Throwable t) {
      System.out.println(t.getMessage());
      System.exit(-1);
    }
  }

  @Override
  public void stop() {
    System.out.println("Stopping simulation...");
    simulationRunners.forEach(SimulationRunner::stop);
  }
}
