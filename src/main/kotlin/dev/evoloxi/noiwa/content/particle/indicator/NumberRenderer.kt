package dev.evoloxi.noiwa.content.particle.indicator

import dev.evoloxi.noiwa.foundation.Utils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import kotlin.math.roundToInt

object NumberRenderer {
    fun drawDamageNumber(matrix: MatrixStack?, txt: Text, x: Double, y: Double, width: Float, itype: Byte) {
        val tr = MinecraftClient.getInstance().textRenderer
	    if (itype != 1.toByte())
        Utils.renderBackground(matrix, y.roundToInt() - 3, x.roundToInt() - 1, tr.getWidth(txt) + 1, 12)
        tr.draw(matrix, txt, x.toFloat(), y.toFloat(), 0xFFFFFF)
    }
}
