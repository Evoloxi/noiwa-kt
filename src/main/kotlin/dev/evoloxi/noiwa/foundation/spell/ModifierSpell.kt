package dev.evoloxi.noiwa.foundation.spell

enum class ModifierSpell : ISpellEnum {
    ;

    override val spellType: SpellType?
        get() = null
    override val manaDrain: Int
        get() = 0
    override val castDelay: Int
        get() = 0
    override val rechargeTime: Int
        get() = 0
    override val spread: Float
        get() = 0F
    override val spreadModifier: Float
        get() = 0F
    override val uses: Int
        get() = 0
    override val castNumber: Int
        get() = 0
}
