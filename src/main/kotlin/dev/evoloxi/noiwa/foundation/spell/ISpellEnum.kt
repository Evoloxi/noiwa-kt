package dev.evoloxi.noiwa.foundation.spell

interface ISpellEnum {
    val spellType: SpellType?
    val manaDrain: Int
    val castDelay: Int
    val rechargeTime: Int
    val spread: Float
    val spreadModifier: Float
    val isInfinite: Boolean
        get() = uses < 0
    val uses: Int
    val castNumber: Int
	val name: String
}
