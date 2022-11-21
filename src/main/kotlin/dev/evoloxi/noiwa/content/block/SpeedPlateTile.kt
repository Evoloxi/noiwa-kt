package dev.evoloxi.noiwa.content.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.state.property.Properties.POWERED
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper.clamp
import software.bernie.geckolib3.core.AnimationState
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.event.predicate.AnimationEvent
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil

class SpeedPlateTile(pos: BlockPos, state: BlockState) : BlockEntity(
	TileRegistry.SPEED_PLATE_TILE, pos, state
), IAnimatable {
	private val factory = GeckoLibUtil.createFactory(this)
	private var wasActive = false
	var blocked: Boolean = false
	var prevTimestamp: Long = 0
	var timestamp: Long = 0
	set (value) {
		this.toUpdatePacket()
		prevTimestamp = field
		field = value
	}
	
	private fun <E : IAnimatable> predicate(event: AnimationEvent<E>): PlayState {
		val controller = event.controller
		
		controller.transitionLengthTicks = 0.4
		if (world?.getBlockEntity(pos) != null) {
			if (world?.getBlockState(pos)?.get(POWERED) == true && controller.currentAnimation != null) {
				if (controller.currentAnimation.animationName == "animation.speed_plate.activate" && controller.animationState == AnimationState.Stopped || wasActive) {
					wasActive = true
					controller.setAnimation(AnimationBuilder().addAnimation("animation.speed_plate.idle", EDefaultLoopTypes.LOOP))
				} else {
					controller.setAnimation(AnimationBuilder().addAnimation("animation.speed_plate.activate"))
				}
			} else if (world?.getBlockState(pos)?.get(POWERED) == false && controller.currentAnimation == null && !wasActive) {
				controller.isJustStarting = true
			} else {
				wasActive = false
				controller.setAnimation(AnimationBuilder().addAnimation("animation.speed_plate.deactivate"))
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
	
	val plateOffset: Float
		get() {
			val tick = factory.getOrCreateAnimationData(0).boneSnapshotCollection["plate"]?.left?.positionY ?: 0f
			println(tick)
			return clamp(tick, -0.32f, 0.0f)
		}
	
	override fun getFactory(): AnimationFactory {
		return factory
	}
	
	public override fun writeNbt(nbt: NbtCompound) {
		super.writeNbt(nbt)
		nbt.putBoolean("blocked", blocked)
		nbt.putLong("timestamp", timestamp)
		nbt.putLong("prevTimestamp", prevTimestamp)
	}
	
	override fun readNbt(nbt: NbtCompound) {
		super.readNbt(nbt)
		blocked = nbt.getBoolean("blocked")
		timestamp = nbt.getLong("timestamp")
		prevTimestamp = nbt.getLong("prevTimestamp")
	}
	
}
