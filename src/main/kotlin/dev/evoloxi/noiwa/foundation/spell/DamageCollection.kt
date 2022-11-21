package dev.evoloxi.noiwa.foundation.spell

import dev.evoloxi.noiwa.foundation.spell.entity.SpellEntityBase
import net.minecraft.entity.Entity

/**
 * Different types of damage source
 * Impact: projectile impact damage
 */
class DamageCollection(
    private val damageImpact: Float,
    private val damageExplosion: Float,
    private val radius: Float,
    private val damageSlice: Float,
    private val damageElectricity: Float
) {
    fun causeDamage(spellEntity: SpellEntityBase?, targetEntity: Entity?) {}
}
