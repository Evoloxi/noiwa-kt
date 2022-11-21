package dev.evoloxi.noiwa.content.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.state.property.Properties.EXTENDED
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import software.bernie.geckolib3.core.AnimationState
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.event.predicate.AnimationEvent
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory

class SpikeTrapTile(pos: BlockPos, state: BlockState) : BlockEntity (
    TileRegistry.SPIKE_TRAP_TILE, pos, state), IAnimatable {
    private val factory = AnimationFactory(this)
	private var isExtended = false

	private fun <E : IAnimatable> predicate(event: AnimationEvent<E>): PlayState {
		val controller = event.controller
		controller.transitionLengthTicks = 0.4
		if (world?.getBlockEntity(pos) != null) {
			if (world?.getBlockState(pos)?.get(EXTENDED) == true && controller.currentAnimation != null) {
				if (controller.currentAnimation.animationName == "trigger2" && controller.animationState == AnimationState.Stopped || isExtended) {
					isExtended = true
					controller.setAnimation(AnimationBuilder().addAnimation("idle2", true))
				} else {
					controller.setAnimation(AnimationBuilder().addAnimation("trigger2"))
				}
			} else if (world?.getBlockState(pos)?.get(EXTENDED) == false && controller.currentAnimation == null && !isExtended) {
				controller.isJustStarting = true
			}
			else {
				isExtended = false
				controller.setAnimation(AnimationBuilder().addAnimation("retract2"))
			}
			// don't play an animation if the block was just placed
			
		}
		return PlayState.CONTINUE
	}

    override fun registerControllers(data: AnimationData) {
        data.addAnimationController(
            AnimationController(this, "controller", 0f, ::predicate)
        )
    }
	

    override fun getFactory(): AnimationFactory {
        return factory
    }
	
}
