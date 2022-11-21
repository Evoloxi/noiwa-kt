package dev.evoloxi.noiwa.content.item

import net.minecraft.item.Item

open class ItemWithRarity(settings: Settings) : Item(settings) {
	open var rarity: ERarity = ERarity.COMMON
}
