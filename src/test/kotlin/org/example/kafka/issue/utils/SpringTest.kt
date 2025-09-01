package org.example.kafka.issue.utils

import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration

@DirtiesContext
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(initializers = [KafkaContextInitializer::class])
abstract class SpringTest(
    body: StringSpec.() -> Unit = {},
) : StringSpec(body)
