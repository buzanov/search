import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.vla1z"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://build.emdev.ru/nexus/repository/public//")
    }
}



dependencies {
    implementation("org.apache.lucene.morphology:morph:1.0")
    implementation("org.apache.lucene.morphology:russian:1.0")
    implementation("org.apache.lucene:lucene-core:3.5.0")




}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}