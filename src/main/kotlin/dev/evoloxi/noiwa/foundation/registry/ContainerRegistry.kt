package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core.Companion.id
import dev.evoloxi.noiwa.foundation.inventory.WandScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.registry.Registry

object ContainerRegistry {
	val WAND_CONTAINER: ScreenHandlerType<WandScreenHandler> = Registry.register(
		Registry.SCREEN_HANDLER, id("wand"), ExtendedScreenHandlerType { syncId, inventory, buf ->
			WandScreenHandler(
				syncId,
				inventory,
				buf.readItemStack()
			)
		}
	)
	fun register() {
	}
}
