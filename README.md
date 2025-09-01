About the issue:

With a batch listener set up in this way (it’s incorrectly configured, of course, but I think it should provide some clues about the configuration issue like it does when using ConsumerRecord as parameter).
```
    @KafkaListener(
        topics = ["batch-issue-topic"],
        groupId = "raw-messages",
        batch = "true"
    )
    fun consume(message: KafkaMessage) {
        recordedMessages.add(message)
        logger.info("[RawMessageConsumer] Consumed message '$message'")
    }
```
There is no error visible, single message from batch is consumed while the rest of them is silently skipped. Example logs:
```
2025-09-01T09:49:30.938+02:00  INFO 16617 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.e.k.issue.KafkaMessageDeserializer     : Deserializing message from topic 'batch-issue-topic'
2025-09-01T09:49:30.943+02:00  INFO 16617 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.e.k.issue.KafkaMessageDeserializer     : Deserializing message from topic 'batch-issue-topic'
2025-09-01T09:49:30.943+02:00  INFO 16617 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.e.k.issue.KafkaMessageDeserializer     : Deserializing message from topic 'batch-issue-topic'
2025-09-01T09:49:30.943+02:00  INFO 16617 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.e.k.issue.KafkaMessageDeserializer     : Deserializing message from topic 'batch-issue-topic'
2025-09-01T09:49:30.943+02:00  INFO 16617 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.e.k.issue.KafkaMessageDeserializer     : Deserializing message from topic 'batch-issue-topic'
2025-09-01T09:49:30.947+02:00  INFO 16617 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.e.kafka.issue.RawMessageConsumer       : [RawMessageConsumer] Consumed message 'KafkaMessage(value=message-1)'
```

It looks ok when parameter type is `message: ConsumerRecord<String, KafkaMessage>`. The logs then looks fine:

```
2025-09-01T09:57:37.225+02:00 ERROR 17371 --- [kafka-batch-issue] [ntainer#0-0-C-1] o.s.k.l.FallbackBatchErrorHandler        : Records discarded: batch-issue-topic-0@77,batch-issue-topic-0@78,batch-issue-topic-0@79,batch-issue-topic-0@80,batch-issue-topic-0@81,batch-issue-topic-0@82,batch-issue-topic-0@83,batch-issue-topic-0@84,batch-issue-topic-0@85,batch-issue-topic-0@86

org.springframework.kafka.listener.ListenerExecutionFailedException: Listener method could not be invoked with the incoming message
Endpoint handler details:
Method [public void org.example.kafka.issue.ConsumerRecordMessageConsumer.consume(org.apache.kafka.clients.consumer.ConsumerRecord<java.lang.String, org.example.kafka.issue.KafkaMessage>)]
Bean [org.example.kafka.issue.ConsumerRecordMessageConsumer@24e616ab]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.decorateException(KafkaMessageListenerContainer.java:2994) ~[spring-kafka-3.3.9.jar:3.3.9]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.doInvokeBatchOnMessage(KafkaMessageListenerContainer.java:2473) ~[spring-kafka-3.3.9.jar:3.3.9]
```


Running the app:
- make sure there is a kafka running on `localhost:9092`
  - the configuration is in `application.yml`
  - if needed there is a `docker-compose.yaml` file in main directory
- run spring app `./gradlew bootRun`

The application has `ApplicationListener<ApplicationReadyEvent>` configured that sends 5 messages on startup so the issue can be immediately observed.


There is also a test added that can be run by `./gradlew test`
