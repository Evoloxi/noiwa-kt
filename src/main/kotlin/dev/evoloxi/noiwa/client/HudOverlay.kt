package dev.evoloxi.noiwa.client

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.component.CustomComponents.Companion.STATIC
import dev.evoloxi.noiwa.foundation.handler.Processor.toRoman
import dev.evoloxi.noiwa.foundation.Utils
import com.mojang.blaze3d.systems.RenderSystem
import dev.evoloxi.noiwa.foundation.Extensions.stat
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import software.bernie.geckolib3.core.util.Color
import java.util.regex.Pattern
import kotlin.math.roundToInt

//import static dev.evoloxi.noiwa.Core.DataTrackers.MANA;
//import static dev.evoloxi.noiwa.Core.MANA;
//import static dev.evoloxi.noiwa.Core.DataTrackers.REEE_HP;
class HudOverlay : HudRenderCallback, Utils {
    override fun onHudRender(matrix: MatrixStack, tickDelta: Float) {
	    val tr: TextRenderer = MinecraftClient.getInstance().textRenderer
	    val client: MinecraftClient = MinecraftClient.getInstance()
	    val x = client.window.scaledWidth shr 1
	    val y = client.window.scaledHeight
	    val p: PlayerEntity = client.player!!
	    val staticComponents = p.getComponent(STATIC)
	    val dungeonFloor = staticComponents.dungeonFloor
	    val glyphCount = staticComponents.glyphCount
	    val glyphCountMax = staticComponents.glyphCountMax
	
	    val mana = p.stat.mana
	    val maxMana = p.stat.maxMana
	    val health = p.stat.health
	    val maxHealth = p.stat.maxHealth
	    val defense = p.stat.defense
	    
	
	    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
	    RenderSystem.setShaderTexture(0, BARS)
	
	    var manaWidth = (mana / maxMana * 33).toInt()
	    // the more defense you have, the less get added to the bar width
	    var defenseWidth = if (defense >= 5000) 33 else (defense / (defense + 200) * 33).toInt()
	    var healthWidth = (health / maxHealth * 33).toInt()
	    val mbgWidth = (tr.getWidth(mana.roundToInt().toString() + "") - 31)
	    val dbgWidth = (tr.getWidth(defense.roundToInt().toString() + "") - 31);
	    val hbgWidth = (tr.getWidth(health.roundToInt().toString() + "") - 31)
	    // background
	
	    Utils.renderBackground(matrix, y - 27, x + 94, 79 + dbgWidth, 12)
	    Utils.renderBackground(matrix, y - 14, x + 94, 79 + mbgWidth, 12)
	    Utils.renderBackground(matrix, y - 40, x + 94, 79 + hbgWidth, 12)
	
	    // icons
	    DrawableHelper.drawTexture(matrix, x + 94, y - 12, 35f, 0f, 9, 9, 256, 256)
	    DrawableHelper.drawTexture(matrix, x + 94, y - 25, 16f, 0f, 9, 9, 256, 256)
	    DrawableHelper.drawTexture(matrix, x + 94, y - 38, 0f, 0f, 9, 9, 256, 256)
	
	    // empty bars
	    DrawableHelper.drawTexture(matrix, x + 104, y - 11, 0f, 9f, 33, 7, 256, 256)
	    DrawableHelper.drawTexture(matrix, x + 104, y - 24, 0f, 9f, 33, 7, 256, 256)
	    DrawableHelper.drawTexture(matrix, x + 104, y - 37, 0f, 9f, 33, 7, 256, 256)
	
	    // full bars
	    if (manaWidth > 33) manaWidth = 33
	    if (healthWidth > 33) healthWidth = 33
	    DrawableHelper.drawTexture(matrix, x + 104, y - 24, 0f, 30f, defenseWidth, 7, 256, 256);
	    DrawableHelper.drawTexture(matrix, x + 104, y - 11, 0f, 23f, manaWidth, 7, 256, 256)
	    DrawableHelper.drawTexture(matrix, x + 104, y - 37, 0f, 16f, healthWidth, 7, 256, 256)
	
	    // text
	    renderInt(matrix, defense.toInt(), x + 155 + dbgWidth shr 1, y - 24, Color.ofRGBA(192, 203, 220, 255))
	    renderInt(matrix, mana.toInt(), x + 155 + mbgWidth shr 1, y - 11, Color.ofRGBA(44, 232, 245, 255))
	    renderInt(matrix, health.toInt(), x + 155 + hbgWidth shr 1, y - 37, Color.ofRGBA(255, 85, 85, 255))
	
	    RenderSystem.setShaderTexture(0, ICONS)
	
	    val tby = y / 60
	
	    val dT = "Archaic Dungeon: §cFloor ${toRoman(dungeonFloor)}"
	    val dTW = tr.getWidth(dT.stripColor())
	
	    val gT = "Glyphs: §d$glyphCount/$glyphCountMax"
	    val gTW = tr.getWidth(gT.stripColor())
	    val off: MutableList<Int> = ArrayList()
	
	    off.add((x + x / 1.12 - dTW - gTW - 25).toInt())
	    off.add((x + x / 1.04 - gTW - 15).toInt())
	
	    // // // // BACKGROUND // // // // // // // // // // // // // // // // // // // // // // // // // // // //
	
	    Utils.renderBackground(matrix, tby + 1, off[0], dTW + 16, 13)
	    Utils.renderBackground(matrix, tby + 1, off[1], gTW + 15, 13)
	
	    // // // // ICONS // // // // // // // // // // // // // // // // // // // // // // // // // // // //
	
	    icon(0, off[0] - 5, tby, 128, matrix)
	    icon(3, off[1] - 5, tby, 128, matrix)
	
	    //icon(6, x + 20, y - 56, 128, matrix)
	
	    // // // // TEXT // // // // // // // // // // // // // // // // // // // // // // // // // // // //
	    // increase all offsets by 1
	    for (i in off.indices) off[i] += 13
	
	    txt(off[0], tby + 4, dT, matrix) // Dungeon
	
	    txt(off[1], tby + 4, gT, matrix) // Glyph
	
    }

