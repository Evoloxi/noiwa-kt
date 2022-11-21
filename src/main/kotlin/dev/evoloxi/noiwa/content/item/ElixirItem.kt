package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.foundation.Extensions.toVec3f
import net.minecraft.entity.player.PlayerEntity
import dev.evoloxi.noiwa.content.entities.ElixirEffectTypes
import dev.evoloxi.noiwa.content.entities.ElixirEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World

open class ElixirItem(settings: Settings) : ItemWithRarity(settings) {
	
	open val effectType: ElixirEffectTypes = ElixirEffectTypes.HARMING
	open val duration: Int = 1
	open val amplifier: Int = 4
	open val radius: Double = 2.0
	open val startColor: Vec3f =  0x4f1d4d.toVec3f()
	open val endColor: Vec3f = 0xe64239.toVec3f()
	
	override var rarity: ERarity = ERarity.UNCOMMON
	
	override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		val itemStack = user.getStackInHand(hand)
		if (!world.isClient) {
			world.playSound(null, user.x, user.y, user.z, SoundEvents.ENTITY_SPLASH_POTION_THROW, user.soundCategory, 1.0f, 1.0f)
			
			val elixirEntity = ElixirEntity(world, user)
			val userRot = user.rotationVector
			elixirEntity.setItem(itemStack)
			elixirEntity.setProperties(user, user.pitch, user.yaw, -20.0f, 0.5f, 1.0f)
			elixirEntity.velocity = userRot.add(elixirEntity.velocity)
			elixirEntity.effect = ElixirEffectTypes.HARMING
			elixirEntity.amplifier = amplifier
			elixirEntity.duration = duration
			elixirEntity.radius = radius
			elixirEntity.startColor = startColor
			elixirEntity.endColor = endColor
			world.spawnEntity(elixirEntity)
			
		}
		user.incrementStat(Stats.USED.getOrCreateStat(this))
		if (!user.abilities.creativeMode) itemStack.decrement(1)
		
		return TypedActionResult.success(itemStack, world.isClient())
	}
}


