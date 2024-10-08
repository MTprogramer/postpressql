
val kotlin_version: String by project
val logback_version: String by project



plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-1"
    id("com.github.johnrengelman.shadow") version "8.1.1" // Add the Shadow plugin here
}

group = "com.example"
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
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    implementation ("org.jetbrains.exposed:exposed-jdbc:0.42.0")
    implementation ("org.jetbrains.exposed:exposed-dao:0.42.0")
    implementation ("org.postgresql:postgresql:42.6.0")
    implementation ("com.zaxxer:HikariCP:5.0.1")

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.0") // or another stable version

    // For Ktor and JWT
    implementation("io.ktor:ktor-server-auth:2.3.4")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.4")
    implementation(kotlin("script-runtime"))

}
