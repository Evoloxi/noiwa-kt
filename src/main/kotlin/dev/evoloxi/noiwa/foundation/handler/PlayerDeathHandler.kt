package dev.evoloxi.noiwa.foundation.handler

import dev.evoloxi.noiwa.foundation.Extensions.notify
import dev.evoloxi.noiwa.foundation.Extensions.parseRGB
import dev.evoloxi.noiwa.foundation.Extensions.schedule
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import dev.evoloxi.noiwa.foundation.component.CustomComponents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory

class PlayerDeathHandler {
	companion object {
		fun notify(player: PlayerEntity) {
			player.notify("<#f56b6f>You Died!")
			player.playSound(SoundRegistry.DEATH, SoundCategory.PLAYERS, 1f, 1f)
			player.velocity = player.velocity.add(0.0, 0.8, 0.0)
			player.setPosition(player.pos.add(0.0, 1.0, 0.0))
			val stats = player.getComponent(CustomComponents.STATS)
			stats.timeOfDeath = player.world.time
			stats.isGhost = true
			stats.renderGhost = true
			player.syncComponent(CustomComponents.STATS)
			
			if (!player.world.isClient) {
				val serverPlayer = player as ServerPlayerEntity
				serverPlayer.networkHandler.sendPacket( TitleS2CPacket(parseRGB("<#f56b6f>§lYou Died!")) )
			}
			
			schedule(13000) {
				player.playSound(SoundRegistry.RESPAWN, SoundCategory.PLAYERS, 1f, 1f)
				if (!player.isCreative) {
					player.abilities.allowFlying = false
					player.sendAbilitiesUpdate()
					player.abilities.flying = false
					player.velocityModified = false
					player.flyDistance = 0f
					player.sendAbilitiesUpdate()
				}
				if (!player.world.isClient) {
					val serverPlayer = player as ServerPlayerEntity
					serverPlayer.networkHandler.sendPacket( TitleS2CPacket(parseRGB("<#65fe92>§lRespawned!")) )
				}
				stats.isGhost = false
				stats.timeOfRespawn = player.world.time
				player.syncComponent(CustomComponents.STATS)
				
				schedule(10000) {
					stats.renderGhost = false
					player.syncComponent(CustomComponents.STATS)
				}
				
			}
		}
	}
}




