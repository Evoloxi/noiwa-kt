package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.content.BouncyBeam
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.quiltmc.qsl.networking.api.PlayerLookup
import software.bernie.geckolib3.core.AnimationState
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.event.predicate.AnimationEvent
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.network.GeckoLibNetwork
import software.bernie.geckolib3.network.ISyncable
import software.bernie.geckolib3.util.GeckoLibUtil

class RifleItem(settings: Settings) : ItemWithRarity(settings), IAnimatable, ISyncable {
	private var controllerName = "controller"
	private var factory = GeckoLibUtil.createFactory(this)
	private val firing = 0
	
	override var rarity: ERarity = ERarity.EPIC
	
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
	    val beam = BouncyBeam(world, user.pos.add(0.0, 1.8, 0.0), user.rotationVector, user)
	    beam.bounces = 1
	    beam.maxDistance = 40.0
	    beam.distanceReduction = 0.0
	    beam.spread = 0.1
	    beam.delay = 20L
	    beam.create()
	    
	    user.world.playSound(
		    user,
		    user.blockPos,
		    SoundEvents.ITEM_INK_SAC_USE,
		    SoundCategory.PLAYERS,
		    1f,
		    1f
	    )
	    syncAnimation(world, user, stack)
	    
        return TypedActionResult.pass(stack)
    }
	
	private fun <P> predicate(removethis: AnimationEvent<P>?): PlayState where P : Item?, P : IAnimatable? {
		return PlayState.CONTINUE
	}
	
	override fun registerControllers(p0: AnimationData?) {
		p0?.addAnimationController(AnimationController(this, controllerName, 1.2f, ::predicate))
	}
	
	override fun getFactory(): AnimationFactory {
		return factory
	}
	
	override fun onAnimationSync(id: Int, state: Int) {
		if (state == firing) {
			val controller: AnimationController<*> = GeckoLibUtil.getControllerForID(
				factory, id, controllerName
			)
				controller.markNeedsReload()
				controller.setAnimation(AnimationBuilder().addAnimation("animation.rifle.fire", EDefaultLoopTypes.PLAY_ONCE))
			
		}
	}
	
	private fun syncAnimation(world: World, user: PlayerEntity, stack: ItemStack) {
		if (!world.isClient) {
			val id = GeckoLibUtil.guaranteeIDForStack(stack, world as ServerWorld?)
			GeckoLibNetwork.syncAnimation(user, this, id, firing)
			for (otherPlayer in PlayerLookup.tracking(user)) {
				GeckoLibNetwork.syncAnimation(otherPlayer, this, id, firing)
			}
		}
	}
}
