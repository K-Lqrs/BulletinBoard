pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        val kotlinVersion: String by settings

        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

rootProject.name = "BulletinBoard"

include("integrations")

include("integrations:core")
include("integrations:bungee")
include("integrations:paper")
include("integrations:velocity")