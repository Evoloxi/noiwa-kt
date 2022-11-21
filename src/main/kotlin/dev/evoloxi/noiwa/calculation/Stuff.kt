package dev.evoloxi.noiwa.calculation

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.entities.coin.CoinEntity
import dev.evoloxi.noiwa.content.item.ERarity
import dev.evoloxi.noiwa.content.item.ItemWithRarity
import dev.evoloxi.noiwa.content.item.LauncherItem
import dev.evoloxi.noiwa.content.item.RifleItem
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.content.particle.indicator.IndicatorParticle
import dev.evoloxi.noiwa.content.particle.indicator.IndicatorRenderManager
import dev.evoloxi.noiwa.foundation.CombatHandler.attackV
import dev.evoloxi.noiwa.foundation.DamageType
import dev.evoloxi.noiwa.foundation.Extensions.fibonacciSphere
import dev.evoloxi.noiwa.foundation.Extensions.parseRGB
import dev.evoloxi.noiwa.foundation.Extensions.shortenNumber
import dev.evoloxi.noiwa.foundation.Extensions.toVec3f
import dev.evoloxi.noiwa.foundation.Extensions.unicode
import dev.evoloxi.noiwa.foundation.component.CustomComponents.Companion.COINS
import dev.evoloxi.noiwa.foundation.component.CustomComponents.Companion.STATIC
import dev.evoloxi.noiwa.foundation.handler.AttributeHandler.eRarity
import dev.evoloxi.noiwa.foundation.handler.Processor
import dev.evoloxi.noiwa.foundation.registry.ParticleRegistry
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import dev.evoloxi.noiwa.foundation.toDamageType
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.TntEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.awt.Color

object Stuff {
    private const val DEBUG = false


    fun getReflection(vec: Vec3d, normal: Vec3d): Vec3d? {
        // get the reflection of the vector
        val projection: Double = vec.dotProduct(normal)
        return vec.add(normal.multiply(-2 * projection))
    }

    fun drawParticleLine(world: World, pos: Vec3d, target: Vec3d, particle: DefaultParticleType) {
        val direction = target.subtract(pos).normalize()
        val length = target.subtract(pos).length()
        for (i in 0..length.toInt() * 2) {
            val particlePos = pos.add(direction.multiply(i.toDouble() * 0.5))
            world.addParticle(particle, particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0)
        }
    }
	
	// safely add a particle to PARTICLES preventing ConcurrentModificationException
    fun spawnIndicator(
        pos: Vec3d,
        itype: Byte,
        eWidth: Float,
        eHeight: Float,
        amount: Long,
        damageType: Byte
    ) {
		val type = damageType.toDamageType()
        val text1 = Processor.crit( if (itype == 1.toByte()) unicode(amount.shortenNumber()) else amount.toString(), type)
        val particle = IndicatorParticle(pos, eWidth, eHeight, text1, itype)
        IndicatorRenderManager.PARTICLES.add(particle)
    }

    @JvmStatic
    fun getAttackStrength(player: PlayerEntity) {
        if (player.world.isClient()) return
        Storage.setAttackScale(player.uuid, player.getAttackCooldownProgress(1f))
    }

    @JvmStatic
    fun mineBlock2(player: PlayerEntity, pos: BlockPos) {
        if (player.world.isClient()) return
        if (player.world.getBlockState(pos).block == Blocks.TNT) {
            player.world.setBlockState(pos, Blocks.AIR.defaultState)

        }
    }

    @JvmStatic
    fun breakRestriction(player: PlayerEntity, pos: BlockPos, cir: CallbackInfoReturnable<Boolean>) {
        if (player.getWorld().getBlockState(pos).block == Blocks.TNT) {
            cir.returnValue = true
            val serverPlayer = MinecraftClient.getInstance().server?.playerManager?.getPlayer(player.uuid)!!
            val serverWorld = serverPlayer.getWorld()!!
            if (player.getStackInHand(Hand.MAIN_HAND).isEmpty) {
                serverWorld.setBlockState(pos, Blocks.AIR.defaultState)
                serverPlayer.giveItemStack(ItemStack(Blocks.TNT))
            } else {
                serverPlayer.sendMessage(Text.literal("§cYou must have an empty hand to obtain TNT!"), false)
            }
        }
    }

