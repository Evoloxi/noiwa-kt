package dev.evoloxi.noiwa.foundation.spell.entity.renderer

import dev.evoloxi.noiwa.foundation.spell.entity.SpellEntityBase
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory.Context

/**
 * Invisible Renderer for all entities extending [SpellEntityBase]
 */
class InvisibleEntityRenderer(context: Context) : EntityRenderer<SpellEntityBase>(context) {
	override fun getTexture(entity: SpellEntityBase) = null
}
