package dev.evoloxi.noiwa.foundation.spell

import com.google.common.collect.Maps

object SpellManager {
    internal val SPELL_MAP: Map<String, ISpellEnum> = Maps.newHashMap()
    @JvmStatic
    fun getSpellByName(name: String): ISpellEnum? {
        return SPELL_MAP[name]
    }
}
