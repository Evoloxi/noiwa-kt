package dev.evoloxi.noiwa.content.particle.generic

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.evoloxi.noiwa.foundation.registry.ParticleRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.*
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.util.registry.Registry
import java.util.*

class GenericParticleEffect(fromColor: Vec3f?, val toColor: Vec3f, scale: Float) : AbstractDustParticleEffect(fromColor, scale) {
	
	val startColor: Vec3f
		get() = color
	
	override fun write(buf: PacketByteBuf) {
		super.write(buf)
		buf.writeFloat(toColor.x)
		buf.writeFloat(toColor.y)
		buf.writeFloat(toColor.z)
	}
	
	override fun asString(): String {
		return String.format(
			Locale.ROOT,
			"%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f",
			Registry.PARTICLE_TYPE.getId(this.type),
			color.x,
			color.y,
			color.z,
			scale,
			toColor.x,
			toColor.y,
			toColor.z
		)
	}
	
	override fun getType(): ParticleType<GenericParticleEffect> {
		return ParticleRegistry.GENERIC
	}
	
	companion object {
		val SCULK_BLUE = Vec3f(Vec3d.unpackRgb(3790560))
		val DEFAULT = GenericParticleEffect(
			SCULK_BLUE,
			DustParticleEffect.RED,
			1.0f
		)
		val CODEC: Codec<GenericParticleEffect> =
			RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<GenericParticleEffect> ->
				instance.group(
					Vec3f.CODEC.fieldOf("fromColor")
						.forGetter { effect: GenericParticleEffect -> effect.color },
					Vec3f.CODEC.fieldOf("toColor")
						.forGetter { effect: GenericParticleEffect -> effect.toColor },
					Codec.FLOAT.fieldOf("scale")
						.forGetter { effect: GenericParticleEffect -> effect.scale }
				)
					.apply(instance) { fromColor: Vec3f, toColor: Vec3f, scale: Float->
						GenericParticleEffect(
							fromColor,
							toColor,
							scale
						)
					}
			}
		val FACTORY: ParticleEffect.Factory<GenericParticleEffect> =
			object : ParticleEffect.Factory<GenericParticleEffect> {
				@Throws(CommandSyntaxException::class)
				override fun read(
					particleType: ParticleType<GenericParticleEffect>,
					stringReader: StringReader
				): GenericParticleEffect {
					val vec3f = readColor(stringReader)
					stringReader.expect(' ')
					val f = stringReader.readFloat()
					val vec3f2 = readColor(stringReader)
					return GenericParticleEffect(vec3f, vec3f2, f)
				}
				
				override fun read(
					particleType: ParticleType<GenericParticleEffect>,
					packetByteBuf: PacketByteBuf
				): GenericParticleEffect {
					val vec3f = readColor(packetByteBuf)
					val f = packetByteBuf.readFloat()
					val vec3f2 = readColor(packetByteBuf)
					return GenericParticleEffect(vec3f, vec3f2, f)
				}
			}
	}
}
