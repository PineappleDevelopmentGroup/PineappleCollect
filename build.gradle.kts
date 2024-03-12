plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "sh.miles"
version = "1.0.0-SNAPSHOT"
val debugLibraries = true

repositories {
    mavenCentral()
    maven("https://maven.miles.sh/libraries")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT") { isChanging = true }
    implementation("sh.miles:Pineapple:1.0.0-SNAPSHOT") {
        isChanging = true
    }
    implementation(kotlin("stdlib"))
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
}

tasks.shadowJar {
    this.archiveClassifier = ""
    this.archiveVersion = ""
    archiveFileName = "${project.name}-${project.version}.jar"

    val packageName = "${project.group}.${project.name.lowercase()}"
    this.relocate("kotlin", "$packageName.shaded.kotlin")
    this.relocate("sh.miles.pineapple", "$packageName.libs.pineapple")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

bukkit {
    name = project.name
    version = project.version.toString()
    main = "sh.miles.${project.name.lowercase()}.${project.name}Plugin"
    apiVersion = "1.20" // LATEST
}

kotlin {
    jvmToolchain(17)
}

if (debugLibraries) {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}
