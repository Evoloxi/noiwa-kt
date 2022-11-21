package dev.evoloxi.noiwa.content.entities

import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory.Context

class LavaBallRenderer(context: Context) : EntityRenderer<LavaBallEntity>(context) {
	override fun getTexture(entity: LavaBallEntity) = null
}
