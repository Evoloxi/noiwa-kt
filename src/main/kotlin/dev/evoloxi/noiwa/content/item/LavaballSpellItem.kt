package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.content.entities.LavaBallEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class LavaballSpellItem(settings: Settings) : ItemWithRarity(settings) {
	override var rarity: ERarity = ERarity.UNCOMMON
	
	override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		val stack = user.getStackInHand(hand)
		val ball = LavaBallEntity(world, user)
		ball.velocity = ball.velocity.multiply(0.5)
		world.spawnEntity(ball)
		return TypedActionResult.success(stack)
	}
}

