package org.example.kafka.issue

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.nondeterministic.continually
import io.kotest.assertions.nondeterministic.continuallyConfig
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import org.example.kafka.issue.utils.SpringTest
import org.springframework.kafka.core.KafkaTemplate
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KafkaBatchIssueApplicationTests(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val rawMessageConsumer: RawMessageConsumer,
    private val consumerRecordMessageConsumer: ConsumerRecordMessageConsumer
) : SpringTest({
    beforeSpec {
        (1..10).map {
            val message = KafkaMessage("message-$it")
            kafkaTemplate.send("batch-issue-topic", Json.encodeToString(message)).asDeferred()

        }.awaitAll()
    }

    "RawMessageConsumer should not consume any messages" {
        // Since the consumers are not configured properly they should not consume any messages and inform about error.
        // Unfortunately RawMessageConsumer is consuming single message, rest of them are lost and there is no information about it.
        continually(continuallyConfig { initialDelay = 3.seconds; duration = 5.seconds },) {
            assertSoftly {
                withClue("[RawMessagesConsumer] should not consume any messages") {
                    rawMessageConsumer.consumedMessages().shouldHaveSize(0)
                }
            }
        }
    }

    "ConsumerRecordMessageConsumer should not consume any messages" {
        continually(continuallyConfig { initialDelay = 3.seconds; duration = 5.seconds },) {
            assertSoftly {
                withClue("[ConsumerRecordMessageConsumer] should not consume any messages") {
                    consumerRecordMessageConsumer.consumedMessages().shouldHaveSize(0)
                }
            }
        }
    }
})
