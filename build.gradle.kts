plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.tracker"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "com.tracker.Main"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}