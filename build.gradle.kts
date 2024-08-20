import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.0"
    id("idea")
    id("io.github.goooler.shadow") version "8.1.4"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "sh.miles"
version = "2.0.0-SNAPSHOT"
val debugLibraries = true

repositories {
    mavenCentral()
    maven("https://maven.miles.sh/pineapple")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT") { isChanging = true }
    implementation("sh.miles:pineapple-bundle:1.0.0-SNAPSHOT") { isChanging = true }
    implementation("sh.miles:pineapple-infstack:1.0.0-SNAPSHOT") { isChanging = true }
    implementation("sh.miles:pineapple-tiles:1.0.0-SNAPSHOT") { isChanging = true }
    bukkitLibrary(kotlin("stdlib"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks.shadowJar {
    this.archiveClassifier = ""
    this.archiveVersion = ""
    archiveFileName = "PineappleCollectors-${project.version}.jar"

    val packageName = "${project.group}.${project.name.lowercase()}"
    this.relocate("sh.miles.pineapple.*", "$packageName.libs.pineapple")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

bukkit {
    name = "ArcticCollectors"
    version = project.version.toString()
    main = "sh.miles.${project.name.lowercase()}.${project.name}Plugin"
    apiVersion = "1.20.4" // LATEST
}

kotlin {
    jvmToolchain(17)
}

if (debugLibraries) {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}
