package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.Core.Companion.id
import net.minecraft.util.Identifier
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer

class LauncherRenderer : GeoItemRenderer<LauncherItem>(LauncherModel()) {
	override fun getGeoModelProvider(): LauncherModel {
		return LauncherModel()
	}
}

class LauncherModel : AnimatedGeoModel<LauncherItem>() {

    override fun getModelResource(`object`: LauncherItem): Identifier {
        return id("geo/launcher.geo.json")
    }

    override fun getTextureResource(`object`: LauncherItem): Identifier {
        return id("textures/item/launcher.png")
    }

    override fun getAnimationResource(animatable: LauncherItem): Identifier {
        return id("animations/launcher.animation.json")
    }

}
