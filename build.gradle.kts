plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.kotest") version "6.0.1"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "kafka-batch-issue"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("io.kotest:kotest-framework-engine:6.0.1")
    testImplementation("io.kotest:kotest-runner-junit5:6.0.1")
    testImplementation("org.testcontainers:kafka:1.21.3")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    systemProperty("kotest.framework.config.fqn", "org.example.kafka.issue.utils.KotestProjectConfig")
    useJUnitPlatform()
}
