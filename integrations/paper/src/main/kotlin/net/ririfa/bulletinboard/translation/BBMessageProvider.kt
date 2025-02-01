package net.ririfa.bulletinboard.translation

import net.kyori.adventure.text.TextComponent
import net.ririfa.langman.IMessageProvider
import net.ririfa.langman.MessageKey

class BBMessageProvider : IMessageProvider<TextComponent> {
	override fun getLanguage(): String {
		TODO("Not yet implemented")
	}

	override fun getMessage(key: MessageKey<*, *>, vararg args: Any): TextComponent {
		TODO("Not yet implemented")
	}

	override fun getRawMessage(key: MessageKey<*, *>): String {
		TODO("Not yet implemented")
	}

	override fun hasMessage(key: MessageKey<*, *>): Boolean {
		TODO("Not yet implemented")
	}
}