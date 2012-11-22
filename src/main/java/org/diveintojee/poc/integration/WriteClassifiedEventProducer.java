package org.diveintojee.poc.integration;

/**
 * User: lgueye Date: 22/11/12 Time: 14:21
 */
public interface WriteClassifiedEventProducer {

  void unregisterListener(WriteClassifiedEventListener writeClassifiedEventListener);

  void registerListener(WriteClassifiedEventListener writeClassifiedEventListener);
}
