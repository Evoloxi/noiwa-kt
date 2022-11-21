package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.calculation.Stuff
import dev.evoloxi.noiwa.content.entities.rockProjectile.RockEntity
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.foundation.*
import dev.evoloxi.noiwa.foundation.Extensions.getBase
import dev.evoloxi.noiwa.foundation.handler.AttributeHandler.stats
import dev.evoloxi.noiwa.foundation.handler.NbtUpdater
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry
import dev.evoloxi.noiwa.foundation.registry.EntityRegistry
import dev.evoloxi.noiwa.foundation.registry.ItemRegistry
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World
import org.quiltmc.qsl.networking.api.PlayerLookup
import software.bernie.geckolib3.core.AnimationState
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.event.predicate.AnimationEvent
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.network.GeckoLibNetwork
import software.bernie.geckolib3.network.ISyncable
import software.bernie.geckolib3.util.GeckoLibUtil


class LauncherItem(settings: Settings) : ItemWithRarity(settings), IAnimatable, ISyncable {
    private var controllerName = "controller"
    private var factory = GeckoLibUtil.createFactory(this)
    private val open = 0
	override var rarity: ERarity = ERarity.LEGENDARY

//    init {
//    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack>? {
        val stack = user.getStackInHand(hand)
        val ammunition = stack.getOrCreateSubNbt("Base").getInt("ammo")
        val maxAmmunition = stack.getOrCreateSubNbt("Base").getInt("maxAmmo")
	    val cd = stack.stats.cooldown
        user.playSound( SoundRegistry.MECHA_RATTLE_1, SoundCategory.PLAYERS, 1f,
            Core.RAND.nextFloat(1.1f, 1.3f) )
        if (ammunition > 0) {
            fire(world, user)
            summonParticles(world, user)
            stack.getOrCreateSubNbt("Base").putInt("ammo", ammunition - 1)
            user.itemCooldownManager.set(this, (cd * 0.05).toInt())

        }

        if (ammunition <= 1 && user.inventory.count(ItemRegistry.AMMO) >= 1) {

            // up to 10 ammo
            val ammo = user.inventory.count(ItemRegistry.AMMO)
            val ammoToLoad = if (ammo > maxAmmunition - 1) maxAmmunition - 1 else ammo
            // remove ammo from inventory
            user.inventory.remove({ it.item == ItemRegistry.AMMO }, ammoToLoad, user.inventory)
            // add ammo to launcher
            stack.getOrCreateSubNbt("Base").putInt("ammo", ammunition + ammoToLoad)

            user.itemCooldownManager.set(this, Core.RAND.nextInt(30, 50)*(cd*2).toInt())

            user.playSound(SoundRegistry.RELOAD, SoundCategory.PLAYERS, 1f,
                Core.RAND.nextFloat(0.9f, 1.1f))

        } else if (ammunition == 0) {

            Stuff.error(user, "Not enough ammo.")
            user.itemCooldownManager.set(this, (cd * 20).toInt())
	
	        fire(world, user)

        }
        syncAnimation(world, user, stack)
        NbtUpdater.updateNBT(stack.orCreateNbt, user)

	    // spawn particles at the position where the projectile will land, if gravity is 0.08 blocks per tick
	    

        return TypedActionResult.pass(stack)
    }

    private fun syncAnimation(world: World, user: PlayerEntity, stack: ItemStack) {
        if (!world.isClient) {
            val id = GeckoLibUtil.guaranteeIDForStack(stack, world as ServerWorld?)
            GeckoLibNetwork.syncAnimation(user, this, id, open)
            for (otherPlayer in PlayerLookup.tracking(user)) {
                GeckoLibNetwork.syncAnimation(otherPlayer, this, id, open)
            }
        }
    }

    private fun fire(world: World, user: PlayerEntity) {
        if (!world.isClient) {
            val direction = user.rotationVector
            val velo = user.velocity
            val length = 5.3
            val spread = 0.1
	        val amp = 0.8f

            val raycast = user.raycast(length, 0f, false)

            val rockEntity = RockEntity(EntityRegistry.ROCK, world)
            rockEntity.owner = user

            val target = raycast.pos.subtract(direction.multiply(1.0))

            rockEntity.setPos(target.x, target.y, target.z)

            rockEntity.setVelocity(
                direction.x * amp + velo.x + (Core.RAND.nextDouble(-spread, spread)),
                direction.y * amp + velo.y + (Core.RAND.nextDouble(-spread, spread)),
                direction.z * amp + velo.z + (Core.RAND.nextDouble(-spread, spread))
            )

            rockEntity.maxBounces = Core.RAND.nextInt(2, 6)

            world.spawnEntity(rockEntity)

            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundRegistry.LAUNCHER_SHOOT,
                SoundCategory.BLOCKS,
                0.25f,
                Core.RAND.nextFloat(0.8f, 2f)
            )
        }


    }

    private fun summonParticles(world: World, user: PlayerEntity) {
        if (world.isClient) {
            val v = user.rotationVector.multiply(0.75)
            val pPos = user.pos.add(0.0, 1.5, 0.0).add(v.multiply(1.4))
            for (i in 0..9) {
                world.addParticle(
                    GenericParticleEffect(
	                    Vec3f(0.45f, 0.45f, 0.45f),
	                    Vec3f(0.4f, 0.3f, 0.3f),
	                    1.45f
					),
                    pPos.x,
                    pPos.y,
                    pPos.z,
	                v.x + Core.RAND.nextDouble(-0.3, 0.3),
	                v.y + Core.RAND.nextDouble(-0.3, 0.3),
	                v.z + Core.RAND.nextDouble(-0.3, 0.3)
                )
            }
        }
    }

    private fun <P> predicate(removethis: AnimationEvent<P>?): PlayState where P : Item?, P : IAnimatable? {
        return PlayState.CONTINUE
    }

    override fun registerControllers(p0: AnimationData?) {
        p0?.addAnimationController(AnimationController(this, controllerName, 1f, ::predicate))
    }

    override fun getFactory(): AnimationFactory {
        return factory
    }

    override fun onAnimationSync(id: Int, state: Int) {
        if (state == open) {
            val controller: AnimationController<*> = GeckoLibUtil.getControllerForID(
                factory, id, controllerName
            )
            if (controller.animationState == AnimationState.Stopped) {
                controller.markNeedsReload()
                controller.setAnimation(AnimationBuilder().addAnimation("fire", false))
            }
        }
    }


}
