package dev.evoloxi.noiwa.content.particle.indicator

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11

object IndicatorRenderer {

    @JvmStatic
    fun renderParticles(matrix: MatrixStack, camera: Camera) {
        IndicatorRenderManager.PARTICLES.forEach { renderParticle(matrix, it, camera) }
    }
    @JvmStatic
    private fun renderParticle(matrix: MatrixStack, particle: IndicatorParticle?, camera: Camera) {
        if (particle == null) return
        val distanceSquared = camera.pos.squaredDistanceTo(particle.x, particle.y, particle.z)
        if (distanceSquared > 512) return
        val scaleToGui = 0.020f
        val client = MinecraftClient.getInstance()
        val tickDelta = client.tickDelta
	
	    val x: Double = MathHelper.lerp(tickDelta.toDouble(), particle.xPrev, particle.x)
	    val y: Double = MathHelper.lerp(tickDelta.toDouble(), particle.yPrev, particle.y)
	    val z: Double = MathHelper.lerp(tickDelta.toDouble(), particle.zPrev, particle.z)

        val camPos = camera.pos
	    
        matrix.push()
        matrix.translate(x - camPos.x, y - camPos.y, z - camPos.z)
        matrix.multiply(camera.rotation)
        matrix.scale(-scaleToGui, -scaleToGui, scaleToGui)

        RenderSystem.setShader(GameRenderer::getPositionColorShader)
        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        // disable depth mast for the selected shader
        RenderSystem.disableDepthTest()
	    RenderSystem.enableBlend()
	    RenderSystem.blendFuncSeparate(
		    GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
		    GL11.GL_ZERO);
	
	    NumberRenderer.drawDamageNumber(matrix, particle.txt, 0.0, 0.0, 10f, particle.itype)

        matrix.pop()
    }
}
