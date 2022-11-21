package dev.evoloxi.noiwa.foundation.handler

import dev.evoloxi.noiwa.calculation.Stuff
import dev.evoloxi.noiwa.foundation.handler.NbtUpdater.updateNBT
import dev.evoloxi.noiwa.foundation.Extensions.getBase
import com.mojang.brigadier.context.CommandContext
import dev.evoloxi.noiwa.foundation.registry.EnumRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper.clamp
import org.quiltmc.qsl.command.api.EnumArgumentType
import java.util.*
import kotlin.math.roundToInt

object LoreHandler {
    var c = "§a"
    val x1 = "‌<#6b6b6b>■"
    val x2 = "‌<#363636>□"

    fun addLore(ctx: CommandContext<ServerCommandSource>, type: String) {
        val stat = EnumArgumentType.getEnumConstant(ctx, "stat", EnumRegistry.Stats::class.java).string
        val source = ctx.source
        val player: PlayerEntity = source.player
	    val stack = player.mainHandStack
	    val base = stack.getBase()
        if (stack.isEmpty) {
            source.sendError(Text.of("§f🧻 You must be holding an item in your main hand to use this command."))
        } else {
            if ("float" == type) {
				val amount = ctx.getArgument("value", Float::class.java)
	            if (amount == 0f) {
					base.remove(stat)
	            } else {
		            base.putFloat(stat, amount)
	            }
            }
            updateNBT(stack.orCreateNbt, player)
        }
    }


    fun processDescription(description: NbtList, lore: ArrayList<String?>) {
        if (!description.isEmpty()) {
            for (element in description) {
                lore.add(element.asString())
            }
            lore.add(" ")
        }
    }
    fun processEnchants(enchants: NbtList, lore: ArrayList<String?>, base: NbtCompound) {
        val enchantsLore = ArrayList<String>()
        if (!enchants.isEmpty()) {
            for (element in enchants) {
                val enchant = element as NbtCompound
                var name = enchant.getString("id")
                name = name.substring(0, 1).uppercase(Locale.getDefault()) + name.substring(1)
                val roman = Processor.toRoman(enchant.getInt("lvl"))
                enchantsLore.add("<#aaaaea>$name §f$roman§f")
            }
            if (enchantsLore.size > 1) enchantsLore.sort()
            val wrapped = Processor.wrapAround(enchantsLore, (base.getString("name").length + 30))
            wrapped.forEach { s: String? -> lore.add("  $s") }
            lore.add(" ")
        }
    }


    fun processQuality(q: Float, lore: ArrayList<String?>, base: NbtCompound) {
        val quality: String = if (q == -1f) "" else c + " [§f" +
                "\uD83E\uDDEB".repeat((q / 20f).roundToInt()) +
                "\uD83D\uDCAA".repeat(5 - (q / 20f).roundToInt()) +
                c + "]" + " §8(" + q + "%)"
        var rarity = base.getString("rarity")
        if (rarity.isEmpty()) rarity = "ꐝ"
        lore.add("<#ffffff>$rarity$c$quality")
        //println("Quality: $rarity$c$quality")
    }
    fun processAmmo(ammo: Int, maxAmmo: Int, lore: ArrayList<String?>) {
        // progress bar similar to quality
        // add X for every ammo and - for every missing ammo until 10, then scale with percentage
        val ammoLore = if (maxAmmo < 10 ) "<#555555>‌‌☵ [" +
                x1.repeat(ammo) +
                x2.repeat(maxAmmo - clamp(ammo, 0, maxAmmo)) + "<#555555>‌]"
        else "<#555555>‌‌☵ [" +
                x1.repeat(clamp((ammo / (maxAmmo / 10f)).roundToInt(), 0, 10)) +
                x2.repeat(clamp(10 - (ammo / (maxAmmo / 10f)).roundToInt(), 0, 10)) + "<#555555>‌]"

        if (maxAmmo > 0) lore.add("<#ffffff>$ammoLore")
    }
    fun addEnchant(ctx: CommandContext<ServerCommandSource>) {
        val enchant = ctx.getArgument("enchant", String::class.java)
        val level = ctx.getArgument("level", Int::class.java)
        val source = ctx.source
        val player: PlayerEntity? = source.player
        val nbt = source.player!!.mainHandStack.orCreateNbt
        val base = nbt.getCompound("Base")
        if (source.player!!.mainHandStack.isEmpty) {
            source.sendError(Text.of("§f🧻 You must be holding an item in your main hand to use this command."))
        } else {
            var enchants = base.getList("Enchantments", 10)
            if (enchants.isEmpty()) {
                enchants = NbtList()
            }
            val enchantment = NbtCompound()
            enchantment.putString("id", enchant)

            // if the enchantment id is already in the list, set the level to 1 or if the level is 0, remove the enchantment
            enchants.removeIf { element: NbtElement -> element.asString().contains(enchantment.getString("id")) }
            if (level == 0) {
                source.sendError(Text.of("§f📎 §7Removed enchantment: §f$enchant"))
                enchants.removeIf { element: NbtElement -> element.asString().contains(enchantment.getString("id")) }
                updateNBT(nbt, player)
                return
            }
            enchantment.putInt("lvl", level)
            enchants.add(enchantment)
            base.put("Enchantments", enchants)
            source.sendFeedback(Text.of("§f📎 §7Added enchantment: §f$enchant"), false)
            updateNBT(nbt, player)
        }
    }
	enum class loreMode {
		ADD, REPLACE
	}
    fun addDescription(ctx: CommandContext<ServerCommandSource>, mode: Enum<loreMode>) {
        val text = ctx.getArgument("text", String::class.java)
        val line = ctx.getArgument("line", Int::class.java)
        val source = ctx.source
        val player: PlayerEntity = source.player!!
        val stack = player.mainHandStack
        val base = stack.getBase()
        val description = base.getList("description", 8)
        player.sendMessage(Text.of("§8$description"), false)
        if (source.player!!.mainHandStack.isEmpty) {
            source.sendError(Text.of("§f🧻 You must be holding an item in your main hand to use this command."))
        } else if (text == "clear") {
            description.removeAt(line - 1)
        } else {
            // fill in empty lines
            for (i in 0 until line) {
                if (description.size <= line - 1) {
                    description.add(NbtString.of(" "))
                }
            }
	        if (mode == loreMode.REPLACE) {
		        description[line - 1] = NbtString.of("§8$text§f")
	        } else if (mode == loreMode.ADD){
				description.add(NbtString.of("§8$text§f"))
	        }
            base.put("description", description)
        }
        // if text is empty, remove line
        updateNBT(stack.nbt!!, player)
    }

