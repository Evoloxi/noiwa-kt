package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.Core.Companion.generic
import dev.evoloxi.noiwa.content.item.ERarity
import dev.evoloxi.noiwa.foundation.Extensions.getBase
import dev.evoloxi.noiwa.foundation.registry.ItemRegistry.AMMO
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

// attributes
object AttributeRegistry {

	@JvmField
	val HEALTH: EntityAttribute = ClampedEntityAttribute("$generic.health", 100.0, 0.0, 102400.0).setTracked(true)
	@JvmField
	val HP_REGEN: EntityAttribute = ClampedEntityAttribute("$generic.health_regeneration", 1.0, 0.0, 1024.0).setTracked(true)
	@JvmField
	val DAMAGE: EntityAttribute = ClampedEntityAttribute("$generic.damage", 1.0, 0.0, 10240000.0).setTracked(true)
	@JvmField
	val STRENGTH: EntityAttribute = ClampedEntityAttribute("$generic.strength", 0.0, 0.0, 102400.0).setTracked(true)
	@JvmField
	val CRIT_DAMAGE: EntityAttribute = ClampedEntityAttribute("$generic.crit_damage", 1.0, 0.0, 102400.0).setTracked(true)
	@JvmField
	val CRIT_CHANCE: EntityAttribute = ClampedEntityAttribute("$generic.crit_chance", 1.0, 0.0, 1024.0).setTracked(true)
	@JvmField
	val ATTACK_SPEED: EntityAttribute = EntityAttributes.GENERIC_ATTACK_SPEED
	@JvmField
	val ECHO: EntityAttribute = ClampedEntityAttribute("$generic.echo", 0.0, 0.0, 1024.0).setTracked(true)
	@JvmField
	val DEFENSE: EntityAttribute = ClampedEntityAttribute("$generic.defense", 0.0, 0.0, 102400.0).setTracked(true)
	// mana, speed, luck, ammo, maxAmmo, cooldown
	@JvmField
	val MANA: EntityAttribute = ClampedEntityAttribute("$generic.mana", 0.0, 0.0, 102400.0).setTracked(true)
	@JvmField
	val MANA_REGEN: EntityAttribute = ClampedEntityAttribute("$generic.mana_regeneration", 0.0, 0.0, 1024.0).setTracked(true)
	@JvmField
	val SPEED: EntityAttribute = EntityAttributes.GENERIC_MOVEMENT_SPEED
	@JvmField
	val LUCK: EntityAttribute = ClampedEntityAttribute("$generic.luck", 0.0, 0.0, 1024.0).setTracked(true)
	//@JvmField
	//val AMMO: EntityAttribute = ClampedEntityAttribute("$generic.ammo", 0.0, 0.0, 102400.0).setTracked(true)
	@JvmField
	val MAX_AMMO: EntityAttribute = ClampedEntityAttribute("$generic.max_ammo", 0.0, 0.0, 102400.0).setTracked(true)
	@JvmField
	val COOLDOWN: EntityAttribute = ClampedEntityAttribute("$generic.cooldown", 0.0, 0.0, 10240.0).setTracked(true)

	private fun registerAttribute(attribute: EntityAttribute) {
        Registry.register(Registry.ATTRIBUTE, Core.id(attribute.translationKey), attribute)
	}
	
	operator fun EntityAttribute.get(stack: ItemStack): Double? {
		// get the attribute from the itemstack nbt
		val nbtList: NbtList = stack.nbt?.getList("AttributeModifiers", NbtElement.COMPOUND_TYPE.toInt()) ?: return null
		for (i in 0 until nbtList.size) {
			val nbtCompound: NbtCompound = nbtList.getCompound(i)
			val attribute = Identifier(nbtCompound.getString("AttributeName"))
			if (attribute == Registry.ATTRIBUTE.getId(this)) {
				return nbtCompound.getDouble("Amount")
			}
		}
		return null
	}
	@JvmStatic
	fun getItemRarity(stack: ItemStack): ERarity {
		return ERarity.fromString(stack.getBase().getString("Rarity") ?: "COMMON")
	}
	
	fun register() {
		// register only clamped attributes
		registerAttribute(HEALTH)
		registerAttribute(HP_REGEN)
		registerAttribute(DAMAGE)
		registerAttribute(STRENGTH)
		registerAttribute(CRIT_DAMAGE)
		registerAttribute(CRIT_CHANCE)
		registerAttribute(ECHO)
		registerAttribute(DEFENSE)
		registerAttribute(MANA)
		registerAttribute(MANA_REGEN)
		registerAttribute(LUCK)
		registerAttribute(MAX_AMMO)
		registerAttribute(COOLDOWN)
	}
}
