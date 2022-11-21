package dev.evoloxi.noiwa.foundation.spell.cast

import dev.evoloxi.noiwa.foundation.inventory.WandInventory
import dev.evoloxi.noiwa.foundation.spell.ISpellEnum
import dev.evoloxi.noiwa.foundation.spell.item.SpellItem
import dev.evoloxi.noiwa.foundation.spell.item.wand.WandData

class WandSpellPoolVisitor(private val wandData: WandData, private val wandInventory: WandInventory) :
    SpellPoolVisitor() {
    private var overused = false
    private var spellsRemaining = 0
    override fun peek(): ISpellEnum? {
        if (wandData.spellPoolPointer >= wandData.spellPool.size || overused && spellsRemaining < 1) {
            return null
        }
        val spellItemStack = wandInventory.getStack(wandData.spellPool[wandData.spellPoolPointer.toInt()].toInt())
        if (spellItemStack.isDamageable && spellItemStack.damage == spellItemStack.maxDamage) {
            pass()
            return peek()
        }
        return (spellItemStack.item as SpellItem).spell
    }

    override fun pass() {
        if (wandData.spellPoolPointer + 1 >= wandData.spellPool.size && !overused) {
            wandData.spellPoolPointer = 0.toByte()
            overused = true
            spellsRemaining += wandData.spellPool.size
        } else {
            wandData.spellPoolPointer = (wandData.spellPoolPointer + 1).toByte()
        }
        spellsRemaining--
    }

    override fun passAndConsume() {
        val itemStack = wandInventory.getStack(wandData.spellPool[wandData.spellPoolPointer.toInt()].toInt())
        if (itemStack.isDamageable && itemStack.damage < itemStack.maxDamage) {
            itemStack.damage = itemStack.damage + 1
            wandInventory.writeToStack()
        }
        pass()
    }
}
