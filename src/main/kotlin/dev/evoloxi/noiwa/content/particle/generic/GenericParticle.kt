package dev.evoloxi.noiwa.content.particle.generic

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.Vec3f

class GenericParticle constructor(
	level: ClientWorld,
	x: Double,
	y: Double,
	z: Double,
	dx: Double,
	dy: Double,
	dz: Double,
	rdc: Boolean,
	particleEffect: GenericParticleEffect,
	spriteProvider: SpriteProvider
) : AbstractDustParticle<GenericParticleEffect>(level, x, y, z, dx, dy, dz, particleEffect, spriteProvider) {
	private var startColor: Vec3f? = null
	private var endColor: Vec3f? = null
	private var spriteProvider: SpriteProvider? = null
	
	init {
		velocityMultiplier = 0.96f
		yMotionBlockedSpeedUp = true
		this.spriteProvider = spriteProvider
		this.velocityX = dx
		this.velocityY = dy
		this.velocityZ = dz
		val f = random.nextFloat() * 0.4f + 0.6f
		colorRed = this.randomizeColor(particleEffect.color.x, f)
		colorGreen = this.randomizeColor(particleEffect.color.y, f)
		colorBlue = this.randomizeColor(particleEffect.color.z, f)
		scale *= 0.75f * particleEffect.scale
		val i = (8.0 / (random.nextDouble() * 0.8 + 0.2)).toInt()
		maxAge = (i.toFloat() * particleEffect.scale).coerceAtLeast(1.0f).toInt()
		setSpriteForAge(spriteProvider)
		collidesWithWorld = false
		startColor = particleEffect.startColor
		endColor = particleEffect.toColor
	}
	
	override fun getType(): ParticleTextureSheet {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE
	}
	
	override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
		this.lerpColors(tickDelta)
		super.buildGeometry(vertexConsumer, camera, tickDelta)
	}
	
	private fun lerpColors(tickDelta: Float) {
		val f = (age.toFloat() + tickDelta) / (maxAge.toFloat() + 1.0f)
		val vec3f: Vec3f = this.startColor?.copy() ?: Vec3f(0.0f, 0.0f, 0.0f)
		vec3f.lerp(this.endColor, f)
		colorRed = vec3f.x
		colorGreen = vec3f.y
		colorBlue = vec3f.z
	}
	

	override fun tick() {
		//super.tick()
		prevPosX = x
		prevPosY = y
		prevPosZ = z
		if (age++ >= maxAge) markDead()
		else {
			setSpriteForAge(spriteProvider)
			move(velocityX, velocityY, velocityZ)
			velocityX *= 0.96
			velocityY *= 0.96
			velocityZ *= 0.96
			if (onGround) {
				velocityX *= 0.7
				velocityZ *= 0.7
			}
		}
	}
}
