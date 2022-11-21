package dev.evoloxi.noiwa.content.block

import net.minecraft.state.property.EnumProperty
import net.minecraft.util.StringIdentifiable

object PropertyRegistry {
	val PLATE_PART = EnumProperty.of("part", PlatePart::class.java)
}

enum class PlatePart(val id: String) : StringIdentifiable {
	// 3x3 - nine plate parts
	TOP_LEFT("top_left"),
	TOP("top"),
	TOP_RIGHT("top_right"),
	RIGHT("right"),
	BOTTOM_RIGHT("bottom_right"),
	BOTTOM("bottom"),
	BOTTOM_LEFT("bottom_left"),
	LEFT("left"),
	CENTER("center");
	
	override fun toString(): String {
		return id
	}
	
	override fun asString(): String {
		return id
	}
}

