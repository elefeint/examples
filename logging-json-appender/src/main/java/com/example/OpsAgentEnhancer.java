package com.example;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.cloud.spring.logging.JsonLoggingEventEnhancer;
import java.util.Map;

public class OpsAgentEnhancer implements JsonLoggingEventEnhancer {

  @Override
  public void enhanceJsonLogEntry(Map<String, Object> jsonMap, ILoggingEvent event) {
    jsonMap.put("logging.googleapis.com/severity", event.getLevel().toString());
  }
}
