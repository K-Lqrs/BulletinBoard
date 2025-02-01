package net.ririfa.bulletinboard

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.ririfa.bulletinboard.translation.BBMessageKey
import net.ririfa.bulletinboard.translation.BBMessageProvider
import net.ririfa.langman.InitType
import net.ririfa.langman.LangMan
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BulletinBoard : JavaPlugin() {
	companion object {
		val logger: Logger = LoggerFactory.getLogger(BulletinBoard::class.simpleName)

		val availableLang = listOf("en", "ja")
	}

	private val langDir = dataFolder.resolve("lang")

	override fun onLoad() {
		val lm = LangMan.createNew<BBMessageProvider, TextComponent>(
			{ Component.text(it) },
			BBMessageKey::class
		)

		lm.init(InitType.YAML, langDir, availableLang)
	}
}