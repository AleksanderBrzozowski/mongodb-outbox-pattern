plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jmailen.kotlinter") version "3.12.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "pl.brzozowski"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.debezium:debezium-bom:1.9.6.Final"))

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-core:1.4.1")
    implementation("ch.qos.logback:logback-classic:1.4.1")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")

    implementation("org.litote.kmongo:kmongo:4.7.1")

    implementation("io.debezium:debezium-api")
    implementation("io.debezium:debezium-embedded")
    implementation("io.debezium:debezium-connector-mongodb")


    testImplementation(platform("io.kotest:kotest-bom:5.4.2"))
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.17.4"))

    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.3.4")
    testImplementation("org.testcontainers:mongodb")

}

tasks.test {
    useJUnitPlatform()
}
