package dev.evoloxi.noiwa.foundation.spell.entity

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.Extensions.squareXZ
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion

class BombSpellEntity(type: EntityType<out BombSpellEntity?>?, worldIn: World) : SpellEntityBase(type, worldIn) {
    constructor(type: EntityType<out BombSpellEntity?>?, caster: LivingEntity, world: World) : this(type, world) {
        this.setPosition(caster.x, caster.eyeY - 0.1, caster.z)
        casterUUID = caster.uuid
    }

    override fun shoot(shooter: Entity, pitch: Float, yaw: Float, velocity: Float, inaccuracy: Float) {
        val x = -MathHelper.sin(yaw * Core.TO_RAD_F) * MathHelper.cos(pitch * Core.TO_RAD_F)
        val y = -MathHelper.sin(pitch * Core.TO_RAD_F)
        val z = MathHelper.cos(yaw * Core.TO_RAD_F) * MathHelper.cos(pitch * Core.TO_RAD_F)
        this.setVelocity(x.toDouble(), y.toDouble(), z.toDouble(), velocity, inaccuracy)
        this.velocity =
            this.velocity.add(
                shooter.velocity.x,
                if (shooter.isOnGround) 0.0 else shooter.velocity.y,
                shooter.velocity.z
            )
    }

    override fun setVelocity(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float) {
    
    }

    override fun initDataTracker() {}
    override fun tick() {
        super.tick()
        prevX = this.x
        prevY = this.y
        prevZ = this.z
        velocity = velocity.add(0.0, -0.04, 0.0)
        if (world.isClient) {
            noClip = false
        } else {
            noClip = !world.doesNotIntersectEntities(this)
            if (noClip) {
                pushOutOfBlocks(this.x, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0, this.z)
            }
        }
        if (!onGround || velocity.squareXZ() > 1.0E-5 || (age + id) % 4 == 0) {
            move(MovementType.SELF, velocity)
            var horizontalFactor = 0.98f
            if (onGround) {
                val pos = BlockPos(this.x, this.y - 1.0, this.z)
                horizontalFactor = 0.94f
            }
            velocity = velocity.multiply(horizontalFactor.toDouble(), 0.98, horizontalFactor.toDouble())
            if (onGround) {
                velocity = velocity.multiply(1.0, -0.5, 1.0)
            }
        }
        val blockpos = blockPos
        val blockstate = world.getBlockState(blockpos)
        if (!blockstate.isAir) {
            val voxelshape = blockstate.getCollisionShape(world, blockpos)
            if (!voxelshape.isEmpty) {
                val vec3d1 = pos
                for (axisalignedbb in voxelshape.boundingBoxes) {
                    if (axisalignedbb.offset(blockpos).contains(vec3d1)) {
                        inGround = true
                        break
                    }
                }
            }
        }
        val motionVec = velocity
        if (submergedInWater) {
            velocity = motionVec.multiply(this.waterDrag.toDouble())
        }
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (!world.isClient()) {
            if (source.isExplosive) {
                age = maxAge - 2
                return false
            }
        }
        return super.damage(source, amount)
    }

    override val gravity: Float
        get() = 0F
    override val maxAge: Int
        get() = 45

    override fun onAgeExpire() {
        if (!world.isClient()) {
            world.createExplosion(
                this,
                DamageSource.explosion(this.caster),
                null,
                this.x, this.y, this.z,
                3f,
                true, Explosion.DestructionType.BREAK
            )
            super.onAgeExpire()
        }
    }

    override fun generateParticles() {
        super.generateParticles()
        world.addParticle(
            ParticleTypes.SMOKE,
            Core.RAND.nextDouble(0.5),
            Core.RAND.nextDouble(),
            Core.RAND.nextDouble(0.5),
            0.0,
            0.0,
            0.0
        )
    }
}
