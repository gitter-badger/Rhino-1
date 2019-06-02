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

package io.ryos.rhino.sdk.reporting;

/**
 * @author Erhan Bagdemir
 */
public class LogEvent {

  public String username;
  public int userId;
  public String scenario;
  public long start;
  public long end;
  public long elapsed;

  public String getUsername() {
    return username;
  }

  public int getUserId() {
    return userId;
  }

  public String getScenario() {
    return scenario;
  }

  public long getStart() {
    return start;
  }

  public long getEnd() {
    return end;
  }

  public long getElapsed() {
    return elapsed;
  }
}
