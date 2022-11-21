package dev.evoloxi.noiwa.foundation.spell.cast

import com.google.common.collect.Lists
import dev.evoloxi.noiwa.foundation.spell.ISpellEnum
import dev.evoloxi.noiwa.foundation.spell.ModifierSpell
import dev.evoloxi.noiwa.foundation.spell.ProjectileSpell
import org.apache.commons.lang3.tuple.Pair
import org.apache.logging.log4j.LogManager

/**
 *
 * This class handles the algorithm of translating a spell list into spells to cast.
 * It only builds the first layer of a "spell tree".
 * Any further layers are roughly separated respect to first layer nodes.
 *
 *
 * We construct a "spell tree" with the following rules:
 * All the nodes are projectiles(static/non-static); all the projectiles are nodes.
 * Only projectiles with triggers and timers can have children nodes.
 * Multicasts nesting in multicasts and triggers expand the number of children, rather than forming a new layer.
 * A modifier "belong" to the projectiles "directly" after it.
 * "Belong" means the modifier merges into the owner projectile since it cannot be a node alone.
 * "Directly" means the there are no other projectiles between the modifier and its owner projectile.
 * Besides its owner projectile, a modifier also has effects on the peer nodes of its owner.
 *
 *
 * The nature of a "Formation" multicast: it is also a modifier, making the spells evenly spread in a fixed interval
 *
 *
 * If the wand runs out of mana, it will skip all spells exceed its current mana storage.
 */
object CastHelper {
    /**
     * Only separates out nodes on the first layer of spell tree, with corresponding children and modifiers.
     */
    @JvmOverloads
    fun processSpellList(visitor: SpellPoolVisitor, manaLimit: Float = Float.MAX_VALUE): CastResult {
        LogManager.getLogger().debug("Start process spell list...")
        val castResult = CastResult()
        var spellCount = 1
        while (spellCount > 0) {
            val spell = visitor.peek()
            LogManager.getLogger().debug("Loop, current spell: {}", if (spell == null) "null" else spell.name)
            if (spell == null) {
                break
            }
            if (spell.manaDrain + castResult.manaDrain > manaLimit) {
                visitor.pass()
                continue
            } else {
                visitor.passAndConsume()
            }
            castResult.manaDrain += spell.manaDrain
            castResult.castDelay += spell.castDelay
            castResult.rechargeTime += spell.rechargeTime
            if (spell !is ModifierSpell) {
                spellCount--
            }
            if (spell is ProjectileSpell) {
                val triggeredList: MutableList<ISpellEnum> = ArrayList()
                val spell2TriggeredSpells = Pair.of<ProjectileSpell, List<ISpellEnum>>(
                    spell, triggeredList
                )
                castResult.spell2TriggeredSpellList.add(spell2TriggeredSpells)
                if (spell.castNumber > 0) {
                    gatherTriggeredSpells(spell.castNumber, visitor, manaLimit, triggeredList, castResult)
                }
            } else if (spell is ModifierSpell) {
                castResult.modifiers.add(spell)
            }
        }
        return castResult
    }

    /**
     * Only gathers triggered spells. Does not do any further process.
     */
    private fun gatherTriggeredSpells(
        triggeredSpellCount: Int,
        visitor: SpellPoolVisitor,
        manaLimit: Float,
        triggeredSpellList: MutableList<ISpellEnum>,
        castResult: CastResult
    ) {
        var triggeredSpellCount = triggeredSpellCount
        LogManager.getLogger().debug("Start gather triggered spells...")
        while (triggeredSpellCount > 0) {
            val triggeredSpell = visitor.peek()
            LogManager.getLogger()
                .debug("Loop, current triggeredSpell: {}", if (triggeredSpell == null) "null" else triggeredSpell.name)
            if (triggeredSpell == null) {
                break
            }
            if (triggeredSpell.manaDrain + castResult.manaDrain > manaLimit) {
                visitor.pass()
                continue
            } else {
                visitor.passAndConsume()
            }
            triggeredSpellList.add(triggeredSpell)
            castResult.manaDrain += triggeredSpell.manaDrain
            // Only spells directly cast by the wand increases castDelay
            castResult.rechargeTime += triggeredSpell.rechargeTime
            triggeredSpellCount += triggeredSpell.castNumber
            if (triggeredSpell !is ModifierSpell) {
                triggeredSpellCount--
            }
        }
    }

    class CastResult {
        var manaDrain = 0
        var castDelay = 0
        var rechargeTime = 0
        val spell2TriggeredSpellList: MutableList<Pair<ProjectileSpell, List<ISpellEnum>>>
        val modifiers: MutableList<ModifierSpell>

        init {
            spell2TriggeredSpellList = Lists.newArrayList()
            modifiers = Lists.newArrayList()
        }

    }
}
