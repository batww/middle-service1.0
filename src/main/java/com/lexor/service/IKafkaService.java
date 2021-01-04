package com.lexor.service;

public interface IKafkaService {
    void createTopics(String bootstrapServers);
    boolean sendKafkaMessage(String topic,String key, String payload);
}

