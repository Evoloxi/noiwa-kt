package dev.evoloxi.noiwa.foundation.component

import dev.onyxstudios.cca.api.v3.component.ComponentV3
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

data class EntityCoinComponent(private val provider: LivingEntity) : ComponentV3, AutoSyncedComponent,
    ServerTickingComponent {
    var coinStreak: Int = 0
    var coinTimer: Int = 0

    override fun applySyncPacket(buf: PacketByteBuf) {
        coinStreak = buf.readInt()
        coinTimer = buf.readInt()
    }

    override fun writeSyncPacket(buf: PacketByteBuf, player: ServerPlayerEntity) {
        buf.writeInt(coinStreak)
        buf.writeInt(coinTimer)
    }

    override fun shouldSyncWith(player: ServerPlayerEntity): Boolean {
        return player === provider // only sync with the provider itself
    }

    fun addCoins(amount: Int) {
        coinStreak += amount
        coinTimer = 15
    }

    override fun readFromNbt(tag: NbtCompound) {
        coinStreak = tag.getInt("coinStreak")
        coinTimer = tag.getInt("coinTimer")
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putInt("coinStreak", coinStreak)
        tag.putInt("coinTimer", coinTimer)
    }

    override fun serverTick() {
        if (provider.world.time % 5 == 0L) {
            if (coinTimer > 0) {
                coinTimer --
            } else {
                coinStreak = 0
            }
        }
    }
}
