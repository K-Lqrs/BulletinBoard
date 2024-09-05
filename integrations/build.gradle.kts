plugins {
    id("org.jetbrains.kotlin.jvm")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    }
}

tasks.register("buildAllPlatform") {

}