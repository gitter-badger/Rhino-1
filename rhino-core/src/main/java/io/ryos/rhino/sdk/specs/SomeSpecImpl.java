/*
 * Copyright 2018 Ryos.io.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package io.ryos.rhino.sdk.specs;

import io.ryos.rhino.sdk.data.UserSession;
import io.ryos.rhino.sdk.reporting.Measurement;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Spec implementation for arbitrary code execution.
 * <p>
 *
 * @author Erhan Bagdemir
 * @since 1.1.0
 */
public class SomeSpecImpl extends AbstractSpec implements SomeSpec {

  private BiFunction<UserSession, Measurement, UserSession> function;

  public SomeSpecImpl(final String measurement) {
    super(Objects.requireNonNull(measurement));
  }

  @Override
  public Spec as(final BiFunction<UserSession, Measurement, UserSession> function) {
    this.function = Objects.requireNonNull(function);
    return this;
  }

  @Override
  public BiFunction<UserSession, Measurement, UserSession> getFunction() {
    return function;
  }
}