    @JvmStatic
    fun addParticles(
        world: World,
        particle: ParticleEffect,
        count: Int,
        speed: Double,
        x: Double,
        y: Double,
        z: Double,
        vx: Double,
        vy: Double,
        vz: Double
    ) {
        if (world is ServerWorld) {
            world.spawnParticles(particle, x, y, z, count, vx, vy, vz, speed)
        }
    }

    @JvmStatic
    fun kaboom(tnt: TntEntity, ci: CallbackInfo) {
        ci.cancel()
        val world = tnt.world
        val pos = tnt.pos
        val r = 5
        // get all stone blocks within 5 blocks of the TNT
        val blocks = ArrayList<BlockPos>()
        for (x in pos.x.toInt() - r..pos.x.toInt() + r) {
            for (y in pos.y.toInt() - r..pos.y.toInt() + r) {
                for (z in pos.z.toInt() - r..pos.z.toInt() + r) {
                    val blockPos = BlockPos(x, y, z)
                    if (world.getBlockState(blockPos).block.toString().contains("cracked")) {
                        // if distance is less than 5, add to list
                        if (pos.squaredDistanceTo(
                                Vec3d(
                                    blockPos.x.toDouble(),
                                    blockPos.y.toDouble(),
                                    blockPos.z.toDouble()
                                )
                            ) < r * r
                        ) {
                            blocks.add(blockPos)
                        }
                    }
                }
            }
        }
        addParticles(world, ParticleTypes.EXPLOSION_EMITTER, 1, 0.0, pos.x, pos.y, pos.z, 0.5, 0.5, 0.5)
        // knockback all entities within 5 blocks of the TNT
        val box = Box(pos.x - r, pos.y - r, pos.z - r, pos.x + r, pos.y + r, pos.z + r)
        val entities = world.getOtherEntities(null, box)
        entities.forEach { entity: Entity ->
            if (entity is LivingEntity) {
                if (entity.distanceTo(tnt) < r) {
                    val mult = (1 - entity.distanceTo(tnt) / r * 0.8)
                    // vad velocity relative to the TNT
                    var velocity = entity.pos.subtract(pos.add(0.0, -1.0, 0.0)).multiply(mult, mult, mult)
                    velocity = velocity.add(entity.velocity.multiply(0.4, 0.4, 0.4))
                    entity.velocity = velocity
                    entity.velocityModified = true

                }
            }
        }
        world.playSound(null, BlockPos(pos), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1f, 1f)
        world.playSound(null, BlockPos(pos), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1f, 1f)

        for (blockPos in blocks) {
            val blockst = world.getBlockState(blockPos)
            addParticles(
                world,
                ParticleTypes.SMOKE,
                16,
                0.0,
                blockPos.x.toDouble(),
                blockPos.y.toDouble(),
                blockPos.z.toDouble(),
                0.5,
                0.5,
                0.5
            )
            world.syncWorldEvent(2008, blockPos, Block.getRawIdFromState(blockst))
            world.setBlockState(blockPos, Blocks.AIR.defaultState)

        }
    }

    @JvmStatic
    fun collectCoins(player: PlayerEntity) {

        val world = player.world
        val COINC = player.getComponent(COINS)
        if (!world.isClient()) {
            if (COINC.coinTimer > 0) {
                player.syncComponent(COINS)
            }
        }

        if (world.time % 3 == 0L) {

            var pos = player.pos
            val r = 0.65

            val box = Box(pos.x - r, pos.y - r, pos.z - r, pos.x + r + 1, pos.y + r, pos.z + r)
            val entities = world.getEntitiesByClass(CoinEntity::class.java, box) { true }
            entities.forEach { entity: CoinEntity ->

                if (entity.pos.squaredDistanceTo(
                        pos.add(
                            0.0,
                            1.0,
                            0.0
                        )
                    ) < r * r * 1.8 || entity.distanceTo(player) < r * 1.5
                ) {

                    pos = entity.pos
                    // AllSounds get random sound kotlin
                    world.playSound(
                        null, BlockPos(pos),
                        SoundRegistry.getCoinSound(), SoundCategory.BLOCKS,
                        1f,
                        Core.RAND.nextFloat(1f, 1.35f)
                    )

                    addParticles(
                        world, DustParticleEffect(Vec3f(252 / 255F, 221 / 255F, 136 / 255F), 1F),
                        6, 0.0, pos.x, pos.y + 0.4, pos.z, 0.25, 0.25, 0.25
                    )
                    addParticles(
                        world, ParticleRegistry.SPARKLE,
                        3, 0.0, pos.x, pos.y + 0.4, pos.z, 0.25, 0.65, 0.25
                    )
                    gainCoins(player, entity.value)
                    entity.remove(Entity.RemovalReason.KILLED)

                }
            }
        }
    }

