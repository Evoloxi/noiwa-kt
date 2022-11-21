package dev.evoloxi.noiwa.foundation.handler

import dev.evoloxi.noiwa.foundation.Extensions.parseRGB
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text
import java.util.regex.Pattern
import kotlin.collections.ArrayList

object NbtUpdater {
	
	fun updateNBT(nbt: NbtCompound, player: PlayerEntity?) {
		val base = nbt.getCompound("Base")
		nbt.put("Base", base)
		nbt.putInt("HideFlags", -1)

		LoreHandler.c = base.getString("color")
		if (LoreHandler.c.isEmpty()) {
			LoreHandler.c = "§a"
		}
		
		var q = base.getFloat("quality")
		if (q == 0f) {
			q = 1f
		}
		base.put("display", NbtCompound())
		var name = base.getString("name")
		if (name.isEmpty()) {
			name = "§fGeneric Item"
		}
		val nbtList = NbtList()
		val damage = base.getInt("damage")
		val strength = base.getInt("strength")
		val critChance = base.getInt("critChance")
		val critDamage = base.getInt("critDamage")
		val echo = base.getInt("echo")
		val mana = base.getInt("mana")
		val health = base.getInt("health")
		val defense = base.getInt("defense")
		val speed = base.getInt("speed")
		val luck = base.getInt("luck")
		
		////////////////
		val lore = ArrayList<String?>()
		val ammo = base.getInt("ammo")
		val maxAmmo = base.getInt("maxAmmo")
		if (ammo > maxAmmo) base.putInt("ammo", maxAmmo)
		
		if (damage != 0) lore.add("§c\uD83D\uDDE1 \uF830§7Damage: §c" + Processor.PoN(damage))
		if (strength != 0) lore.add("§c\u200C\uD83E\uDE93 \uF830§7Strength: §c" + Processor.PoN(strength))
		if (critChance != 0) lore.add("§6☣ \uF830§7Crit Chance: §6" + Processor.PoN(critChance) + "%")
		if (critDamage != 0) lore.add("§6☠ \uF830§7Crit Damage: §6" + Processor.PoN(critDamage) + "%")
		// echo (#ff5585)
		if (echo != 0) lore.add("<#04c29e>\u200CΣ §7Echo: <#04c29e>" + Processor.PoN(echo))
		// if any of intelligence, health, baseSpeed, luck are not 0, add a blank line and any of baseDamage, strength, crit chance, crit baseDamage
		if ((defense != 0 || mana != 0 || health != 0 || speed != 0 || luck != 0) && (damage != 0 || strength != 0 || critChance != 0 || critDamage != 0 || echo != 0)) lore.add(
			" "
		)
		
		
		if (mana != 0) lore.add("<#47fcee>☄\uF830 §7Mana: <#47fcee>" + Processor.PoN(mana))
		if ((defense != 0 || health != 0 || speed != 0 || luck != 0) && (mana != 0) && (damage != 0 || strength != 0 || critChance != 0 || critDamage != 0 || echo != 0)) lore.add(
			" "
		)
		
		
		if (health != 0) lore.add("§a‌\uF830❤\uF830 §7Health: §a" + Processor.PoN(health))
		if (defense != 0) lore.add("§f‌\uF830⛨ \uF830§7Defense: §f" + Processor.PoN(defense))
		if (speed != 0) lore.add("<#fcf532>‌⚡‌ \uF830§7Speed: <#fcf532>" + Processor.PoN(speed))
		if (luck != 0) lore.add("<#a1fc32>\u272E \uF830§7Luck: <#a1fc32>" + Processor.PoN(luck))
		if ((defense != 0 || health != 0 || speed != 0 || luck != 0 || damage != 0 || mana != 0 || strength != 0 || critChance != 0 || critDamage != 0 || echo != 0)) lore.add(
			" "
		)
		
		///////////////// Enchants /////////////////
		var enchants = base.getList("Enchantments", 10)
		if (enchants.isEmpty()) {
			enchants = NbtList()
		} else {
			val fakelist = NbtList()
			val fake = NbtCompound()
			fake.put("id", NbtString.of("fake"))
			fake.put("lvl", NbtString.of("1"))
			fakelist.add(fake)
			nbt.put("Enchantments", fakelist)
		}
		
		///////////////// Description /////////////////
		var description = base.getList("description", 8)
		if (description.isEmpty()) {
			description = NbtList()
		}
		LoreHandler.processEnchants(enchants, lore, base)
		LoreHandler.processDescription(description, lore)
		LoreHandler.processAmmo(ammo, maxAmmo, lore)
		if (maxAmmo != 0) {
			lore.add(" ")
		}
		LoreHandler.processQuality(q, lore, base)
		
		//////////////// LORE ////////////////
		var displaynbt: NbtCompound = nbt.getCompound("display")
		for (s in lore) {
			if (s != null) {
				//val parsed = pParse(base, s)
				val hex = parseRGB(s)
				nbtList.add(NbtString.of(Text.Serializer.toJson(hex)))
			}
		}
		
		//nbtList.add(NbtString.of(str))
		//////////////// NAME ////////////////
		if (displaynbt.isEmpty) {
			displaynbt = NbtCompound()
		}
		displaynbt.put("Lore", nbtList)
		val display = parseRGB(LoreHandler.c + name)
		//remove outermost brackets
		//val name1 = display.toString().substring(1, display.toString().length - 1)
		player!!.mainHandStack.setCustomName(display)
		//player.sendMessage(Text.of(display.copy().toString()), false)
		
	}
	
	private fun pParse(base: NbtCompound, line: String): String {
		val pVint = Pattern.compile("INT;%.+%")
		val pVfloat = Pattern.compile("FLOAT;%.+%")
		val pVstring = Pattern.compile("STRING;%.+%")
		val pVany = Pattern.compile("INT;%.+%|FLOAT;%.+%|STRING;%.+%")
		val pVanyX = "INT;|FLOAT;|STRING;"
		val value: MutableList<String> = ArrayList()
		val final: ArrayList<String> = ArrayList()
		val matcher = pVint.matcher(line) ?: pVfloat.matcher(line) ?: pVstring.matcher(line) ?: return line
		while (matcher.find()) {
			value.add(matcher.group())
		}
		return if (value.size > 0) {
			val split = line.split(pVany).toTypedArray()
			val split2 = split.copyOfRange(1, split.size)
			for ((i, s) in split2.withIndex()) {
				val key = value[i].replace(pVanyX.toRegex(), "").replace("%", "")
				final.add(s + base.getFloat(key).toString())
			}
			final.joinToString("")
		} else {
			line
		}
	}
}
