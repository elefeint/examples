/*
 * Copyright 2022-2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.benchmark;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class App {

  public static void main(String[] args) throws IOException {
    org.openjdk.jmh.Main.main(args);
  }

  @State(Scope.Benchmark)
  public static class LargeIntArray {
    int[] data = new int[1_000_000];

    @Setup
    public void setup() {
      Random rand = new Random();
      for (int i = 0; i < data.length; i++) {
        data[i] = rand.nextInt();
      }
    }
  }

  @State(Scope.Benchmark)
  public static class SmallIntArray {
    int[] data = new int[] { 13, 27, 194, 23242, 9393022};
  }

  @Benchmark
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public void copySmallArrayWithLoop(SmallIntArray arr, Blackhole blackhole) {

    long[] result = toLongArray(arr.data);

    blackhole.consume(result);

  }

  @Benchmark
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public void copySmallArrayWithStream(SmallIntArray arr, Blackhole blackhole) {
    long[] result = IntStream.of(arr.data).mapToLong(x -> x).toArray();

    blackhole.consume(result);

  }

  @Benchmark
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  public void copyLargeArrayWithLoop(LargeIntArray arr, Blackhole blackhole) {

    long[] result = toLongArray(arr.data);

    blackhole.consume(result);

  }

  @Benchmark
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  public void copyLargeArrayWithStream(LargeIntArray arr, Blackhole blackhole) {
    long[] result = IntStream.of(arr.data).mapToLong(x -> x).toArray();

    blackhole.consume(result);

  }

  private static long[] toLongArray(int[] input) {
    if (input == null) {
      return new long[0];
    }
    long[] output = new long[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = input[i];
    }
    return output;
  }

}
