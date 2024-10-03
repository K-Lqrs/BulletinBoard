import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-base:3.0.2")
    implementation("net.rk4z:igf:1.0.0")

    library("com.google.code.gson:gson:2.10.1")
    paperLibrary("com.google.code.gson:gson:2.10.1")
}

paper {
    main = "net.rk4z.bulletinboard.BulletinBoard"
    generateLibrariesJson = false
    foliaSupported = false
    apiVersion = "1.21"
    version = rootProject.version.toString()
    name = rootProject.name
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    authors = listOf("Lars", "cotrin_d8")
    description = "A simple bulletin board plugin"

    serverDependencies {
        register("Kotlin") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("LuckPerms") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    permissions {
        register("bulletinboard.*") {
            children = listOf(
                "bulletinboard.admin",
                "bulletinboard.gui.use",
                "bulletinboard.post.delete.own",
                "bulletinboard.post.create",
                "bulletinboard.post.edit.own",
                "bulletinboard.post.view",
                "bulletinboard.post.anonymous"
            )

            childrenMap = mapOf(
                "bulletinboard.admin" to true,
                "bulletinboard.gui.use" to true,
                "bulletinboard.post.delete.own" to true,
                "bulletinboard.post.create" to true,
                "bulletinboard.post.edit.own" to true,
                "bulletinboard.post.view" to true,
                "bulletinboard.post.anonymous" to true,
                "bulletinboard.post.delete.other" to true,
                "bulletinboard.post.edit.other" to true,
                "bulletinboard.reload" to true
            )
        }

        register("bulletinboard.admin") {
            description = "Allows the player to use all admin commands"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf(
                "bulletinboard.post.delete.other",
                "bulletinboard.post.edit.other",
                "bulletinboard.reload"
            )
        }

        register("bulletinboard.gui.use") {
            description = "Allows the player to use the bulletinboard GUI"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("bulletinboard.post.delete.own") {
            description = "Allows the player to delete posts"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("bulletinboard.post.create") {
            description = "Allows the player to create posts"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("bulletinboard.post.edit.own") {
            description = "Allows the player to edit their own posts"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("bulletinboard.post.view") {
            description = "Allows the player to view posts"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("bulletinboard.post.anonymous") {
            description = "Allows the player to post anonymously"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("bulletinboard.post.delete.other") {
            description = "Allows the player to delete other players' posts"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("bulletinboard.post.edit.other") {
            description = "Allows the player to edit other players' posts"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("bulletinboard.post.debug") {
            description = "Allows the player to add a debug post"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("bulletinboard.reload") {
            description = "Allows the player to reload the plugin"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().map { file ->
            if (file.isDirectory) file else { zipTree(file) }
        }
    })
}
