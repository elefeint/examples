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

package com.example;

import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

  private static final Log LOGGER = LogFactory.getLog(WebController.class);

  @GetMapping("/")
  public String hello() {
    Random rand = new Random();
    Long num = rand.nextLong();

    LOGGER.error("Error: random number is " + num);
    LOGGER.warn("Warning: random number is " + num);
    LOGGER.info("Info: random number is " + num);
    LOGGER.debug("Debug: random number is " + num);
    return "I just logged a random number: " + num;
  }
}
