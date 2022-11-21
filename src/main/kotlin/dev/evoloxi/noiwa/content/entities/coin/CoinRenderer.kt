package dev.evoloxi.noiwa.content.entities.coin

import dev.evoloxi.noiwa.Core
import com.google.common.collect.Maps
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer

class CoinRenderer(ctx: EntityRendererFactory.Context?) : GeoEntityRenderer<CoinEntity?>(ctx, CoinModel()) {
    companion object {

        val TEXTURE_GEN: Map<CoinVariant, Identifier> = Util.make(Maps.newEnumMap(CoinVariant::class.java)) { map ->
            for (variant in CoinVariant.values()) {
                map[variant] = Core.id("textures/entity/coin/coinstack_${variant.name.toLowerCase()}.png")
            }
        }
        val GEO_GEN: Map<CoinVariant, Identifier> = Util.make(Maps.newEnumMap(CoinVariant::class.java)) { map ->
            for (variant in CoinVariant.values()) {
                map[variant] = Core.id("geo/coinstack_${variant.name.toLowerCase()}.geo.json")
            }
        }
    }
	
	override fun getTexture(entity: CoinEntity?): Identifier {
		return TEXTURE_GEN[entity!!.variant]!!
	}
	
}

