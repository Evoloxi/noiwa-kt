package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.CombatHandler.hurtV
import dev.evoloxi.noiwa.foundation.DamageType
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult

class FnSInfinite(settings: Settings?) : FnSRegular(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val cd = context.stack.nbt?.getCompound("Base")?.getFloat("cooldown")?.times(20)?.toInt() ?: 0
        usageExecutor(context, false, 3,  Core.RAND.nextInt(18, 23), cd, 100, 180)

        //context.player!!.getComponent(CustomComponents.STATS).health -= 100f
        context.player!!.hurtV(100.0, damageType = DamageType.MAGIC_CRIT)
        return ActionResult.SUCCESS
    }

}


