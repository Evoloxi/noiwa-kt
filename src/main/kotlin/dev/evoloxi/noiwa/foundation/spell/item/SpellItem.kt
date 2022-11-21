package dev.evoloxi.noiwa.foundation.spell.item

import dev.evoloxi.noiwa.foundation.spell.ISpellEnum
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

class SpellItem(settings: Settings, val spell: ISpellEnum) : BaseItem(
    settings.maxCount(1).group(ItemGroup.COMBAT).maxDamage(
        spell.uses
    )
) {

    override fun appendTooltip(stack: ItemStack, worldIn: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        tooltip.add(Text.translatable(translationKey + ".detail"))
        tooltip.add(Text.translatable("desc.noitaWands.spell.mana_drain", spell.manaDrain))
        tooltip.add(Text.translatable("desc.noitaWands.spell.cast_delay", spell.castDelay / 20.0))
        tooltip.add(Text.translatable("desc.noitaWands.spell.recharge_time", spell.rechargeTime / 20.0))
        //        if (spell instanceof StaticSpell && ((StaticSpell) spell).uses > -1) {
//            tooltip.add(Text.translatable("desc.noitaWands.spell.uses_remain", stack.getDamage()));
//        }
    }
}
