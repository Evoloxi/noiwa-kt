package dev.evoloxi.noiwa.client

import dev.evoloxi.noiwa.client.shader.DeathFx
import dev.evoloxi.noiwa.client.tooltip.ItemTooltip
import dev.evoloxi.noiwa.content.block.TileRegistry
import dev.evoloxi.noiwa.foundation.inventory.WandHandledScreen
import dev.evoloxi.noiwa.foundation.registry.*
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

class ClientCore : ClientModInitializer {
	
    override fun onInitializeClient(mod: ModContainer?) {
	    HandledScreens.register(ContainerRegistry.WAND_CONTAINER, ::WandHandledScreen)
        HudRenderCallback.EVENT.register(HudOverlay())
	    
	    DeathFx.register()
	
	    EntityRegistry.registerClient()
	    
        TileRegistry.registerRenderers()
        ParticleRegistry.registerClient()

        PacketRegistry.registerS2CPackets()
	    ItemRegistry.registerClient()
	
	    ItemTooltip.register()
	
    }
}

