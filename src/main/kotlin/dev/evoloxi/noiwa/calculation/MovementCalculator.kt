package dev.evoloxi.noiwa.calculation;

import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


object MovementCalculator {
    @JvmStatic
    fun move(player: LivingEntity, movementInput: Vec3d, ci: CallbackInfo) {
//        return
//        // reverse the movement input
//        val d = movementInput.lengthSquared()
//        val vec3d: Vec3d = (if (d > 1.0) movementInput.normalize() else movementInput)
//        val yaw = player.yaw;
//        val f = MathHelper.sin(yaw * 0.017453292f)
//        val g = MathHelper.cos(yaw * 0.017453292f)
//
//        val moveDir = Vec3d(
//            vec3d.x * g - vec3d.z * f,
//            vec3d.y,
//            vec3d.z * g + vec3d.x * f
//        )
//        val accelVec: Vec3d = player.getVelocity()
//
//        val projVel = Vec3d(accelVec.x, 0.0, accelVec.z).dotProduct(moveDir)
//        var accelVel = 0.24
//        val maxVel = 0.4
//
//        if (projVel + accelVel > maxVel) {
//            accelVel = maxVel - projVel
//        }
//        val accelDir = moveDir.multiply(Math.max(accelVel, 0.0))
//
//        player.setVelocity(accelVec.add(accelDir))
    }
}
