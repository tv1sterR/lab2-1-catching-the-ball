plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

javafx {
    version = "17.0.14"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("app.GameApplication")
}
