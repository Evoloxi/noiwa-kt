package dev.evoloxi.noiwa.foundation.component

import dev.evoloxi.noiwa.foundation.component.CustomComponents.Companion.STATIC
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID

data class StaticComponents(private val provider: LivingEntity) : ComponentV3, AutoSyncedComponent,
    ServerTickingComponent {
    var dungeonFloor = 0
    var glyphCount = 0
    var glyphCountMax = 0
	var lastDamager = UUID(0, 0)

    var coins = 0


    override fun shouldSyncWith(player: ServerPlayerEntity): Boolean {
        return player === provider // only sync with the provider itself
    }

    override fun readFromNbt(tag: NbtCompound) {
        dungeonFloor = tag.getInt("dungeonFloor")
        glyphCount = tag.getInt("glyphCount")
        glyphCountMax = tag.getInt("glyphCountMax")
        coins = tag.getInt("coins")
	    if (tag.containsUuid("lastDamager")) {
		    lastDamager = tag.getUuid("lastDamager")
	    }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putInt("dungeonFloor", dungeonFloor)
        tag.putInt("glyphCount", glyphCount)
        tag.putInt("glyphCountMax", glyphCountMax)
        tag.putInt("coins", coins)
	    if (lastDamager != UUID(0, 0))
		tag.putUuid("lastDamager", lastDamager)
	   
    }

    override fun serverTick() {
        if (provider.world.time % 20 == 0L) {
            provider.syncComponent(STATIC)
        }
    }
}
