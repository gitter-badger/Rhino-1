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

package io.ryos.examples.benchmark;

import io.ryos.rhino.sdk.annotations.Logging;
import io.ryos.rhino.sdk.users.User;

import io.ryos.rhino.sdk.Recorder;
import io.ryos.rhino.sdk.annotations.CleanUp;
import io.ryos.rhino.sdk.annotations.Feeder;
import io.ryos.rhino.sdk.annotations.Prepare;
import io.ryos.rhino.sdk.annotations.Scenario;
import io.ryos.rhino.sdk.annotations.Simulation;
import io.ryos.rhino.sdk.annotations.UserFeeder;
import io.ryos.rhino.sdk.feeders.UUIDFeeder;

/**
 * An example for annotated entity of benchmark job.
 */
@Simulation(name = "helloWorld")
@Logging(file="/var/log/sim.out")
public class RhinoEntity {

    @UserFeeder(max = 10)
    private User user;

    @Feeder(factory = UUIDFeeder.class)
    private String uuid;

    @Prepare
    public void prepare() {
        System.out.println("Preparing the test with user:" + user.getUsername());
    }

    @Scenario(name = "hello")
    public void run(Recorder recorder) {
        System.out.println(uuid + " Hello World! Running test with user:" + user.getUsername());
    }

    @CleanUp
    public void cleanUp() {
        System.out.println("Clean up the test.");
    }
}
