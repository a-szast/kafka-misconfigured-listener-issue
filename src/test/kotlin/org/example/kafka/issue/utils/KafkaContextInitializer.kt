package org.example.kafka.issue.utils

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.test.context.event.AfterTestClassEvent
import  org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

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

        applicationContext.environment.propertySources.addFirst(properties)
    }
}
