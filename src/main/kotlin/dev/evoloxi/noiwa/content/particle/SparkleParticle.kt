package dev.evoloxi.noiwa.content.particle

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType

class SparkleParticle constructor(
    level: ClientWorld?, x: Double, y: Double, z: Double,
    spriteSet: SpriteProvider?, dx: Double, dy: Double, dz: Double
) : AnimatedParticle(level, x, y, z, spriteSet, 0f) {
    init {
        maxAge = 12 + random.nextInt(5)
        setSpriteForAge(spriteSet)
        scale = 0.5f
    }

    // make the particle always max brightness
    override fun getBrightness(tint: Float): Int {
        return 15728880
    }

    override fun getType(): ParticleTextureSheet {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT
    }

    @Environment(EnvType.CLIENT)
    class Factory(private val sprites: SpriteProvider) : ParticleFactory<DefaultParticleType?> {
        override fun createParticle(
            type: DefaultParticleType?, level: ClientWorld,
            x: Double, y: Double, z: Double, dx: Double, dy: Double, dz: Double
        ): Particle {
            return SparkleParticle(level, x, y, z, sprites, dx, dy, dz)
        }
    }
}
