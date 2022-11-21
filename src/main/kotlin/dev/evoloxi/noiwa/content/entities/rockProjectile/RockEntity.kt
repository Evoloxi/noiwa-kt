package dev.evoloxi.noiwa.content.entities.rockProjectile

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.calculation.Stuff
import dev.evoloxi.noiwa.foundation.CombatHandler.attackV
import dev.evoloxi.noiwa.foundation.DamageType
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper.clamp
import net.minecraft.util.math.MathHelper.square
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.easing.EasingType
import software.bernie.geckolib3.core.event.predicate.AnimationEvent
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class RockEntity(entityType: EntityType<RockEntity>, world: World?) : ProjectileEntity(entityType, world), IAnimatable {
    private val factory = GeckoLibUtil.createFactory(this)

    companion object {
        private val DATA_BOUNCES =
            DataTracker.registerData(RockEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        private val DATA_LAST_BOUNCE = DataTracker.registerData(RockEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        private val DATA_IN_GROUND =
            DataTracker.registerData(RockEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        // max bounces 10
        private val DATA_MAX_BOUNCES =
            DataTracker.registerData(RockEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
    }

    private var hitCount: Int
        get() = dataTracker.get(DATA_BOUNCES)
        set(value) {
            dataTracker.set(DATA_BOUNCES, value)
        }
    private var lastHit: Int
        get() = dataTracker.get(DATA_LAST_BOUNCE)
        set(value) {
            dataTracker.set(DATA_LAST_BOUNCE, value)
        }
    private var inGround: Boolean
        get() = dataTracker.get(DATA_IN_GROUND)
        set(value) {
            dataTracker.set(DATA_IN_GROUND, value)
        }
    var maxBounces: Int
        get() = dataTracker.get(DATA_MAX_BOUNCES)
        set(value) {
            dataTracker.set(DATA_MAX_BOUNCES, value)
        }

    init {
        hitCount = 0
        lastHit = 0
        inGround = false
    }
	
	override fun collidesWith(other: Entity): Boolean {
		return canCollide(this, other)
	}
	
	private fun canCollide(entity: Entity, other: Entity): Boolean {
		return (other.isCollidable || other.isPushable) && !entity.isConnectedThroughVehicle(other)
	}
	
	override fun isCollidable(): Boolean {
		return true
	}
	
	override fun isPushable(): Boolean {
		return true
	}
    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        val side = blockHitResult!!.side
        val hitDiff = this.world.time - this.lastHit
	    
	    if (hitDiff < 3 && this.velocity.length() < 0.5) {
		    this.inGround = true
	    }
        if (hitDiff < 1 || this.inGround) return

        if (this.velocity.normalize().dotProduct(Vec3d(side.unitVector)) + 0.999 < 0.003) {
            this.maxBounces = 1
        }
	    
        if (((this.hitCount >= this.maxBounces && side == Direction.UP)) && hitDiff > 6 || this.hitCount > 14) {

            inGround = true
            setPosition(blockHitResult.pos)
            playSound(SoundRegistry.getStoneSound(), 1.0f, 1.6f)
            return

        } else if (hitDiff > 4){
            hitCount++
        }

        val motionVector = velocity

        val surfaceVector = Vec3d(side.unitVector)

        val reflectionVector = Stuff.getReflection(
            motionVector, surfaceVector
        )!!.multiply(0.8, 0.9 - 0.1 * hitCount, 0.8)

        this.lastHit = world.time.toInt()

        knockbackEntities(pos, hitDiff)

        if (world.isClient) {

            val pPos = blockHitResult.pos
            val v = reflectionVector.multiply(0.25)
            for (i in 0..5) {
                world.addParticle(
                    ParticleTypes.SMOKE,
                    pPos.x,
                    pPos.y,
                    pPos.z,
                    v.x + Core.RAND.nextGaussian(-0.1, 0.1),
                    v.y + Core.RAND.nextGaussian(-0.1, 0.1),
                    v.z + Core.RAND.nextGaussian(-0.1, 0.1)
                )
            }
            if (hitDiff > 4) {
                world.players.filter { it.pos.distanceTo(this.pos) < 100 }.forEach {
                    it.playSound(
                        SoundRegistry.getStoneSound(),
                        SoundCategory.BLOCKS,
                        (1.2 * (1 - it.pos.distanceTo(this.pos) / 80f)).toFloat(),
                        Core.RAND.nextFloat(1.7f, 2f)
                    )
                }
                if (!world.isClient) {
                    Stuff.addParticles(
                        world,
                        ParticleTypes.EXPLOSION,
                        3,
                        1.0 + hitCount * 0.4,
                        pPos.x,
                        pPos.y,
                        pPos.z,
                        0.0,
                        0.0,
                        0.0
                    )
                }
            }
        }

        this.setPosition(this.pos.add(reflectionVector.multiply(0.5)))

        this.velocity = reflectionVector

    }



    private fun knockbackEntities(pos: Vec3d, hitDiff: Long) {
        val knockbackRadius = 4.1
        val damageRadius = 6.4
        var doClamp = true

        val entities = world.getOtherEntities(
            this, this.boundingBox.expand(11.0)
        ).filter { it !is RockEntity }

        for (entity in entities) {
            val dist = entity.pos.distanceTo(pos)
            var knockback = entity.velocity.clamp(-1.0, 1.0)

            if ((dist < 2 &&
                 entity.pitch <= 90 &&
                 entity.pitch >= 85 &&
                 entity.isOnGround) &&
                 entity is PlayerEntity
            ) {

                val yaw = entity.yaw
                val yawRad = Math.toRadians(yaw.toDouble())

                val x = -sin(yawRad) * 6.6
                val z = cos(yawRad) * 6.6

                knockback = knockback.add(x, 0.3, z)
                doClamp = false

            }
            if (dist < knockbackRadius && (!world.isClient || entity is PlayerEntity)) {

                val repos = entity.pos.add(0.0, 0.9, 0.0)

                val m = clamp(abs(1 - (repos.distanceTo(pos) / (knockbackRadius * 0.1)) * 0.05), 0.1, 0.6)

                knockback = Vec3d(
                    knockback.x, clamp(square(knockback.y),0.0, 3.9) * 0.5, knockback.z
                )

                knockback = knockback
                    .add((repos.subtract(pos).multiply(m / (hitCount * 2 - 1)).multiply(0.8, 0.6, 0.8)))
                    .add(0.0, 0.3, 0.0)
            }

            if (dist < damageRadius && entity !is PlayerEntity && entity is LivingEntity && hitDiff > 4) {
                val damage = clamp(
                    abs(20 - (entity.pos.distanceTo(pos) / (damageRadius * 0.1)) * 1.0),
                    0.1,
                    1.7
                ) * 2 * (hitCount + 1)

                (this.owner as? LivingEntity)?.attackV(entity, DamageType.RANGED)
            }
            // if there are any entities in range, knock them back
            if (dist < knockbackRadius) {
                entity.addVelocity(knockback.x, knockback.y, knockback.z)
                if (doClamp)
                entity.velocity = entity.velocity.clamp(-1.5, 1.5)
                entity.velocityModified = true
            }
        }

    }

    override fun tick() {
        this.age++
        if (this.age > 20 * 20) {
            this.kill()
        }
        if (this.inGround) return
        if (!this.world.isClient && lastHit > 0 && world.time - lastHit < 13) {
            Stuff.addParticles(
                this.world,
                BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultState),
                3,
                0.1,
                this.x + Core.RAND.nextGaussian(0.0, 0.8),
                this.y + Core.RAND.nextGaussian(0.0, 0.8),
                this.z + Core.RAND.nextGaussian(0.0, 0.8),
                Core.RAND.nextGaussian(-0.1, 0.1),
                Core.RAND.nextGaussian(-0.1, 0.1),
                Core.RAND.nextGaussian(-0.1, 0.1)
            )
        }


        val v = this.velocity

        val currentPosition = this.pos
        val nextPosition = this.pos.add(v)
	    
	    
        //TODO: Revisit this, maybe change to @setVelocityClient
        this.velocity = if (world.getBlockState(BlockPos(currentPosition)).block is FluidBlock) {
			this.velocity.multiply(0.91, 0.92, 0.91).add(0.0, -0.02, 0.0)
		} else {
			this.velocity.add(0.0, -0.04, 0.0)
		}
	    setVelocityClient(velocity.x, velocity.y, velocity.z)
        // check for collisions
        val hitResult = world.raycast(
            RaycastContext(
                currentPosition,
                nextPosition.add(velocity.multiply(1.9)),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
            )
        )

        this.move(MovementType.SELF, this.velocity)
        // check collision
        if (hitResult.type != HitResult.Type.MISS) {
            this.onBlockHit(hitResult)
        }

    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putInt("HitCount", hitCount)
        nbt.putInt("LastHit", lastHit)
        nbt.putBoolean("InGround", inGround)
        nbt.putInt("MaxBounces", maxBounces)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        dataTracker.set(DATA_BOUNCES, nbt.getInt("HitCount"))
        dataTracker.set(DATA_LAST_BOUNCE, nbt.getInt("LastHit"))
        dataTracker.set(DATA_IN_GROUND, nbt.getBoolean("InGround"))
        dataTracker.set(DATA_MAX_BOUNCES, nbt.getInt("MaxBounces"))
    }

    override fun initDataTracker() {
        dataTracker.startTracking(DATA_BOUNCES, 0)
        dataTracker.startTracking(DATA_LAST_BOUNCE, 0)
        dataTracker.startTracking(DATA_IN_GROUND, false)
        dataTracker.startTracking(DATA_MAX_BOUNCES, 3)
    }


    override fun registerControllers(animationData: AnimationData) {
        animationData.addAnimationController(
            AnimationController(this, "controller", 0f, ::predicate)
        )
    }

    private fun <E> predicate(event: AnimationEvent<E>): PlayState where E : IAnimatable, E : Entity {
	    event.controller.animationSpeed = 0.3
            if (event.controller.currentAnimation == null) {
				event.controller.setAnimation(AnimationBuilder().addAnimation("idle", EDefaultLoopTypes.LOOP))
			}
			return PlayState.CONTINUE
    }

    override fun getFactory(): AnimationFactory {
        return factory
    }
}

fun Vec3d.clamp(d: Double, d1: Double): Vec3d {
    return Vec3d(
        clamp(this.x, d, d1),
        clamp(this.y, d, d1),
        clamp(this.z, d, d1)
    )
}

