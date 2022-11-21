package dev.evoloxi.noiwa.foundation.spell.item.wand

import it.unimi.dsi.fastutil.bytes.ByteArrayList
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.MathHelper
import java.util.*

class WandData(wandStack: ItemStack) {
    private val wandTag: NbtCompound = wandStack.getOrCreateSubNbt("Wand")
	
    /**
     * Spell Pool Operations
     * spell pool is an array of bytes indicating the ordering of remaining spells in wand inventory
     * an additional pointer indicates how much of the pool is used
     */
    val spellPool: ByteArray
        get() = wandTag.getByteArray("SpellPool")

    fun setSpellPoll(pool: ByteArray?) {
        wandTag.putByteArray("SpellPool", pool)
    }

    var spellPoolPointer: Byte
        get() = wandTag.getByte("SpellPoolPointer")
        set(index) {
            wandTag.putByte("SpellPoolPointer", index)
        }

    fun resetSpellPool() {
        if (spellPoolPointer > 0) {
            spellPoolPointer = 0.toByte()
        }
    }

    fun refreshWandPool() {
        if (isShuffle) {
            val pool = ByteArrayList.wrap(spellPool)
            Collections.shuffle(pool)
            wandTag.putByteArray("SpellPool", pool.toByteArray())
        }
        resetSpellPool()
    }
    /** Getters  */
    /** Setters  */
    var isShuffle: Boolean
        get() = wandTag.getBoolean("Shuffle")
        set(shuffle) {
            wandTag.putBoolean("Shuffle", shuffle)
        }
    var casts: Byte
        get() = wandTag.getByte("Casts")
        set(casts) {
            wandTag.putByte("Casts", casts)
        }
    var castDelay: Int
        get() = wandTag.getInt("CastDelay")
        set(castDelay) {
            wandTag.putInt("CastDelay", castDelay)
        }
    var rechargeTime: Int
        get() = wandTag.getInt("RechargeTime")
        set(rechargeTime) {
            wandTag.putInt("RechargeTime", rechargeTime)
        }
    var manaMax: Int
        get() = wandTag.getInt("ManaMax")
        set(manaMax) {
            wandTag.putInt("ManaMax", manaMax)
        }
    var manaChargeSpeed: Int
        get() = wandTag.getInt("ManaChargeSpeed")
        set(manaChargeSpeed) {
            wandTag.putInt("ManaChargeSpeed", manaChargeSpeed)
        }
    var capacity: Byte
        get() = wandTag.getByte("Capacity")
        set(capacity) {
            wandTag.putByte("Capacity", capacity)
        }
    var spread: Float
        get() = wandTag.getFloat("Spread")
        set(spread) {
            wandTag.putFloat("Spread", spread)
        }
    var cooldown: Int
        get() = wandTag.getInt("Cooldown")
        set(cooldown) {
            wandTag.putInt("Cooldown", Math.max(0, cooldown))
        }
    var mana: Float
        get() = wandTag.getFloat("Mana")
        set(mana) {
            wandTag.putFloat("Mana", MathHelper.clamp(mana, 0f, manaMax.toFloat()))
        }
    var textureID: Int
        get() = wandTag.getInt("TextureID")
        set(textureID) {
            wandTag.putInt("TextureID", textureID)
        }
}
