package org.example.kafka.issue

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SendMessageController(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val rawMessageConsumer: RawMessageConsumer,
    private val consumerRecordMessageConsumer: ConsumerRecordMessageConsumer,
) {

    @GetMapping("/messages")
    fun getMessages(): Map<String, List<KafkaMessage>> {
        return mapOf(
            "raw" to rawMessageConsumer.consumedMessages(),
            "consumerRecord" to consumerRecordMessageConsumer.consumedMessages(),
        )
    }

    @PostMapping("/send")
    fun send(@RequestBody message: KafkaMessage) {
        kafkaTemplate.send("batch-issue-topic", Json.encodeToString(message)).get()
    }

    @PostMapping("/send-multiple")
    suspend fun sendMultiple(@RequestBody message: KafkaMessage) {
        (1..10).map {
            kafkaTemplate.send("batch-issue-topic", Json.encodeToString(message)).asDeferred()
        }.awaitAll()
    }
}