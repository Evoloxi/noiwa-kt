@file:ClientOnly
package dev.evoloxi.noiwa.client.tooltip

import dev.evoloxi.noiwa.content.item.ERarity
import dev.evoloxi.noiwa.content.item.ItemWithRarity
import dev.evoloxi.noiwa.foundation.Extensions.getBase
import dev.evoloxi.noiwa.foundation.Extensions.parseRGB
import dev.evoloxi.noiwa.foundation.handler.Processor.PoN
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry.get
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qkl.wrapper.minecraft.text.TextBuilder
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback
import kotlin.math.roundToInt

object ItemTooltip {
	fun register() {
		ItemTooltipCallback.EVENT.register(ItemTooltip::tipTool)
	}
	private fun tipTool(stack: ItemStack, playerEntity: PlayerEntity?, tooltipContext: TooltipContext, lore: MutableList<Text>) {
		lore.clear()
		val group_one = ArrayList<Text>()
		val group_two = ArrayList<Text>()
		val group_three = ArrayList<Text>()
		val group_four = ArrayList<Text>()
		
		var hasQuality = true
		val eRarity = (stack.item as? ItemWithRarity)?.rarity ?: run {
			hasQuality = false
			when (stack.rarity) {
				Rarity.COMMON -> ERarity.COMMON
				Rarity.UNCOMMON -> ERarity.UNCOMMON
				Rarity.RARE -> ERarity.RARE
				else -> ERarity.EPIC
			}
		}
		//
		val color = Integer.toHexString(eRarity.color)
		
		// name, colored
		val tb = TextBuilder()
		tb.text.append(stack.name).styled { it.withColor(eRarity.color) }
		lore.add(tb.build())

		
		// attack attributes
		AttributeRegistry.DAMAGE[stack]?.let {
			group_one.add(formatAttribute("Damage", it, 0xFF5555, "\uD83D\uDDE1 \uF830"))
		}
		AttributeRegistry.STRENGTH[stack]?.let {
			group_one.add(formatAttribute(AttributeRegistry.STRENGTH.translationKey, it, 0xFF5555, "\u200C\uD83E\uDE93 \uF830"))
		}
		AttributeRegistry.CRIT_DAMAGE[stack]?.let {
			group_one.add(formatAttribute(AttributeRegistry.CRIT_DAMAGE.translationKey, it, 0xFFAA00, "\u2623 \uF830", "%"))
		}
		AttributeRegistry.CRIT_CHANCE[stack]?.let {
			group_one.add(formatAttribute(AttributeRegistry.CRIT_CHANCE.translationKey, it, 0xFFAA00, "\u2620 \uF830", "%"))
		}
		AttributeRegistry.ATTACK_SPEED[stack]?.let {
			group_one.add(formatAttribute(AttributeRegistry.ATTACK_SPEED.translationKey, (it * 100).roundToInt().toDouble(), 0xFFFFAA, "\u2694 "))
		}
		AttributeRegistry.ECHO[stack]?.let {
			group_one.add(formatAttribute(AttributeRegistry.ECHO.translationKey, it, 0xff5585, "\u200C\u03a3 "))
		}
		
		// misc attributes
		AttributeRegistry.HEALTH[stack]?.let {
			group_two.add(formatAttribute(AttributeRegistry.HEALTH.translationKey, it, 0x55FF55, "\u200C\uF830❤\uF830 "))
		}
		AttributeRegistry.DEFENSE[stack]?.let {
			group_two.add(formatAttribute(AttributeRegistry.DEFENSE.translationKey, it, 0xFFFFFF, "\u200C\uF830⛨ \uF830"))
		}
		AttributeRegistry.SPEED[stack]?.let {
			group_two.add(formatAttribute(AttributeRegistry.SPEED.translationKey, (it * 100).roundToInt().toDouble(), 0xb9ffff, "\u200C⚡\u200C \uF830"))
		}
		AttributeRegistry.LUCK[stack]?.let {
			group_two.add(formatAttribute(AttributeRegistry.LUCK.translationKey, it, 0xAAAAFF, "\u200C\uF830\u2728\ufe0f\uF830 "))
		}
		AttributeRegistry.MANA[stack]?.let {
			group_two.add(formatAttribute(AttributeRegistry.MANA.translationKey, it, 0x47fcee, "✎ "))
		}
		AttributeRegistry.COOLDOWN[stack]?.let {
			//group_two.add(Text.empty())
			group_two.add(basicFormat("<#55556a>⌚: ${(it)}s"))
		}
		// else get vanilla rarity
		if (hasQuality) {
			stack.getBase().getDouble("quality").let {
				val sb = StringBuilder()
				sb.append( "<#$color> [§f" )
				sb.append( "\uD83E\uDDEB".repeat((it / 20f).roundToInt()) )
				sb.append( "\uD83D\uDCAA".repeat(5 - (it / 20f).roundToInt()) )
				sb.append( "<#$color>] §8($it%)" )
				group_four.add(parseRGB(eRarity.plate + sb))
			}
		} else {
			group_four.add(Text.of(eRarity.plate))
		}
		// lore
		lore.addAll(group_one)
		if (group_one.isNotEmpty()) lore.add(Text.empty())
		lore.addAll(group_two)
		if (group_two.isNotEmpty()) lore.add(Text.empty())
		lore.addAll(group_three)
		if (group_three.isNotEmpty()) lore.add(Text.empty())
		lore.addAll(group_four)
	}
	
	private fun formatAttribute(attribute: String?, value: Double, color: Int, prefix: String, suffix: String?): Text {
		val builder = TextBuilder()
		builder.text.append(Text.translatable(prefix).styled { it.withColor(color) })
		if (attribute != null) builder.text.append(Text.translatable(attribute).styled { it.withColor(0xAAAAAA) })
		builder.text.append(Text.literal(": ").styled { it.withColor(0xAAAAAA) })
		builder.text.append(Text.literal(PoN(value.toInt())).styled { it.withColor(color) })
		if (suffix != null) builder.text.append(Text.translatable(suffix).styled { it.withColor(color) })
		
		return builder.build()
	}
	private fun basicFormat(format: String): Text {
		val builder = TextBuilder()
		builder.text.append(parseRGB(format))
		return builder.build()
	}
	
	private fun formatAttribute(attribute: String?, value: Double, color: Int, prefix: String): Text {
		return formatAttribute(attribute, value, color, prefix, null)
	}
}





