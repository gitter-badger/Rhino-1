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

import io.ryos.rhino.sdk.runners.SimulationRunner;

/**
 * Simulation runner controller. Within a performance testing project, it is likely that there are
 * multiple test classes. The controller runs each test case in an isolated execution context.
 * <p>
 *
 * @author Erhan Bagdemir
 * @version 1.0
 * @see SimulationRunner
 */
public interface Simulation {

  /**
   * Execute all tests, detected by scanning the paths defined in the configuration.
   */
  void start();

  /**
   * Stop all running tests.
   */
  void stop();


  /**
   * Static factory method to create a new {@link Simulation} instance for convenience.
   * <p>
   *
   * @param path Path to the rhino.properties file.
   * @param simulationToRun SimulationMetadata name to be run.
   * @return Simulation instance.
   */
  static Simulation create(String path, String simulationToRun) {
    return new SimulationImpl(path, simulationToRun);
  }
}
