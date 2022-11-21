package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.foundation.Extensions.toVec3f
import net.minecraft.text.Text

enum class ERarity(val color: Int, val plate : String) {
	COMMON(0xEEEEEE, "ꐝ"),
	UNCOMMON(0x95DC26, "ꐓ"),
	RARE(0x7575FF, "ꐕ"),
	EPIC(0xB13DEB, "ꐔ"),
	LEGENDARY(0xFFAA00, "ꐑ"),
	MYTHIC(0xFF0000, "ꐙ"),
	ETERNAL(0xFFFFFF, "Eternal"),
	SPECIAL(0xFF5500, "ꐖ");
	
	override fun toString() = plate
	
	fun color() = color
	fun colorVec() = color.toVec3f()
	fun coloredString() = Text.literal(plate).styled { it.withColor(color) }
	companion object {
		fun fromString(string: String) = values().firstOrNull { it.name.equals(string, true) } ?: RARE
	}
}
