package dev.evoloxi.noiwa.content.entities.rockProjectile

import dev.evoloxi.noiwa.Core.Companion.id
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.util.Identifier
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer

class RockProjectileRenderer(context: EntityRendererFactory.Context?) : GeoProjectilesRenderer<RockEntity>(context, RockModel()) {
    override fun getTextureLocation(instance: RockEntity?): Identifier {
        return id("textures/entity/rock.png")
    }
}
