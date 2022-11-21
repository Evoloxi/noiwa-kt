@file:Suppress("unused")

package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core.Companion.id
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry
import java.lang.reflect.Field

@Suppress("MemberVisibilityCanBePrivate")
object SoundRegistry {
	val ERROR = soundEvent("error")
	val COIN_PICKUP_1 = soundEvent("coin.pickup.1")
	val COIN_PICKUP_2 = soundEvent("coin.pickup.2")
	val COIN_PICKUP_3 = soundEvent("coin.pickup.3")
	val COIN_PICKUP_4 = soundEvent("coin.pickup.4")
	val COIN_PICKUP_5 = soundEvent("coin.pickup.5")
	val COIN_PICKUP_6 = soundEvent("coin.pickup.6")
	val COIN_PICKUP_7 = soundEvent("coin.pickup.7")
	val COIN_PICKUP_8 = soundEvent("coin.pickup.8")
	val COIN_PICKUP_9 = soundEvent("coin.pickup.9")
	val STONE_CRACK_1 = soundEvent("stone.crack.1")
	val STONE_CRACK_2 = soundEvent("stone.crack.2")
	val STONE_CRACK_3 = soundEvent("stone.crack.3")
	val LAUNCHER_SHOOT = soundEvent("launcher.shoot")
	val TRIGGER_1 = soundEvent("weapon.trigger.1")
	val TRIGGER_2 = soundEvent("weapon.trigger.2")
	val TRIGGER_3 = soundEvent("weapon.trigger.3")
	val RELOAD = soundEvent("weapon.reload")
	val MECHA_RATTLE_1 = soundEvent("mecha.rattle.1")
	val ECHO_0 = soundEvent("echo.0")
	val ECHO_1 = soundEvent("echo.1")
	val ECHO_2 = soundEvent("echo.2")
	val NOTE_TAIKO = soundEvent("note.taiko")
	val NOTE_KALIMBA = soundEvent("note.kalimba")
	val NOTE_KOTO = soundEvent("note.koto")
	val SPIKE_TRAP_IN = soundEvent("spiketrap.in")
	val SPIKE_TRAP_OUT = soundEvent("spiketrap.out")
	val SPRING_TRAP_BOING_1 = soundEvent("springtrap.boing.1")
	val SPRING_TRAP_BOING_2 = soundEvent("springtrap.boing.2")
	val SPRING_TRAP_BOING_3 = soundEvent("springtrap.boing.3")
	val SPRING_TRAP_BOING_4 = soundEvent("springtrap.boing.4")
	val SPRING_TRAP_BOING_5 = soundEvent("springtrap.boing.5")
	val KILL = soundEvent("player.kill")
	val DEATH = soundEvent("player.death")
	val RESPAWN = soundEvent("player.respawn")
	val COUNTDOWN = soundEvent("player.countdown")
	val COIN_SMALL = soundEvent("coin.small")
	val COIN_BIG = soundEvent("coin.big")
	val ELIXIR_HIT = soundEvent("elixir.hit")
	
	val declaredFields: Array<Field> = SoundRegistry::class.java.declaredFields
	val soundClass: Class<SoundEvent> = SoundEvent::class.java
	
	private fun soundEvent(id: String): SoundEvent {
		return SoundEvent(id(id))
	}
	
	val coinPickupSounds = arrayOf(
		COIN_BIG,
		COIN_SMALL
	)
	
	val echoSounds = arrayOf(ECHO_0, ECHO_1, ECHO_2)
	val stoneCrackSounds = arrayOf(STONE_CRACK_1, STONE_CRACK_2, STONE_CRACK_3)
	val triggerSounds = arrayOf(TRIGGER_1, TRIGGER_2, TRIGGER_3)
	val boingSounds = arrayOf(
		SPRING_TRAP_BOING_1,
		SPRING_TRAP_BOING_2,
		SPRING_TRAP_BOING_3,
		SPRING_TRAP_BOING_4,
		SPRING_TRAP_BOING_5
	)
	
	fun register() {
		declaredFields.filter { it.type == soundClass }.map { it.get(SoundRegistry) as SoundEvent }
			.forEach { Registry.register(Registry.SOUND_EVENT, it.id, it) }
	}
	
	fun getCoinSound(): SoundEvent {
		return coinPickupSounds.random()
	}
	
	fun getStoneSound(): SoundEvent {
		return stoneCrackSounds.random()
	}
	
	fun getTriggerSound(): SoundEvent {
		return triggerSounds.random()
	}
	
	fun getEchoSound(): SoundEvent {
		return echoSounds.random()
	}
	
	fun getBoingSound(): SoundEvent {
		return boingSounds.random()
	}
}
