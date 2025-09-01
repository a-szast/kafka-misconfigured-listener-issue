package org.example.kafka.issue

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
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

@Component
class OnStartupMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) = runBlocking {
        (1..5).map {
            kafkaTemplate.send(
                "batch-issue-topic",
                Json.encodeToString(KafkaMessage("message-${it}"))
            ).asDeferred()
        }.awaitAll()

        Unit
    }
}