package dev.evoloxi.noiwa.foundation.inventory

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.collection.DefaultedList

object InventoryUtils {
    fun saveAllItems(tag: NbtCompound, list: DefaultedList<ItemStack?>, saveEmpty: Boolean): NbtCompound {
        val nbtList = NbtList()
		for (i in list.indices) {
			if (!list[i].isEmpty || saveEmpty) {
				val nbtCompound = NbtCompound()
				nbtCompound.putInt("Slot", i)
				list[i].writeNbt(nbtCompound)
				nbtList.add(nbtCompound)
			}
		}
		tag.put("Items", nbtList)
		return tag
    }

    fun loadAllItems(tag: NbtCompound, list: DefaultedList<ItemStack?>) {
		val nbtList = tag.getList("Items", 10)
	    		for (i in 0 until nbtList.size) {
			val nbtCompound = nbtList.getCompound(i)
			val j = nbtCompound.getInt("Slot")
			if (j >= 0 && j < list.size) {
				list[j] = ItemStack.fromNbt(nbtCompound)
			}
		}
	}
}
