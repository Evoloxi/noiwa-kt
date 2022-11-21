package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.content.BouncyBeam
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class ChaingunItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
	    val beam = BouncyBeam(world, user.pos.add(0.0, 1.8, 0.0), user.rotationVector, user)
	    beam.bounces = 15
	    beam.maxDistance = 40.0
	    beam.distanceReduction = 0.0
	    beam.spread = 0.3
	    beam.delay = 50L
	    beam.create()
	    
	    user.world.playSound(
		    user,
		    user.blockPos,
		    SoundRegistry.getEchoSound(),
		    net.minecraft.sound.SoundCategory.VOICE,
		    1.0f,
		    0.5f
	    )
	    
	    
        return TypedActionResult.consume(stack)
    }
}
