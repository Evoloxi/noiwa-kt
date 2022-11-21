package dev.evoloxi.noiwa.foundation.spell.item.wand

import dev.evoloxi.noiwa.foundation.inventory.WandInventory
import dev.evoloxi.noiwa.foundation.registry.ItemRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

object DefaultWands {
    val HANDGUN = makeWandStack(
        4, false, 30, 1.toByte(),
        0f, 8, 3.toByte(), 1, 100,
        ItemRegistry.SPELL_SPARK_BOLT, ItemRegistry.SPELL_SPARK_BOLT
    ).setCustomName(Text.translatable("item.noitaWands.wand.starting"))
    val BOMB_WAND = makeWandStack(
        2, false, 10, 1.toByte(),
        0f, 1, 1.toByte(), 2, 100,
        ItemRegistry.BOMB
    ).setCustomName(Text.translatable("item.noitaWands.wand.starting"))

    fun makeWandStack(
        castDelay: Int, shuffle: Boolean, manaChargeSpeed: Int, casts: Byte,
        spread: Float, rechargeTime: Int, capacity: Byte, textureID: Int, manaMax: Int, vararg spells: Item?
    ): ItemStack {
        val itemStack = ItemStack(ItemRegistry.WAND, 1)
        val wandData = WandData(itemStack)
        wandData.castDelay = castDelay
        wandData.mana = manaMax.toFloat()
        wandData.isShuffle = shuffle
        wandData.manaChargeSpeed = manaChargeSpeed
        wandData.cooldown = 0
        wandData.casts = casts
        wandData.spread = spread
        wandData.rechargeTime = rechargeTime
        wandData.capacity = capacity
        wandData.textureID = textureID
        wandData.manaMax = manaMax
        val inventory = WandInventory(itemStack)
        for (i in spells.indices) {
            inventory.setStack(i, ItemStack(spells[i]))
        }
        inventory.writeToStack()
        return itemStack
    }
}
