package dev.evoloxi.noiwa.content.entities.rockProjectile

import dev.evoloxi.noiwa.Core.Companion.id
import dev.evoloxi.noiwa.content.entities.coin.CoinEntity
import net.minecraft.util.Identifier
import software.bernie.geckolib3.geo.render.built.GeoModel
import software.bernie.geckolib3.model.AnimatedGeoModel

class RockModel : AnimatedGeoModel<RockEntity?>() {
    override fun getModelResource(`object`: RockEntity?): Identifier {
        return id("geo/rock.geo.json")
    }

    override fun getTextureResource(`object`: RockEntity?): Identifier {
        return id("textures/entity/rock.png")
    }

    override fun getAnimationResource(animatable: RockEntity?): Identifier? {
        return id("animations/rock.animation.json")
    }

}
