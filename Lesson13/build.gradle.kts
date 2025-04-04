plugins {
    kotlin("jvm") version "2.1.10"
    id("me.champeau.jmh") version "0.7.3"
}

group = "isel.lae.i43d"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin reflection
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}