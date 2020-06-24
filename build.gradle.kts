plugins {
    kotlin("jvm") version "1.3.71"
}

group = "puc"
version = "1.0-SNAPSHOT"

val ktor_version = "1.3.2"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.2")
    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("com.google.code.gson:gson:2.8.6")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}