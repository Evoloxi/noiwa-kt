package dev.evoloxi.noiwa.content.particle

import dev.evoloxi.noiwa.Core
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.math.MathHelper.lerp
import net.minecraft.util.math.MathHelper.sin

// particle that increases in size and then fades out
class EchoParticle constructor(
	level: ClientWorld?, x: Double, y: Double, z: Double,
	spriteSet: SpriteProvider?, dx: Double, dy: Double, dz: Double
) : SpriteBillboardParticle(level, x, y, z, dx, dy, dz) {
	
	private var lastScale: Float
	private var rotationSpeed: Float
	
	init {
		this.maxAge = 9
		this.setSpriteForAge(spriteSet)
		this.scale = 0.0f
		this.rotationSpeed = (Core.RAND.nextFloat(-0.2f, 0.2f))
		this.lastScale = this.scale
		//this.angle = Math.random().toFloat() * (Math.PI * 2).toFloat()
	}

	// make the particle always max brightness
	override fun getBrightness(tint: Float): Int {
		return 15728880
	}

	override fun getType(): ParticleTextureSheet {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT
	}
	
	override fun getSize(tickDelta: Float): Float {
		// lerping between scale and lastScale
		return lerp(tickDelta, this.lastScale, this.scale)
	}
	
	@Environment(EnvType.CLIENT)
	override fun tick() {
		if (age++ >= maxAge) {
			markDead()
		} else {
			//prevAngle = angle
			//wobble (sin wave)
			lastScale = scale
			prevAngle = angle
			angle += rotationSpeed
			scale = sin(age.toFloat() / maxAge * 6.2831855f) * 0.2f + 0.5f
			this.colorAlpha = 1f - age.toFloat() / maxAge.toFloat()
		}
	}

	@Environment(EnvType.CLIENT)
	class Factory(private val sprites: SpriteProvider) : ParticleFactory<DefaultParticleType?> {
		override fun createParticle(
			type: DefaultParticleType?, level: ClientWorld,
			x: Double, y: Double, z: Double, dx: Double, dy: Double, dz: Double
		): Particle {
			return EchoParticle(level, x, y, z, sprites, dx, dy, dz)
		}
	}
}