    fun error(player: PlayerEntity, message: String) {
        if (player.world is ClientWorld) {
            player.sendMessage(Text.of("🧻 §c$message"), true)
            player.playSound(SoundRegistry.ERROR, SoundCategory.MASTER, 1f, 1f)
        }
    }

    private fun gainCoins(player: PlayerEntity, amount: Int) {
        //player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1f, 2f)
        player.getComponent(COINS).addCoins(amount)
        val streak = player.getComponent(COINS).coinStreak
        player.sendMessage(parseRGB("<#ffdc4f>+$amount <#f59f36>+[<#ffdc4f>$streak<#f59f36>]"), true)
        player.getComponent(STATIC).coins += amount
        val floor = Core.RAND.nextInt(1, 15)
        player.getComponent(STATIC).dungeonFloor = floor
        player.getComponent(STATIC).glyphCount = Core.RAND.nextInt(1, floor + 1) - 1
        player.getComponent(STATIC).glyphCountMax = floor + Core.RAND.nextInt(0, (floor / 3) + 1)
        error(player, "Nooo! Why? Error!")

    }

    @JvmStatic
    fun getArmPose(
        player: AbstractClientPlayerEntity,
        hand: Hand,
        cir: CallbackInfoReturnable<BipedEntityModel.ArmPose>,
        item: Item
    ) {
        if (item is LauncherItem || item is RifleItem) {
            cir.returnValue = BipedEntityModel.ArmPose.BOW_AND_ARROW
        }
    }

    @JvmStatic
    fun attack(
        source: DamageSource,
        victim: LivingEntity,
        cir: CallbackInfoReturnable<Boolean>
    ) {
        if (source.attacker is PlayerEntity) {
            val player = source.attacker as LivingEntity
            player.attackV(victim, DamageType.MELEE)
        }
        if (!source.isOutOfWorld)
            cir.cancel()
    }

    fun elixirHit(origin: Vec3d, radius: Double, startColor: Vec3f, endColor: Vec3f) {
	    val samples = (radius *  3.141592653589793 * 30).toInt()
		val points = fibonacciSphere(samples, radius)
	    val world = MinecraftClient.getInstance().world ?: return
	    
	    for (point in points) {
	        val pos = origin.add(point)
		    // velocity for the particles to move away from the origin
		    val velocity = point.multiply(0.0)
		    world.addParticle(
			    GenericParticleEffect(
				    startColor,
					endColor,
				    1.2f
			    ), pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z)
	    }
    }
	

	
    @JvmStatic
    fun itemEffect(itemEntity: ItemEntity, stack: ItemStack) {
	    if (itemEntity.world.isClient) {
		    // spawn particle
			val rarity = stack.eRarity
		    val color = rarity.colorVec()
		    val offset = Vec3d(Core.RAND.nextDouble(-0.14, 0.14), Core.RAND.nextDouble(-0.14, 0.18), Core.RAND.nextDouble(-0.14, 0.14))
		    itemEntity.world.addParticle(
			    GenericParticleEffect(
				    color, color, Core.RAND.nextFloat(0.77f, 0.92f)
			    ),
			    false,
			    itemEntity.x + offset.x,
			    itemEntity.y + offset.y + 0.2,
			    itemEntity.z + offset.z,
			    offset.x * 0.12,
			    (offset.y + 0.2) * 0.12,
			    offset.z * 0.12
		    )
	    }
    }


}


