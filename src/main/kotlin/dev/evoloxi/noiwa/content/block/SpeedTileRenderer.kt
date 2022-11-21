package dev.evoloxi.noiwa.content.block


import dev.evoloxi.noiwa.Core
import net.minecraft.util.Identifier
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.processor.AnimationProcessor
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer

class SpeedPlateBlockModel : AnimatedGeoModel<SpeedPlateTile>(){
	override fun getModelResource(obj: SpeedPlateTile): Identifier {
		return Core.id("geo/dungeon/speed_plate.geo.json")
	}
	
	override fun getTextureResource(obj: SpeedPlateTile): Identifier {
		return Core.id("textures/block/dungeon/speed_plate.png")
	}
	
	override fun getAnimationResource(obj: SpeedPlateTile): Identifier {
		return Core.id("animations/dungeon/speed_plate.animation.json")
	}
}
class SpeedPlateTileRenderer : GeoBlockRenderer<SpeedPlateTile>(SpeedPlateBlockModel()) {
	val animationProcessor: AnimationProcessor<IAnimatable> = SpeedPlateBlockModel().animationProcessor
}
