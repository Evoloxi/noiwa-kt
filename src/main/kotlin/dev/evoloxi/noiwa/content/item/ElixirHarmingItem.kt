package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.content.entities.ElixirEffectTypes
import net.minecraft.util.math.Vec3f

class ElixirHarmingItem(settings: Settings) : ElixirItem(settings) {
	
	override val effectType: ElixirEffectTypes = ElixirEffectTypes.HARMING
	override val duration: Int = 200
	override val amplifier: Int = 1
	override val radius: Double = 6.0
	override val startColor: Vec3f =  Vec3f(0.7f, 0.0f, 0.2f)
	override val endColor: Vec3f = Vec3f(0.2f, 0.0f, 0.2f)
	
	
}