    fun setRarity(ctx: CommandContext<ServerCommandSource>, rarity: String?) {
        val source = ctx.source
        val player: PlayerEntity = source.player!!
	    val base = player.mainHandStack.getBase()
	    val nbt = player.mainHandStack.nbt!!
	    if (source.player!!.mainHandStack.isEmpty) {
            source.sendError(Text.of("§f🧻 You must be holding an item in your main hand to use this command."))
        } else {
            when (rarity) {
                "COMMON" -> {
                    base.putString("rarity", "ꐝ")
                    base.putString("color", "<#eeeeee>")
                }

                "UNCOMMON" -> {
                    base.putString("rarity", "ꐓ")
                    base.putString("color", "<#95dc26>")
                }

                "RARE" -> {
                    base.putString("rarity", "ꐕ")
                    base.putString("color", "<#7575ff>")
                }

                "EPIC" -> {
                    base.putString("rarity", "ꐔ")
                    base.putString("color", "<#b13deb>")
                }

                "LEGENDARY" -> {
                    base.putString("rarity", "ꐑ")
                    base.putString("color", "<#ffaa00>")
                }

                "EXOTIC" -> {
                    base.putString("rarity", "ꐙ")
                    base.putString("color", "<#ff55ff>")
                }

                "SPECIAL" -> {
                    base.putString("rarity", "ꐖ")
                    base.putString("color", "§c")
                }

                else -> source.sendError(Text.of("§f🧻 Invalid rarity."))
            }
        }
        updateNBT(nbt, player)
    }

    fun setDisplayName(ctx: CommandContext<ServerCommandSource>) {
        val source = ctx.source
        val player: PlayerEntity = source.player!!
        val nbt = player.mainHandStack.orCreateNbt
        if (source.player!!.mainHandStack.isEmpty) {
            source.sendError(Text.of("§f🧻 You must be holding an item in your main hand to use this command."))
        } else {
            val base = nbt.getCompound("Base")
            base.putString("name", ctx.getArgument("name", String::class.java))
        }
        updateNBT(nbt, player)
    }

    fun setRarity(ctx: CommandContext<ServerCommandSource>) {
        val source = ctx.source
        val player: PlayerEntity = source.player!!
        val nbt = player.mainHandStack.orCreateNbt
        if (player.mainHandStack.isEmpty) {
            source.sendError(Text.of("§f🧻 You must be holding an item in your main hand to use this command."))
        } else {
            val base = nbt.getCompound("Base")
            base.putFloat("quality", ctx.getArgument("value", Float::class.java))
            player.sendMessage(Text.of("§f📎 §7Set quality to §f${ctx.getArgument("value", Float::class.java)}"), false)
        }
        updateNBT(nbt, player)
    }

    fun setType(ctx: CommandContext<ServerCommandSource>) {
        val player = ctx.source.player
        val nbt = player!!.mainHandStack.orCreateNbt
        val base = player.mainHandStack.getBase()
        if (player.mainHandStack.isEmpty) {
            Stuff.error(player, "§f🧻 You must be holding an item in your main hand to use this command.")
        } else {
            base.putString("type", ctx.getArgument("type", String::class.java))
            player.sendMessage(Text.of("§f📎 §7Set type to §f${ctx.getArgument("type", String::class.java)}"), false)
        }
        updateNBT(nbt, player)
    }


}

