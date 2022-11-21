package dev.evoloxi.noiwa.content.particle.cube

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import dev.evoloxi.noiwa.Core
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.math.*
import net.minecraft.util.math.MathHelper.clamp
import net.minecraft.util.math.MathHelper.lerp
import kotlin.math.pow

class CubeParticle(
	world: ClientWorld?,
	x: Double,
	y: Double,
	z: Double,
	sprites: SpriteProvider,
	motionX: Double,
	motionY: Double,
	motionZ: Double
) : SpriteBillboardParticle(world, x, y, z) {
	
	companion object {
		val CUBE = arrayOf( // TOP
			Vec3d(1.0, 1.0, -1.0), Vec3d(1.0, 1.0, 1.0), Vec3d(-1.0, 1.0, 1.0), Vec3d(-1.0, 1.0, -1.0),  // BOTTOM
			Vec3d(-1.0, -1.0, -1.0), Vec3d(-1.0, -1.0, 1.0), Vec3d(1.0, -1.0, 1.0), Vec3d(1.0, -1.0, -1.0),  // FRONT
			Vec3d(-1.0, -1.0, 1.0), Vec3d(-1.0, 1.0, 1.0), Vec3d(1.0, 1.0, 1.0), Vec3d(1.0, -1.0, 1.0),  // BACK
			Vec3d(1.0, -1.0, -1.0), Vec3d(1.0, 1.0, -1.0), Vec3d(-1.0, 1.0, -1.0), Vec3d(-1.0, -1.0, -1.0),  // LEFT
			Vec3d(-1.0, -1.0, -1.0), Vec3d(-1.0, 1.0, -1.0), Vec3d(-1.0, 1.0, 1.0), Vec3d(-1.0, -1.0, 1.0),  // RIGHT
			Vec3d(1.0, -1.0, 1.0), Vec3d(1.0, 1.0, 1.0), Vec3d(1.0, 1.0, -1.0), Vec3d(1.0, -1.0, -1.0)
		)
	}
	
	var TEXTURE_SHEET: ParticleTextureSheet = object : ParticleTextureSheet {
		override fun begin(bufferBuilder: BufferBuilder, textureManager: TextureManager) {
			RenderSystem.setShaderTexture(0, Core.id("textures/special/blank.png"))
			
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE)
			
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
			
			
		}
		
		override fun toString(): String = "CUBE_SHEET"
		
		override fun draw(tessellator: Tessellator) {
			tessellator.draw()
			RenderSystem.blendFunc(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
			)
		}
	}

	
	var scale = 0f
	var hot = false
	fun setBBS(scale: Float) {
		this.scale = scale
		setBoundingBoxSpacing(scale * 0.5f, scale * 0.5f)
	}
	
	fun averageAge(age: Int) {
		maxAge = (age + (random.nextDouble() * 2.0 - 1.0) * 8).toInt()
	}
	
	private var billowing = false
	
	init {
		velocityX = motionX
		velocityY = motionY
		velocityZ = motionZ
		setBBS(0.2f)
		setSprite(sprites)
	}
	
	override fun tick() {
		if (hot && age > 0) {
			if (this.prevPosY == y) {
				billowing = true
				collidesWithWorld = false // TODO: revisit
				if (velocityX == 0.0 && velocityZ == 0.0) {
					val diff = Vec3d.of(BlockPos(x, y, z)).add(0.5, 0.5, 0.5).subtract(x, y, z)
					velocityX = -diff.x * 0.1
					velocityZ = -diff.z * 0.1
				}
				velocityX *= 1.1
				velocityY *= 0.9
				velocityZ *= 1.1
			} else if (billowing) {
				velocityY *= 1.2
			}
		}
		super.tick()
	}
	
	override fun buildGeometry(builder: VertexConsumer, renderInfo: Camera, delta: Float) {
		val projectedView: Vec3d = renderInfo.pos
		val lerpedX = (lerp(delta.toDouble(), this.velocityX, x) - projectedView.x)
		val lerpedY = (lerp(delta.toDouble(), this.velocityY, y) - projectedView.y)
		val lerpedZ = (lerp(delta.toDouble(), this.velocityZ, z) - projectedView.z)
		
		// int light = getBrightnessForRender(p_225606_3_);
		
		// int light = getBrightnessForRender(p_225606_3_);
		val light: Int = 15728880
		val ageMultiplier = 1 - clamp((age + delta).toDouble(), 0.0, maxAge.toDouble()).pow(3.0) / maxAge.toDouble().pow(3.0)
		
		for (i in 0..5) {
			// 6 faces to a cube
			for (j in 0..3) {
				var vec: Vec3d = CUBE[i * 4 + j].multiply(-scale.toDouble())
				vec = vec /* .rotate(?) */
					.multiply(scale * ageMultiplier)
					.add(lerpedX, lerpedY, lerpedZ)
				builder.vertex(vec.x, vec.y, vec.z)
					.uv((j / 2).toFloat(), (j % 2).toFloat())
					.color(0.8f, 0.3f, 0.3f, 0.8f)
					.light(light)
					.next()
			}
		}
		
	}
	
	override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT
	
	class Factory(private val sprites: SpriteProvider) : ParticleFactory<DefaultParticleType?> {
		override fun createParticle(
			type: DefaultParticleType?, level: ClientWorld,
			x: Double, y: Double, z: Double, dx: Double, dy: Double, dz: Double
		): Particle {
			return CubeParticle(level, x, y, z, sprites, dx, dy, dz)
		}
	}
}

