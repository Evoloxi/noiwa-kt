package dev.evoloxi.noiwa.foundation.spell.entity

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.Extensions.squareXZ
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

abstract class SpellEntityMagicalBase(entityTypeIn: EntityType<out SpellEntityMagicalBase?>?, worldIn: World) :
    SpellEntityBase(entityTypeIn, worldIn) {
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

    override fun tick() {
        super.tick()
        val hitResult = ProjectileUtil.getCollision(this) { !it.isSpectator && it.collides() && it !== this.caster }
        if (hitResult.type != HitResult.Type.MISS) {
            onHit(hitResult)
            inGround = false
        }
        val motionVec = velocity
        val positionVec = pos
        val f1 = MathHelper.sqrt(motionVec.squareXZ())
        yaw = (MathHelper.atan2(motionVec.x, motionVec.z) * Core.TO_DEG).toFloat()
        pitch = (MathHelper.atan2(motionVec.y, f1.toDouble()) * Core.TO_DEG).toFloat()
	    
        while (pitch - prevPitch < -180.0f) prevPitch -= 360.0f
        while (pitch - prevPitch >= 180.0f) prevPitch += 360.0f
        while (yaw - prevYaw < -180.0f) prevYaw -= 360.0f
        while (yaw - prevYaw >= 180.0f) prevYaw += 360.0f
        
        pitch = MathHelper.lerp(0.2f, prevPitch, pitch)
        this.getYaw(MathHelper.lerp(0.2f, prevYaw, yaw))
        val motionScale = if (this.isSubmergedInWater) waterDrag else airDrag
        val nextMotionVec = motionVec.multiply(motionScale.toDouble())
        val nextPositionVec = motionVec.add(positionVec)
        adjustVelocity(nextMotionVec.getX(), nextMotionVec.getY() - this.gravity, nextMotionVec.getZ())
        this.setPosition(nextPositionVec.getX(), nextPositionVec.getY(), nextPositionVec.getZ())
        checkBlockCollision() // Call BlockState.onEntityCollision(), such as bubble columns, web, cactus
    }

    fun adjustVelocity(x: Double, y: Double, z: Double) {
        this.setVelocity(x, y, z)
        if (prevPitch == 0.0f && prevYaw == 0.0f) {
            val f = MathHelper.sqrt((x * x + z * z).toFloat())
            pitch = (MathHelper.atan2(
                y,
                f.toDouble()
            ) * Core.TO_DEG).toFloat()
            yaw =
	            (MathHelper.atan2(x, z) * Core.TO_DEG).toFloat()
            prevPitch = pitch
            prevYaw = yaw
            updatePositionAndAngles(x, y, z, yaw, pitch)
        }
    }
}
