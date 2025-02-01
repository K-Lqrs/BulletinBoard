plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
}

allprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

	dependencies {
		implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
		implementation("com.h2database:h2:+")
		implementation("net.ririfa:langman:+")
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