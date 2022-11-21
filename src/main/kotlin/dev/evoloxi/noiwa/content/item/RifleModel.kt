package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.Core.Companion.id
import net.minecraft.util.Identifier
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer

class RifleRenderer : GeoItemRenderer<RifleItem>(RifleModel()) {
	override fun getGeoModelProvider(): RifleModel {
		return RifleModel()
	}
}

class RifleModel : AnimatedGeoModel<RifleItem>() {

    override fun getModelResource(item: RifleItem) = id("geo/rifle.geo.json")

    override fun getTextureResource(item: RifleItem) = id("textures/item/rifle.png")
    

    override fun getAnimationResource(item: RifleItem) = id("animations/rifle.animation.json")

}
