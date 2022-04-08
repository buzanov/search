import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.vla1z"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://build.emdev.ru/nexus/repository/public//")
    }
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}



dependencies {
    implementation("org.apache.lucene.morphology:morph:1.0")
    implementation("org.apache.lucene.morphology:russian:1.0")
    implementation("org.apache.lucene.morphology:english:1.0")
    implementation("org.apache.lucene:lucene-core:3.5.0")


}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}