package org.example.kafka.issue

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory


@Suppress("unused")
class KafkaMessageDeserializer : Deserializer<KafkaMessage> {
    private val logger = LoggerFactory.getLogger(KafkaMessageDeserializer::class.java)

    override fun deserialize(
        topic: String,
        data: ByteArray?,
    ): KafkaMessage? {
        logger.info("Deserializing message from topic '$topic'")
        return data?.let { Json.decodeFromString(String(data, Charsets.UTF_8)) }
    }
}

@Serializable
data class KafkaMessage(val value: String)