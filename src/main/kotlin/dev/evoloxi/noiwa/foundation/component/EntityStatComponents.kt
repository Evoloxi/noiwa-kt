@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate")
package dev.evoloxi.noiwa.foundation.component

import dev.evoloxi.noiwa.foundation.Extensions.stat
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.MathHelper

data class EntityStatComponents(private val provider: LivingEntity) : ComponentV3, AutoSyncedComponent,
    ServerTickingComponent {
	var timeOfDeath: Long = 0L
	var timeOfRespawn: Long = 0L
	var isGhost: Boolean = false
	var renderGhost: Boolean = false
    var health = 100.0
	var lastHealth = 100.0

    var mana = 100.0
	var lastMana = 100.0

	var timeOfDamage: Long = 0L
	
    override fun writeToNbt(tag: NbtCompound) {
	    val active = NbtCompound()
	    val misc = NbtCompound()
	    
	    active.putDouble("health", health)
	    active.putDouble("mana", mana)
	    active.putDouble("lastHealth", lastHealth)
	    active.putDouble("lastMana", lastMana)
	    misc.putLong("timeOfDamage", timeOfDamage)
	    
	    misc.putBoolean("isGhost", isGhost)
	    misc.putLong("timeOfDeath", timeOfDeath)
	    misc.putLong("timeOfRespawn", timeOfRespawn)
	    misc.putBoolean("renderGhost", renderGhost)
	    
	    tag.put("active", active)
	    tag.put("misc", misc)
    }
	
	override fun readFromNbt(tag: NbtCompound) {
		val active = tag.getCompound("active")
		val misc = tag.getCompound("misc")
		
		health = active.getDouble("health")
		mana = active.getDouble("mana")
		lastHealth = active.getDouble("lastHealth")
		lastMana = active.getDouble("lastMana")
		timeOfDamage = misc.getLong("timeOfDamage")
		isGhost = misc.getBoolean("isGhost")
		timeOfDeath = misc.getLong("timeOfDeath")
		timeOfRespawn = misc.getLong("timeOfRespawn")
		renderGhost = misc.getBoolean("renderGhost")
	}
	
    override fun serverTick() {
	    if (provider is PlayerEntity) {
			val maxHealth = provider.stat.maxHealth
		    val maxMana = provider.stat.maxMana
		    health = MathHelper.clamp(health + 0.01 + maxHealth * 0.001, 0.0, maxHealth)
		    mana = MathHelper.clamp(mana + 0.01 + maxMana * 0.001, 0.0, maxMana)
	    }
        if (provider.world.time % 20 == 0L) {
            //healthModified = false
            provider.syncComponent(CustomComponents.STATS)
	        
        }
    }

    override fun shouldSyncWith(player: ServerPlayerEntity): Boolean {
        return player == provider || (player.squaredDistanceTo(provider) < 64 * 64)
    }

    // only include the stats that have changed in the buffer to reduce network traffic
    override fun writeSyncPacket(buf: PacketByteBuf, recipient: ServerPlayerEntity) {
        val bufferCompound = NbtCompound()
	    val active = NbtCompound()
	    val misc = NbtCompound()
	    
	    misc.putBoolean("isGhost", isGhost)
	    misc.putLong("timeOfDeath", timeOfDeath)
	    misc.putLong("timeOfRespawn", timeOfRespawn)
	    
	    misc.putBoolean("renderGhost", renderGhost)
	    
	    bufferCompound.put("active", active)
		bufferCompound.put("misc", misc)
	    
        buf.writeNbt(bufferCompound)
    }

}

