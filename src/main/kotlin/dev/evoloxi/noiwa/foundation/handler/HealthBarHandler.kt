package dev.evoloxi.noiwa.foundation.handler

import dev.evoloxi.noiwa.Core
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity

object HealthBarHandler {
	//TODO-LIST: render HP icon, HP bar and HP value eg. (100/100) below nametag
	//private val tr: TextRenderer = MinecraftClient.getInstance().textRenderer
	val client: MinecraftClient = MinecraftClient.getInstance()
	private val camera: Camera = client.gameRenderer.camera
	private val TEXTURE = Core.id("textures/gui/enemy_health_bar.png")
	
	@JvmStatic
	fun render(entity: LivingEntity, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider) {
		// skip client player
//		return
//		if (entity == client.player) return
//		if (entity.squaredDistanceTo(client.cameraEntity) > 1024) return //FIXME: temp breakpoint
//		if (!entity.shouldRender(camera.pos.x, camera.pos.y, camera.pos.z)) return
//
//		val sneaking = entity.isSneaky
//		val height = entity.height + 0.3
//		val tickDelta = client.tickDelta
//		val stats = entity.getComponent(CustomComponents.STATS)
//
// 		matrices.push()
//		matrices.translate(0.0, height, 0.0)
//		matrices.multiply(camera.rotation)
//		matrices.multiply(-0.025f, -0.025f, 0.025f)
//
//		val x = -(39 * 0.5f)
//		val y = if (sneaking) 10f else 0f
//
//		RenderSystem.setShader(GameRenderer::getPositionColorShader);
//		RenderSystem.enableDepthTest()
//		RenderSystem.enableBlend()
//		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
//			GL11.GL_ZERO)
//		RenderSystem.setShaderTexture(0, TEXTURE)
//		val delta = (tickDelta + client.world?.time!! - stats.timeOfDamage) * 0.5f
//		// multiply delta by difference between current and last health
//		//delta *= (stats.lastHealth - stats.health) * 0.09f
//		val health = MathHelper.lerp(min(delta,1f), stats.lastHealth, stats.health)
//		val width = (health / stats.maxHealth * 39).toInt()
//		DrawableHelper.drawTexture(matrices, x.toInt(), y.toInt(), 0f, 14f, 39, 7, 64, 64)
//		DrawableHelper.drawTexture(matrices, x.toInt(), y.toInt(), 0f, 7f, width, 7, 64, 64)
//		matrices.pop()
	}
}
