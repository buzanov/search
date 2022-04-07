import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    application
}
group = "me.vla1z"
version = "1.0"

repositories {
    mavenCentral()
    flatDir {
        dirs("$projectDir/libs")
    }
}
dependencies {
    implementation(":task2-1.0-standalone")
    implementation(":task2-1.0")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
application {
    mainClass.set("IndexSearcherKt")
}