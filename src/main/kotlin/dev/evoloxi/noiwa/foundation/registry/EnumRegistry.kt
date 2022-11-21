package dev.evoloxi.noiwa.foundation.registry

import net.minecraft.entity.attribute.EntityAttribute

object EnumRegistry {
	// validStats to enum
	enum class Stats(var string: String, val attribute: EntityAttribute, val scale: Float) {
		DAMAGE("damage", AttributeRegistry.DAMAGE, 1f),
		STRENGTH("strength", AttributeRegistry.STRENGTH, 1f),
		CRITCHANCE("critChance", AttributeRegistry.CRIT_CHANCE, 1f),
		CRITDAMAGE("critDamage", AttributeRegistry.CRIT_DAMAGE, 1f),
		ECHO("echo", AttributeRegistry.ECHO, 1f),
		ATTACKSPEED("attackSpeed", AttributeRegistry.ATTACK_SPEED, 0.01f),
		MANA("mana", AttributeRegistry.MANA, 1f),
		HEALTH("health", AttributeRegistry.HEALTH, 1f),
		DEFENSE("defense", AttributeRegistry.DEFENSE, 1f),
		SPEED("speed", AttributeRegistry.SPEED, 0.01f),
		LUCK("luck", AttributeRegistry.LUCK, 1f),
		//AMMO("ammo", AttributeRegistry.AMMO, 1f),
		MAXAMMO("maxAmmo", AttributeRegistry.MAX_AMMO, 1f),
		COOLDOWN("cooldown", AttributeRegistry.COOLDOWN, 1f)
	}
	
	
	enum class Type(val string: String) {
		SWORD("sword"),
		BOW("bow"),
		STAFF("staff"),
		GUN("gun"),
		HELMET("helmet"),
		CHESTPLATE("chestplate"),
		LEGGINGS("leggings"),
		BOOTS("boots"),
		AXE("axe"),
		PICKAXE("pickaxe"),
		SHOVEL("shovel"),
		HOE("hoe"),
		FISHING_ROD("fishing_rod"),
		TRIDENT("trident"),
		TALISMAN("talisman"),
		RING("ring"),
		NECKLACE("necklace"),
		BRACELET("bracelet"),
		AMULET("amulet"),
		BELT("belt"),
		EARRING("earring"),
		GLOVES("gloves"),
		PENDANT("pendant"),
		SHIELD("shield"),
		BAG("bag"),
		BOOK("book"),
		SCROLL("scroll"),
		TRAP("trap"),
	}
	
	enum class Rarity {
		COMMON,
		UNCOMMON,
		RARE,
		EPIC,
		LEGENDARY,
		EXOTIC,
		SPECIAL
	}

	fun isStatValid(stat: String): Boolean {
		for (validStat in Stats.values()) {
			if (validStat.string == stat) {
				return true
			}
		}
		return false
	}
	// make isStatValid faster
	fun isStatValidFast(stat: String): Boolean {
		return Stats.values().any { it.string == stat }
	}
}