    companion object {

        fun icon(id: Int, x: Int, y: Int, size: Int, matrixStack: MatrixStack) {
            val v = if (id * 16 >= size) size - id * 16f else 0f
            DrawableHelper.drawTexture(
                matrixStack,
                x,
                y,
                id * 16f,
                v,
                16,
                16,
                size,
                size
            )
        }

        private fun txt(x: Int, y: Int, text: String, matrixStack: MatrixStack) {
            val tr = MinecraftClient.getInstance().textRenderer
            DrawableHelper.drawTextWithShadow(
                matrixStack, tr, Text.of(text), x, y, 0xFFFFFF
            )
        }

        private val BARS = Identifier(
            Core.MOD_ID,
            "textures/gui/bars.png"
        )
        private val ICONS = Identifier(
            Core.MOD_ID,
            "textures/gui/icons.png"
        )
    }
}


private val PATTERN = Pattern.compile("<(#|HEX)([a-fA-F0-9]{6})>")
private fun String.stripColor(): String {
    // replace all color codes and hex codes
    return this.replace("§.", "").replace(PATTERN.toRegex(), "")
}

//DrawableHelper.drawTexture(matrixStack, off[1] - 5, tby, 32f, 0f, 16, 16, 128, 128)
//DrawableHelper.drawTexture(matrixStack, off[0] - 5, tby, 0f, 0f, 16, 16, 128, 128)

//var streakTimer = (p.getComponent(COINS).coinTimer - 1) / 5.0

//        if (streakTimer != 0.0) {
//            val streakTimerInt = (streakTimer * 10.0).roundToInt() / 10.0
//            val plate = "${streakTimerInt}s"
//            val sOff = (tr.getWidth(plate) - 21) / 2
//
//            val color = Color.ofRGBA(255, 255, 85, (min(streakTimer, 1.0) * 255).roundToInt()).color
//            DrawableHelper.drawCenteredText(matrixStack, tr, "§6(§e$plate§6)", x + sOff, y - 63, color)
//
//        }
//         DrawableHelper.drawTextWithShadow(
//            matrixStack, tr, hexColorText(dT), off[0], tby + 4, 0xFFFFFF
//        )
//         DrawableHelper.drawTextWithShadow(
//            matrixStack, tr, hexColorText(gT), off[1], tby + 4, 0xFFFFFF
//        )
