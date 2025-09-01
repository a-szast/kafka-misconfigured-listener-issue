package org.example.kafka.issue.utils

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringAutowireConstructorExtension

object KotestProjectConfig : AbstractProjectConfig() {
    override val isolationMode: IsolationMode = IsolationMode.SingleInstance

    override val extensions: List<Extension> =
        super.extensions + listOf(SpringAutowireConstructorExtension)

}