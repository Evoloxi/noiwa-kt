package dev.evoloxi.noiwa.content.block


import dev.evoloxi.noiwa.Core.Companion.id
import net.minecraft.util.Identifier
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer

class SpringBlockModel : AnimatedGeoModel<SpringTrapTile?>(){
	override fun getModelResource(obj: SpringTrapTile?): Identifier {
		return id("geo/spring_trap.geo.json")
	}
	
	override fun getTextureResource(obj: SpringTrapTile?): Identifier {
		return id("textures/block/spring_trap.png")
	}
	
	override fun getAnimationResource(obj: SpringTrapTile?): Identifier {
		return id("animations/spring_trap.animation.json")
	}
}

class SpringTrapTileRenderer : GeoBlockRenderer<SpringTrapTile?>(SpringBlockModel())
