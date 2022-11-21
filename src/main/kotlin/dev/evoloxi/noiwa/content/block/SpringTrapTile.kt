package dev.evoloxi.noiwa.content.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.state.property.Properties.EXTENDED
import net.minecraft.util.math.BlockPos
import software.bernie.geckolib3.core.AnimationState
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.event.predicate.AnimationEvent
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory

class SpringTrapTile(pos: BlockPos, state: BlockState) : BlockEntity(
    TileRegistry.SPRING_TRAP_TILE, pos, state), IAnimatable {
    private val factory = AnimationFactory(this)
	private var wasExtended = false

	private fun <E : IAnimatable> predicate(event: AnimationEvent<E>): PlayState {
		val controller = event.controller
		controller.transitionLengthTicks = 0.0
		if (world?.getBlockEntity(pos) != null) {
			// when the block extends, play animation.spring_trap.trigger, then play animation.spring_trap.idle when it's done#
			if (!wasExtended && world?.getBlockState(pos)?.get(EXTENDED) == true) {
				wasExtended = true
				controller.setAnimation(AnimationBuilder().addAnimation("animation.spring_trap.trigger2", false))
			} else if (wasExtended && controller.animationState == AnimationState.Stopped) {
				controller.setAnimation(AnimationBuilder().addAnimation("animation.spring_trap.idle2", true))
			}
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
