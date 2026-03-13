val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val koin_version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "3.0.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.instagallery"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}


repositories {
    mavenCentral()
}

dependencies {
    // === Ktor Core ===
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // === Auth ===
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("org.mindrot:jbcrypt:0.4")                    // Password hashing

    // === Database ===
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("com.zaxxer:HikariCP:6.0.0")                 // Connection pool
    implementation("com.mysql:mysql-connector-j:9.0.0")          // MySQL driver
    implementation("redis.clients:jedis:4.4.3")                  // Redis client

    // === Utilities ===
    implementation("io.insert-koin:koin-ktor:$koin_version")            // Dependency Injection
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-rate-limit-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-request-validation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")    // Error handling
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // === Testing ===
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("io.insert-koin:koin-test:$koin_version")
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
