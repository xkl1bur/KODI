import appdependencies.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask
import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

// Declare the task that will monitor all configurations.
configurations.all {
    // 2 Define the resolution strategy in case of conflicts.
    resolutionStrategy {
        // Fail eagerly on version conflict (includes transitive dependencies),
        // e.g., multiple different versions of the same dependency (group and name are equal).
        failOnVersionConflict()

        // Prefer modules that are part of this build (multi-project or composite build) over external modules.
        preferProjectModules()
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.noReflect = true
    kotlinOptions.freeCompilerArgs += listOf(
            "-XXLanguage:+InlineClasses"
    )
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", Versions.kotlin))
}


tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
}

repositories {
    mavenCentral()
}
// comment this apply function if you fork this project
apply {
    from("deploy.gradle")
}
