package dev.evoloxi.noiwa.foundation.handler

import com.mojang.brigadier.context.CommandContext
import dev.evoloxi.noiwa.content.item.ERarity
import dev.evoloxi.noiwa.content.item.ItemWithRarity
import dev.evoloxi.noiwa.foundation.component.CustomComponents
import dev.evoloxi.noiwa.foundation.handler.AttributeHandler.eRarity
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry.get
import dev.evoloxi.noiwa.foundation.registry.EnumRegistry
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.command.api.EnumArgumentType

object AttributeHandler {
	fun addAttribute(context: CommandContext<ServerCommandSource>) {
		try {
			val player = context.source.player
			val nbt = player.mainHandStack.orCreateNbt
			val value = context.getArgument("value", Double::class.java)
			val enum = EnumArgumentType.getEnumConstant(context, "stat", EnumRegistry.Stats::class.java)
			val attribute = enum.attribute
			// add attribute to nbt, if the supplied value is 0, remove the attribute
			val nbtList = nbt.getList("AttributeModifiers", 10)
			nbtList.removeIf { (it as NbtCompound).getString("AttributeName") == Registry.ATTRIBUTE.getId(attribute).toString() }
			if (value != 0.0) {
				player.mainHandStack.addAttributeModifier(
					attribute, EntityAttributeModifier(
						Registry.ATTRIBUTE.getId(attribute).toString(),
						value * enum.scale,
						EntityAttributeModifier.Operation.ADDITION
					), null
				)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
	}
	
	val ItemStack.eRarity: ERarity
		get() {
			val eRarity = (this.item as? ItemWithRarity)?.rarity ?: run {
				when (this.rarity) {
					Rarity.COMMON -> ERarity.COMMON
					Rarity.UNCOMMON -> ERarity.UNCOMMON
					Rarity.RARE -> ERarity.RARE
					else -> ERarity.EPIC
				}
			}
			return eRarity
		}
	val ItemStack.stats
		get() = ItemStats(this)
}

class ItemStats(stack: ItemStack) {
	val damage by lazy { AttributeRegistry.DAMAGE[stack] ?: 0.0 }
	val strength by lazy { AttributeRegistry.STRENGTH[stack] ?: 0.0 }
	val critChance by lazy { AttributeRegistry.CRIT_CHANCE[stack] ?: 0.0 }
	val critDamage by lazy { AttributeRegistry.CRIT_DAMAGE[stack] ?: 0.0 }
	val echo by lazy { AttributeRegistry.ECHO[stack] ?: 0.0 }
	val defense by lazy { AttributeRegistry.DEFENSE[stack] ?: 0.0 }
	val health by lazy { AttributeRegistry.HEALTH[stack] ?: 0.0 }
	val mana by lazy { AttributeRegistry.MANA[stack] ?: 0.0 }
	val speed by lazy { AttributeRegistry.SPEED[stack] ?: 0.0 }
	val attackSpeed by lazy { AttributeRegistry.ATTACK_SPEED[stack] ?: 0.0 }
	val luck by lazy { AttributeRegistry.LUCK[stack] ?: 0.0 }
	val eRarity by lazy { stack.eRarity }
	val cooldown by lazy { AttributeRegistry.COOLDOWN[stack] ?: 0.0 }
}
