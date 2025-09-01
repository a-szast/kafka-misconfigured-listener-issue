package org.example.kafka.issue

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@SpringBootApplication
class KafkaBatchIssueApplication

fun main(args: Array<String>) {
    runApplication<KafkaBatchIssueApplication>(*args)
}

@Component
class RawMessageConsumer {

    private val logger = LoggerFactory.getLogger(RawMessageConsumer::class.java)
    private val recordedMessages = mutableListOf<KafkaMessage>()

    @KafkaListener(
        topics = ["batch-issue-topic"],
        groupId = "raw-messages",
        batch = "true"
    )
    fun consume(message: KafkaMessage) {
        recordedMessages.add(message)
        logger.info("[RawMessageConsumer] Consumed message '$message'")
    }

    fun consumedMessages() = recordedMessages.toList()
}

@Component
class ConsumerRecordMessageConsumer {

    private val logger = LoggerFactory.getLogger(ConsumerRecordMessageConsumer::class.java)
    private val recordedMessages = mutableListOf<KafkaMessage>()

    @KafkaListener(
        topics = ["batch-issue-topic"],
        groupId = "consumer-record",
        batch = "true"
    )
    fun consume(message: ConsumerRecord<String, KafkaMessage>) {
        recordedMessages.add(message.value())
        logger.info("[ConsumerRecordMessageConsumer] Consumed message '$message'")
    }

    fun consumedMessages() = recordedMessages.toList()
}
