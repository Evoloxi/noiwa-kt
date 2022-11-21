package dev.evoloxi.noiwa.foundation.inventory

import dev.evoloxi.noiwa.foundation.spell.item.SpellItem
import dev.evoloxi.noiwa.foundation.spell.item.wand.WandData
import it.unimi.dsi.fastutil.bytes.ByteArrayList
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList

open class WandInventory(private val wandItemStack: ItemStack) : SimpleInventory(9) {
	private var wandNBT: NbtCompound = wandItemStack.getOrCreateSubNbt("Wand")
	
	init {
		readFromStack()
    }

    fun readFromStack() {
        if (wandItemStack.hasNbt()) {
            val list = DefaultedList.ofSize(size(), ItemStack.EMPTY)
            InventoryUtils.loadAllItems(wandNBT.getCompound("Spells"), list)
            for (i in 0 until size()) {
                setStack(i, list[i])
            }
        }
    }
	
	override fun canInsert(stack: ItemStack): Boolean {
		return stack.item is SpellItem
	}
	
	override fun markDirty() {
		super.markDirty()
		writeToStack()
	}
    /**
     * Write itemStacks into NBT, and update the spell pool
     */
    fun writeToStack() {
        if (!wandNBT.contains("Spells")) wandNBT.put("Spells", NbtCompound())
        val list = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        val pool = ByteArrayList()
        for (i in 0 until size()) {
            list[i] = getStack(i)
            if (!getStack(i).isEmpty) {
                pool.add(i.toByte())
            }
        }
        InventoryUtils.saveAllItems(wandNBT.getCompound("Spells"), list, false)
        val wandData = WandData(wandItemStack)
        wandData.setSpellPoll(pool.toByteArray())
        wandData.refreshWandPool()
	    //wandItemStack.getOrCreateSubNbt("Wand").put("Spells", wandNBT.getCompound("Spells"))
    }
}
