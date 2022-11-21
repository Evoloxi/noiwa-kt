@file:Suppress("LocalVariableName", "MemberVisibilityCanBePrivate", "NAME_SHADOWING")

package dev.evoloxi.noiwa.foundation

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.calculation.Storage
import dev.evoloxi.noiwa.content.entities.coin.CoinEntity
import dev.evoloxi.noiwa.foundation.Extensions.broadcastPacket
import dev.evoloxi.noiwa.foundation.Extensions.stat
import dev.evoloxi.noiwa.foundation.component.CustomComponents
import dev.evoloxi.noiwa.foundation.handler.PlayerDeathHandler
import dev.evoloxi.noiwa.foundation.registry.PacketRegistry
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import io.netty.buffer.Unpooled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import java.util.*
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

object CombatHandler {
	
	fun LivingEntity.hurtV(amount: Double, damageType: DamageType) {
		if (!this.isAlive) return
		val VICTIM = this.getComponent(CustomComponents.STATS)
		if (VICTIM.isGhost) return
		val defense = this.stat.defense
		val finalAmount = amount * (1f - (defense / (defense + 100f)))
		VICTIM.lastHealth = VICTIM.health
		VICTIM.health = max(0.0, VICTIM.health - finalAmount)
		VICTIM.timeOfDamage = this.world.time
		this.timeUntilRegen = 20
		this.hurtTime = 10
		world.sendEntityStatus(this, 2.toByte())
		
		val buf = PacketByteBuf(Unpooled.buffer())
		buf.writeByte(1) // type
		buf.writeByte(damageType.toByte().toInt())
		buf.writeLong(finalAmount.toLong())
		buf.writeDouble(this.x)
		buf.writeDouble(this.y)
		buf.writeDouble(this.z)
		buf.writeFloat(this.width)
		buf.writeFloat(this.height)
		
		world.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_HURT, SoundCategory.HOSTILE, 0.38f, 1f)
		if (damageType.isCritical) world.playSound(null, x, y, z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 0.2f, 1f)
		
		broadcastPacket(
			buf = buf,
			id = PacketRegistry.INDICATOR_PACKET_ID,
			world = world, origin = pos,
			radius = 64
		)
		
		// sync health with every player within 64 blocks
		this.syncComponent(CustomComponents.STATS)
		
		
		if (VICTIM.health <= 0) {
			if (this is PlayerEntity) {
				PlayerDeathHandler.notify(this)
			} else {
				this.getLastDamager()?.playSound(SoundRegistry.getCoinSound(), SoundCategory.MASTER, 0.7f, Core.RAND.nextFloat(0.9f, 1.1f))
				try {
					this.kill()
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}
	}
	
	fun LivingEntity.healV(amount: Float) {
		val STATS = this.getComponent(CustomComponents.STATS)
		STATS.health += MathHelper.clamp(amount.toDouble(), 0.0, this.stat.maxHealth)
	}
	
	/**
	 * Simulates a hit on {this}
	 * @param victim The entity that is damaged
	 * @param damageType The type of damage e.g. melee, ranged, magic, etc
	 **/
	
	fun LivingEntity.attackV(victim: LivingEntity, damageType: DamageType) {
		if (world is ServerWorld) {
			if (victim is CoinEntity) return
			val damage = this.stat.damage
			val critChance = this.stat.critChance
			val critDamage = this.stat.critDamage
			val strength = this.stat.strength
			val echo = this.stat.echo
			var damageType = damageType
			val scale = if (damageType.isScaled)
				Storage.getAttackScale(this.uuid) else 1f
			println("scale: $scale")
			println("scale:2: ${this.getHandSwingProgress(-1f)}")
			
			/****************\
			Damage Formula
			\****************/
			
			val crit = (Core.RAND.nextInt(0, 100) <= critChance && scale >= 1 || damageType.isCritical)
			var amountToDamage = (1 + damage) * (1 + strength / 100) * scale
			
			if (crit) damageType = damageType.getCritVariant()
			
			amountToDamage *= if (crit) (critDamage + 1) / 100f else 1.0
			amountToDamage *= if (this.velocity.y < -0.2) 1.5f else 1f
			
			if (this is PlayerEntity) {
				victim.setLastDamager(this)
			}
			
			EnchantmentHelper.onUserDamaged(victim as? LivingEntity, this)
			EnchantmentHelper.onTargetDamaged(this, victim)
			
			victim.hurtV(amountToDamage, damageType)
			if (damageType.doKnockback)
				victim.takeKnockback(
					0.4,
					+sin(yaw * Core.TO_RAD),
					-cos(yaw * Core.TO_RAD)
				)
			victim.applyDamageEffects(this, victim)
			if (echo > 0 && scale >= 0) {
				// create coroutine
				CoroutineScope(Dispatchers.Default).launch {
					// echo: 100 = repeat 1 time, 120 = repeat 1 times, with 20% chance to repeat again, etc
					val repeat = ceil(echo / 100f).toInt()
					var chance = echo / 100f
					var damageMultiplier = 1f
					val rand = Random()
					//println("repeat: $repeat, chance: $chance")
					val attacker = this@attackV as PlayerEntity
					for (i in 0..repeat) {
						if ((repeat > 1 || rand.nextFloat() <= chance) && victim.isAlive && damageMultiplier > 0.05f) {
							
							while (Core.isPaused) delay(100)
							
							val x = victim.pos.x + rand.nextFloat(-0.3f, 0.3f)
							val y = victim.pos.y + rand.nextFloat(0.3f, 0.6f)
							val z = victim.pos.z + rand.nextFloat(-0.3f, 0.3f)
							(world as ServerWorld).spawnParticles(
								ParticleTypes.SONIC_BOOM,
								x, y, z,
								1,
								0.0, 0.0, 0.0,
								0.0
							)
							world.playSound(
								null,
								attacker.x, attacker.y, attacker.z,
								SoundRegistry.getEchoSound(),
								SoundCategory.VOICE,
								1f, 1f
							)
							val repeatCrit = rand.nextInt(0, 10) <= 9 && crit
							chance = -1.0
							delay(410)
							victim.hurtV(amountToDamage * damageMultiplier, if (repeatCrit) DamageType.ECHO_CRIT else DamageType.ECHO)
							damageMultiplier *= 0.9f
						}
					}
				}
			}
			
		}
	}
	
	fun LivingEntity.setLastDamager(player: PlayerEntity) {
		val STATIC = this.getComponent(CustomComponents.STATIC)
		STATIC.lastDamager = player.uuid
		this.syncComponent(CustomComponents.STATIC)
	}
	
	fun LivingEntity.getLastDamager(): PlayerEntity? {
		val STATIC = this.getComponent(CustomComponents.STATIC)
		return if (STATIC.lastDamager != UUID(0, 0)) {
			world.getPlayerByUuid(STATIC.lastDamager)
		} else null
	}
	
}

