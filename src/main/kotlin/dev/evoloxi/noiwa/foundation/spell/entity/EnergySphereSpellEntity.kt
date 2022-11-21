package dev.evoloxi.noiwa.foundation.spell.entity

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.foundation.spell.ProjectileSpell
import dev.evoloxi.noiwa.foundation.util.MethHelper
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World

class EnergySphereSpellEntity(entityTypeIn: EntityType<out EnergySphereSpellEntity?>?, worldIn: World) :
    SpellEntityMagicalBase(entityTypeIn, worldIn) {
    constructor(type: EntityType<out EnergySphereSpellEntity?>?, caster: LivingEntity, world: World) : this(
        type,
        world
    ) {
        this.setPosition(caster.x, caster.eyeY - 0.1, caster.z)
        casterUUID = caster.uuid
    }

    override val maxAge: Int
        get() = 26
    override val gravity: Float
        get() = 0.03f

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        if (hitResult.type == HitResult.Type.ENTITY) {
            val entityHit = (hitResult as EntityHitResult).entity
            if (entityHit.uuid == casterUUID) return
            ProjectileSpell.ENERGY_SPHERE.damageCollection().causeDamage(this, entityHit)
            if (!world.isClient) discard()
        } else if (hitResult.type == HitResult.Type.BLOCK) { // Bounce
            var motion = velocity
            val reflectionAxis = (hitResult as BlockHitResult).side.axis
            val normalVec = Vec3d(hitResult.side.unitVector)
            /*
               180.0 - Math.acos((motion.dotProduct(normalVec))/(motion.length())) / Math.PI * 180.0 > 60.0
            =>   -acos(dot(norm)/length) / pi * 180 > -120
            =>   acos(dot(norm)/length) < 120*pi/180
            =>   dot(norm)/length > cos(120*pi/180) = -0.5
            =>   dot(norm) > -0.5 * length;
            =>   dot(norm)^2 < (-0.5 * length)^2

                More general equation:
                180.0 - Math.acos((motion.dotProduct(normalVec))/(motion.length())) / Math.PI * 180.0 > deg
            =>   -acos(dot(norm)/length) / pi * 180 > deg - 180
            =>   acos(dot(norm)/length) < (180 - deg)*pi/180
            =>   dot(norm)/length > cos((180 - deg)*pi/180) = cos(pi - deg*pi/180)
            =>   dot(norm) > cos(pi - deg*pi/180) * length
            =>   dot(norm)^2 < cos(pi - deg*pi/180)^2 * length^2
             */
            val d0 = motion.dotProduct(normalVec)
            val d1 = motion.length() / 2
            if (d0 * d0 >= d1 * d1) {
                discard()
                return
            }
            motion = MethHelper.reflectByAxis(motion, reflectionAxis)
            velocity = motion.multiply(0.6)
        }
    }

    override fun generateParticles() {
        super.generateParticles()
        for (i in 0..40) {
            world.addParticle(
                GenericParticleEffect(
                    Vec3f(0.9f, 0.6f, 0.67f),
                    Vec3f(0.3f, 0.4f, 0.67f),
                    0.5f
                ),
	            this.x + Core.RAND.nextGaussian(-0.6, 0.6),
	            this.y + Core.RAND.nextGaussian(-0.6, 0.6),
	            this.z + Core.RAND.nextGaussian(-0.6, 0.6),
                0.0, 0.2, 0.0
            )
        }
    }

    override fun initDataTracker() {}
}
