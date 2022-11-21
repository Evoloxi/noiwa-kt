package dev.evoloxi.noiwa.foundation.spell.cast

import dev.evoloxi.noiwa.foundation.spell.ISpellEnum

abstract class SpellPoolVisitor {
    /**
     * @return the first spell in the pool,
     * or null if the pool is now empty
     */
    abstract fun peek(): ISpellEnum?

    /**
     * deque the first spell in the pool
     */
    abstract fun pass()

    /**
     * similar to pass(), but may consume a non-infinite spell
     */
    abstract fun passAndConsume()
}
