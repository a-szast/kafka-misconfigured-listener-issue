package org.example.kafka.issue.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import org.example.kafka.issue.KafkaMessage
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.event.AfterTestClassEvent
import  org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

val kafkaMessageSerializer: Serializer<KafkaMessage> =
    Serializer<KafkaMessage> { _, data ->
        Json.encodeToString<KafkaMessage>(data).toByteArray()
    }


class KafkaContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val kafkaContainer = KafkaContainer(DockerImageName.parse("apache/kafka:3.9.1"))
            .also { it.start() }

        applicationContext.addApplicationListener {
            if (it is AfterTestClassEvent) {
                kafkaContainer.stop()
            }
        }

        val properties =
            MapPropertySource(
                "KafkaContextInitializerSource",
                mapOf(
                    "spring.kafka.bootstrap-servers" to kafkaContainer.bootstrapServers,
                    "spring.kafka.properties.security.protocol" to "PLAINTEXT",
                    "spring.kafka.consumer.auto-offset-reset" to "earliest",
                ),
            )

        val kafkaTemplate =
            KafkaTemplate(
                DefaultKafkaProducerFactory(
                    KafkaTestUtils.producerProps(kafkaContainer.bootstrapServers),
                    StringSerializer(),
                    kafkaMessageSerializer,
                ),
            )

        applicationContext.beanFactory.registerSingleton("kafkaTemplate", kafkaTemplate)
        applicationContext.environment.propertySources.addFirst(properties)
    }
}
