package dev.evoloxi.noiwa.foundation.inventory

import com.mojang.blaze3d.systems.RenderSystem
import dev.evoloxi.noiwa.Core.Companion.id
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.minecraft.ClientOnly
import java.awt.Dimension

@ClientOnly
class WandHandledScreen(handler: WandScreenHandler, player: PlayerInventory, title: Text?) :
	HandledScreen<WandScreenHandler>(handler, player, handler.wandStack.name) {
	
	init {
		val dimension: Dimension = handler.dimension
		this.backgroundWidth = dimension.width
		this.backgroundHeight = dimension.height
	}
	
	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		this.renderBackground(matrices)
		super.render(matrices, mouseX, mouseY, delta)
		this.drawMouseoverTooltip(matrices, mouseX, mouseY)
	}
	
	override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {}
	
	override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
		RenderSystem.setShaderTexture(0, GUI_TEXTURE)
		
		val j = (width - backgroundWidth) / 2
		val k = (height - backgroundHeight) / 2
		this.drawTexture(matrices, j - 30, k - 50, 0, 0, backgroundWidth + 30, 218)
	}
	
	companion object {
		private val GUI_TEXTURE: Identifier = id("textures/gui/spell_screen.png")
	}
}
