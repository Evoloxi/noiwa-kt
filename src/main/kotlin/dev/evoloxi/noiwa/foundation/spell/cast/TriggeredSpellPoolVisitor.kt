package dev.evoloxi.noiwa.foundation.spell.cast

import dev.evoloxi.noiwa.foundation.spell.ISpellEnum

class TriggeredSpellPoolVisitor(pool: List<ISpellEnum>) : SpellPoolVisitor() {
    private val iterator: Iterator<ISpellEnum?>
    private var spell: ISpellEnum?

    init {
        iterator = pool.iterator()
        spell = if (iterator.hasNext()) iterator.next() else null
    }

    override fun peek(): ISpellEnum? {
        return spell
    }

    override fun pass() {
        spell = if (iterator.hasNext()) iterator.next() else null
    }

    override fun passAndConsume() {
        pass()
    }
}
