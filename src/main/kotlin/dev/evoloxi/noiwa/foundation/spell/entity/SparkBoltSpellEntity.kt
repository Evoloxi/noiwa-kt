package dev.evoloxi.noiwa.foundation.spell.entity

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.foundation.spell.ProjectileSpell
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World

open class SparkBoltSpellEntity(type: EntityType<out SparkBoltSpellEntity?>?, worldIn: World) :
	SpellEntityMagicalBase(type, worldIn) {
	init {
		inGround = false
	}
	
	constructor(type: EntityType<out SparkBoltSpellEntity?>?, caster: LivingEntity, world: World) : this(type, world) {
		this.setPosition(caster.x, caster.eyeY - 0.1, caster.z)
		casterUUID = caster.uuid
	}
	

	override fun onHit(traceResult: HitResult) {
		super.onHit(traceResult)
		if (traceResult.type == HitResult.Type.ENTITY) {
			val entityHit = (traceResult as EntityHitResult).entity
			if (entityHit.uuid == casterUUID) return
			ProjectileSpell.SPARK_BOLT.damageCollection().causeDamage(this, entityHit)
		}
		if (!world.isClient()) discard()
	}
	

	override fun generateParticles() {
		super.generateParticles()
		for (i in 0..4) {
			world.addParticle(
				GenericParticleEffect(
					Vec3f(0.8f, 0.8f, 0.97f),
					Vec3f(0.3f, 0.4f, 0.67f),
					1.0f
				),
				this.x + Core.RAND.nextFloat(-0.1f, 0.1f) + (velocity.x * i / 4),
				this.y + Core.RAND.nextFloat(-0.1f, 0.1f) + (velocity.y * i / 4),
				this.z + Core.RAND.nextFloat(-0.1f, 0.1f) + (velocity.z * i / 4),
				0.0, 0.0, 0.0
			)
		}
	}
	
	override val ageToCast = 7
	override val gravity = 0.01f
	override val maxAge = 10
	
	override fun initDataTracker() {}
}
