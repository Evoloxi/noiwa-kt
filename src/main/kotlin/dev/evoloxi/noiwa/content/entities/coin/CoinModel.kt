package dev.evoloxi.noiwa.content.entities.coin

import net.minecraft.util.Identifier
import software.bernie.geckolib3.model.AnimatedGeoModel

class CoinModel : AnimatedGeoModel<CoinEntity?>() {
    override fun getModelResource(obj: CoinEntity?): Identifier? {
            return CoinRenderer.GEO_GEN[obj?.variant]!!
    }

    override fun getTextureResource(obj: CoinEntity?): Identifier? {
            return CoinRenderer.TEXTURE_GEN[obj?.variant]!!
    }

    override fun getAnimationResource(animatable: CoinEntity?): Identifier? {
        return null
    }
}
