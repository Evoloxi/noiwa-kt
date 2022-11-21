package dev.evoloxi.noiwa.content.entities

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.foundation.*
import dev.evoloxi.noiwa.foundation.CombatHandler.hurtV
import dev.evoloxi.noiwa.foundation.Extensions.broadcastPacket
import dev.evoloxi.noiwa.foundation.Extensions.toInt
import dev.evoloxi.noiwa.foundation.registry.PacketRegistry.ELIXIR_HIT_PACKET_ID
import dev.evoloxi.noiwa.foundation.registry.EntityRegistry
import dev.evoloxi.noiwa.foundation.registry.ItemRegistry
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World
import org.quiltmc.qsl.networking.api.PacketByteBufs

data class ElixirEffectTypes(val unit: (ElixirEntity) -> Unit) {
	companion object {
		val HARMING = ElixirEffectTypes(unit = { e ->
			for (entity in e.world.getOtherEntities(e, e.boundingBox.expand(24.0, 24.0, 24.0)) { it is LivingEntity }) {
				if (entity.squaredDistanceTo(e) < e.radius * e.radius) {
					(entity as LivingEntity).hurtV(e.amplifier * 8.0, DamageType.MAGIC)
				}
			}
			e.spawnParticles()
		})
		val HEALING = ElixirEffectTypes(unit = { e ->
			for (entity in e.world.getOtherEntities(e, e.boundingBox.expand(4.0, 2.0, 4.0)) { it is LivingEntity }) {
				if (entity.squaredDistanceTo(e) < e.radius * e.radius) {
					(entity as LivingEntity).addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, e.duration, e.amplifier))
				}
			}
			e.spawnParticles()
		})
		private fun ElixirEntity.spawnParticles() {
			val entity = this
			val world = entity.world as? ServerWorld ?: return
			val rad = entity.radius
			// spawn glass breaking particles
				world.spawnParticles(
					ItemStackParticleEffect(ParticleTypes.ITEM, ItemStack(ItemRegistry.ELIXIR_OF_HARMING)),
					entity.x,
					entity.y,
					entity.z,
					8,
					0.1,
					0.1,
					0.1,
					0.1
				)
				world.spawnParticles(
					GenericParticleEffect(
						entity.startColor,
						entity.endColor,
						Core.RAND.nextFloat(0.8f, 1.2f)
					),
					entity.x,
					entity.y,
					entity.z,
					40,
					0.31,
					0.31,
					0.31,
				0.1
				)
				val buf = PacketByteBufs.create()
				buf.writeDouble(entity.x)
				buf.writeDouble(entity.y)
				buf.writeDouble(entity.z)
				buf.writeDouble(radius)
				buf.writeInt(startColor.toInt())
				buf.writeInt(endColor.toInt())
			
				broadcastPacket(buf, ELIXIR_HIT_PACKET_ID, world, entity.pos, 128)
		}
	}
}



class ElixirEntity : ThrownItemEntity, FlyingItemEntity {
	constructor(entityType: EntityType<out ElixirEntity?>?, world: World?) : super(entityType, world)
	constructor(world: World?, owner: LivingEntity?) : super(EntityRegistry.ELIXIR, owner, world)
	constructor(world: World?, x: Double, y: Double, z: Double) : super(EntityRegistry.ELIXIR, x, y, z, world)
	
	override fun getDefaultItem(): Item = Items.SPLASH_POTION
	
	
	override fun getGravity(): Float = 0.05f
	
	var startColor: Vec3f = Vec3f(1.0f, 0.0f, 0.0f)
	var endColor: Vec3f = Vec3f(0.0f, 1.0f, 0.0f)
	
	var effect: ElixirEffectTypes = ElixirEffectTypes.HARMING
	var radius: Double = 4.0
	var duration: Int = 40
	var amplifier: Int = 1
	
	override fun onCollision(hitResult: HitResult) {
		effect.unit.invoke(this)
		this.playSound(SoundRegistry.ELIXIR_HIT, 1.0f, Core.RAND.nextFloat(0.8f, 1.2f))
		super.onCollision(hitResult)
		discard()
	}
	
	override fun onBlockCollision(state: BlockState?) {
		super.onBlockCollision(state)
	}
}
