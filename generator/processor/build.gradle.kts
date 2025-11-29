plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation("com.squareup:kotlinpoet-ksp:2.0.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.0-1.0.29")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = "com.github.romanthekulikov"
                artifactId = "processor"
                version = "0.1.2"
            }
        }
    }
}