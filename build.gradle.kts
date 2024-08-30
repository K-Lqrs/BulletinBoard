plugins {
    id("org.jetbrains.kotlin.jvm")
}

group = "net.rk4z"
version = "1.0.0"

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.md-5.net/content/groups/public/")
    }
}

tasks.register("buildAll") {
    dependsOn(":integrations:buildAllPlatform")
}