/*************************************************************************
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 * Copyright 2019 Adobe
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe
 * and its suppliers and are protected by all applicable intellectual
 * property laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe.
 **************************************************************************/
package io.ryos.rhino.sdk.runners;

import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

public class Throttler {
  private static final Logger LOG = LoggerFactory.getLogger(Throttler.class);

  public static <T> Function<Flux<T>, Flux<T>> throttle(final Limit... rps) {
    final Flux<Long> reduce = Arrays.stream(rps)
        .map(r -> {
          LOG.debug("throttle: tickNano={}, duration={}", r.tickNano, r.durationSec);
          return Flux.interval(ofNanos(r.tickNano)).take(ofSeconds(r.durationSec));
        })
        .reduce(Flux::concatWith).orElse(Flux.generate(sink -> sink.next(0L)));
    final UnaryOperator<Flux<T>> res = f ->
        f.zipWith(reduce.concatWith(Flux.generate(sink -> sink.next(0L))))
            .map(Tuple2::getT1);
    return res;
  }

  public static class Limit {
    final long durationSec;
    final long tickNano;

    private Limit(final long requestsPerSecond, final long durationSec) {
      this.durationSec = durationSec;
      this.tickNano = (long) Math.floor((1d / requestsPerSecond) * 1e9);
    }

    public static Limit of(final long requestsPerSecond, final Duration duration) {
      return new Limit(requestsPerSecond, duration.toSeconds());
    }
  }
}
