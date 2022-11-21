package dev.evoloxi.noiwa.foundation

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.StringVisitable
import net.minecraft.util.math.MathHelper
import software.bernie.geckolib3.core.util.Color

interface Utils {
	
    fun renderInt(matrix: MatrixStack?, value: Int, x: Int, y: Int, color: Color) {
        val text = value.toString()
        val tr = MinecraftClient.getInstance().textRenderer
        val textVisitable = StringVisitable.plain(text)
        tr.getWidth(textVisitable)

        DrawableHelper.drawCenteredText(matrix, tr, text, x, y, color.color)
    }

    companion object {
        fun renderBackground(matrices: MatrixStack?, y: Int, x: Int, width: Int, height: Int) {
            val color = MathHelper.ceil(255.0 * 0.40) shl 24
            // left
            DrawableHelper.fill(
                matrices,
               (x + width),  //x
               (y + height - 1),  //y
               (x + width + 1),  //x2
               (y + 2),  //y2
                color
            )
            // right
            DrawableHelper.fill(
                matrices,
               (x - 1),  //x
               (y + 2),  //y
               (x),  //x2
               (y + height - 1),  //y2
                color
            )
            // center
            DrawableHelper.fill(
                matrices,
               (x),  //x
               (y + 1),  //y
               (x + width),  //x2
               (y + height),  //y2
                color
            )
        }
    }
}
