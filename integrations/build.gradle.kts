plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        implementation("com.google.guava:guava:33.3.0-jre")
        implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.20")
        implementation("org.json:json:20240303")
        implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
        implementation("net.rk4z:beacon:1.4.5")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.3")
        implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.20")
    }
}

tasks.register("buildAllPlatform") {
    dependsOn("paper:jar", "bungee:jar", "velocity:jar")

    doLast {
        val buildDir = "$rootDir/artifacts"
        println("Build directory: $buildDir")

        if (!file(buildDir).exists()) {
            file(buildDir).mkdir()
            println("Created build directory: $buildDir")
        }

        val (rel, maj, min) = rootProject.version.toString().split(".")
        println("Version: $rel.$maj.$min")

        val platformJars = mapOf(
            "paper" to "${rootProject.name}-paper-$rel.$maj.$min.jar",
            "bungee" to "${rootProject.name}-bungee-$rel.$maj.$min.jar",
            "velocity" to "${rootProject.name}-velocity-$rel.$maj.$min.jar"
        )

        platformJars.forEach { (platform, outputJarName) ->
            val platformDir = file("$platform/build/libs")
            println("Platform directory for $platform: $platformDir")

            val jarFiles = platformDir.listFiles { file -> file.extension == "jar" }
            if (jarFiles.isNullOrEmpty()) {
                println("No JAR files found for platform $platform.")
                return@forEach
            }

            val outputJar = file("$buildDir/$outputJarName")
            println("Output JAR will be created at: $outputJar")

            ant.withGroovyBuilder {
                "jar"("destfile" to outputJar, "duplicate" to "preserve") {
                    jarFiles.forEach { jarFile ->
                        println("Merging JAR file: $jarFile")
                        "zipfileset"("src" to jarFile)
                    }

                    configurations.runtimeClasspath.get().filter { artifact ->
                        !artifact.name.startsWith("kotlin") && !artifact.name.startsWith("slf4j")
                    }.forEach { artifact ->
                        println("Merging artifact: ${artifact.name}")
                        "zipfileset"("src" to artifact)
                    }
                }
            }

            println("Merged $platform JAR to $outputJar")
        }
    }
}