package dev.evoloxi.noiwa.foundation.spell

interface IModifier {
    fun onCast()
    fun onTick()
    fun onHit()
    fun onDamage()
}
