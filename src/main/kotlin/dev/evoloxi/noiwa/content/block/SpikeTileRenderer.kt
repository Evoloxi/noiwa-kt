package dev.evoloxi.noiwa.content.block


import dev.evoloxi.noiwa.Core
import net.minecraft.util.Identifier
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer

class SpikeBlockModel : AnimatedGeoModel<SpikeTrapTile?>(){
	override fun getModelResource(obj: SpikeTrapTile?): Identifier {
		return Core.id("geo/spikes.geo.json")
	}
	
	override fun getTextureResource(obj: SpikeTrapTile?): Identifier {
		return Core.id("textures/block/spikes.png")
	}
	
	override fun getAnimationResource(obj: SpikeTrapTile?): Identifier {
		return Core.id("animations/spikes.animation.json")
	}
}
class SpikeTileRenderer : GeoBlockRenderer<SpikeTrapTile?>(SpikeBlockModel())
