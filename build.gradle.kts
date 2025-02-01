plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
}

group = "net.ririfa"
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
		maven("https://oss.sonatype.org/content/repositories/snapshots")
		maven("https://repo.ririfa.net/maven2/")
	}
}

tasks.register("buildAll") {
	dependsOn("integrations:buildAllPlatform")
}