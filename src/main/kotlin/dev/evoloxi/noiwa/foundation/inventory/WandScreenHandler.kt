package dev.evoloxi.noiwa.foundation.inventory

import dev.evoloxi.noiwa.foundation.registry.ContainerRegistry
import dev.evoloxi.noiwa.foundation.spell.item.SpellItem
import dev.evoloxi.noiwa.foundation.spell.item.wand.WandItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import java.awt.Dimension
import java.awt.Point

class WandScreenHandler(synchronizationID: Int, val playerInventory: PlayerInventory, val wandStack: ItemStack) :
	ScreenHandler(ContainerRegistry.WAND_CONTAINER, synchronizationID) {
	private val padding = 8
	private val titleSpace = 10
	private val height = 1
	private var wandInventory: WandInventory = WandInventory(wandStack)
	
	init {
		setupContainer()
	}
	
	override fun syncState() {
		super.syncState()
		// write to wandStack
		wandInventory.writeToStack()
	}
	
	private fun setupContainer() {
		val i: Int = (this.height - 4) * 18
		var m = 0
		var n = 0
			while (n < this.height) {
				while (m < 9) {
					this.addSlot(Slot(wandInventory, m + n * 9, 8 + m * 18, n * 18 - 2))
					++m
				}
				++n
			}
			n = 0
			while (n < 3) {
				m = 0
				while (m < 9) {
					this.addSlot(Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i))
					++m
				}
				++n
			}
			n = 0
			while (n < 9) {
				this.addSlot(Slot(playerInventory, n, 8 + n * 18, 161 + i))
				++n
			}
		}
	
	val item: WandItem
		get() = wandStack.item as WandItem
	val dimension: Dimension
		get() {
			return Dimension(
				padding * 2 + 9 * 18,
				padding * 2 + titleSpace * 2 + 8 + (1 + 4) * 18
			)
		}

	override fun quickTransfer(player: PlayerEntity, index: Int): ItemStack {
		var itemstack = ItemStack.EMPTY
		val slot: Slot = this.slots[index]
		if (slot.hasStack()) {
			val itemstack1 = slot.stack
			itemstack = itemstack1.copy()
			if (index < this.playerInventory.size()) {
				if (!this.insertItem(itemstack1, this.playerInventory.size(), this.slots.size, true) || !canInsert(itemstack1)) {
					return ItemStack.EMPTY
				}
			} else if (itemstack1.item !is SpellItem) {
				return ItemStack.EMPTY
			} else if (!this.insertItem(itemstack1, 0, this.playerInventory.size(), false) || !canInsert(itemstack1)) {
				return ItemStack.EMPTY
			}
			if (itemstack1.isEmpty) {
				slot.stack = ItemStack.EMPTY
			} else {
				slot.markDirty()
			}
		}
		return itemstack
	}
	
	fun canInsert(stack: ItemStack): Boolean {
		return stack.item is SpellItem
	}
	
	override fun canUse(player: PlayerEntity): Boolean {
		return wandStack.item is WandItem
	}
}
