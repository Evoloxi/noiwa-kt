package dev.evoloxi.noiwa.foundation.spell.entity

import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World

class SparkBoltDoubleTriggerSpellEntity(type: EntityType<out SparkBoltDoubleTriggerSpellEntity?>?, worldIn: World) :
    SparkBoltSpellEntity(type, worldIn) {
    init {
        inGround = false
    }

    constructor(type: EntityType<out SparkBoltDoubleTriggerSpellEntity?>?, caster: LivingEntity, world: World) : this(
        type,
        world
    ) {
        this.setPosition(caster.x, caster.eyeY - 0.1, caster.z)
        casterUUID = caster.uuid
    }

    override fun generateParticles() {
        val motionVec = velocity
        val nextPos = pos.add(motionVec)
        if (submergedInWater) {
            for (j in 0..3) {
                world.addParticle(
                    ParticleTypes.BUBBLE,
                    nextPos.getX() - motionVec.getX() * 0.25,
                    nextPos.getY() - motionVec.getY() * 0.25,
                    nextPos.getZ() - motionVec.getZ() * 0.25,
                    motionVec.getX(),
                    motionVec.getY(),
                    motionVec.getZ()
                )
            }
        }
        for (i in 0..4) {
            for (j in 0..4) {
                world.addParticle(
                    GenericParticleEffect(
                        Vec3f(0.2f, 0.3f, 0.77f),
                        Vec3f(0.3f, 0.4f, 0.67f),
                        0.5f
                    ), this.x, this.y, this.z,
                    0.0, 0.0, 0.0
                )
            }
        }
    }
}
