plugins {
    id("org.jetbrains.kotlin.jvm")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation("org.json:json:20240303")
        implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
        implementation("net.rk4z:beacon:1.4.5")
    }
}

tasks.register("buildAllPlatform") {

}