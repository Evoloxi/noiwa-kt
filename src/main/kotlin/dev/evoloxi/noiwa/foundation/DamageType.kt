package dev.evoloxi.noiwa.foundation

open class DamageType constructor(
	val name: String, val prefix: String, val suffix: String, val format: Array<String>
) {
	constructor(name: String, prefix: String, suffix: String) : this(name, prefix, suffix, arrayOf())

    var isScaled = false
	private var bypassesArmor = false
	private var bypassesInvulnerability = false
	private var bypassesMagic = false
	var isCritical = false
	private var isExplosive = false
	private var isFire = false
	private var isProjectile = false
	private var scalesWithDifficulty = false
	private var isUnblockable = false
	private var isOutOfWorld = false
	var isMagic = false
	private var isCreativePlayer = false
	private var isSweep = false
	var doKnockback = false
	
	companion object {
		val MELEE_CRIT = DamageType("melee_crit", "§f", "§f", arrayOf("§c", "§6", "§e", "§f")).isCritical().withScale().doKnockback()
		val MELEE = DamageType("melee_no_crit", "§f", "").withScale().doKnockback()
		
		val RANGED_CRIT = DamageType("ranged_crit", "§f", "§f", arrayOf("§c", "§6", "§e", "§f")).isCritical().doKnockback()
		val RANGED = DamageType("ranged_no_crit", "§7", "").doKnockback()
		
		val MAGIC_CRIT = DamageType(
			"magic_crit", "§f", "§f",
			arrayOf(
				"<#f7faea>",
				"<#ffd2de>",
				"<#f792e4>",
				"<#de39e9>",
				"<#8623ae>"
			)
		).isCritical().isMagic().doKnockback()
		val MAGIC = DamageType("magic_no_crit", "<#e88eed>", "").isMagic()
		
		val TRAP_CRIT = DamageType(
			"trap_crit", "<#eaac8b>", "<#eaac8b>", arrayOf(
				"<#eaac8b>",
				"<#e56b6f>",
				"<#b56576>",
				"<#6d597a>",
				"<#355070>"
			)
		).isCritical()
		val TRAP = DamageType("trap_no_crit", "§7", "")
		
		val RUNIC_CRIT = DamageType(
			"runic_crit", "§f", "§f", arrayOf(
				"<#ffdc5e>",
				"<#ffbf81>",
				"<#ffa3a5>",
				"<#ff86c8>",
				"<#ff69eb>"
			)
		).isCritical()
		val RUNIC = DamageType("runic_no_crit", "<#ff69eb>", "")
		
		val VOID_CRIT = DamageType("void_crit", "§f", "§f", arrayOf(
			"<#265c42>",
			"<#ff4763>",
			"<#e92681>",
			"<#c8129e>",
			"<#971585>"
		)).isCritical()
		
		val ECHO_CRIT = DamageType("echo_crit", "§f", "§f", arrayOf(
			"<#265c42>",
			"<#3e8948>",
			"<#63c74d>",
			"<#3e8948>",
			"<#265c42>"
		)).isCritical()
		
		val ECHO = DamageType("echo_no_crit", "<#265c42>", "")
	}
	
	// associate every damage type with a byte
	// this is used to send the damage type to the client
	// and to save it to the nbt
	

	
	
	open fun bypassesArmor(): DamageType {
		this.bypassesArmor = true
		return this
	}
	
	open fun withScale(): DamageType {
		this.isScaled = true
		return this
	}
	
	open fun bypassesInvulnerability(): DamageType {
		this.bypassesInvulnerability = true
		return this
	}
	
	open fun isMagic(): DamageType {
		this.isMagic = true
		return this
	}
	
	open fun isCritical(): DamageType {
		this.isCritical = true
		return this
	}
	
	open fun doKnockback(): DamageType {
		this.doKnockback = true
		return this
	}
}
fun DamageType.getCritVariant() : DamageType {
	return when (this) {
		DamageType.MELEE -> DamageType.MELEE_CRIT
		DamageType.RANGED -> DamageType.RANGED_CRIT
		DamageType.MAGIC -> DamageType.MAGIC_CRIT
		DamageType.TRAP -> DamageType.TRAP_CRIT
		DamageType.RUNIC -> DamageType.RUNIC_CRIT
		DamageType.VOID_CRIT -> DamageType.VOID_CRIT
		else -> this
	}
}

fun DamageType.toByte(): Byte {
	return when (this) {
		DamageType.MELEE_CRIT -> 0
		DamageType.MELEE -> 1
		DamageType.RANGED_CRIT -> 2
		DamageType.RANGED -> 3
		DamageType.MAGIC_CRIT -> 4
		DamageType.MAGIC -> 5
		DamageType.TRAP_CRIT -> 6
		DamageType.TRAP -> 7
		DamageType.RUNIC_CRIT -> 8
		DamageType.RUNIC -> 9
		DamageType.VOID_CRIT -> 10
		DamageType.ECHO_CRIT -> 11
		DamageType.ECHO -> 12
		else -> 0
	}
}

fun Byte.toDamageType(): DamageType {
	return when (this) {
		0.toByte() -> DamageType.MELEE_CRIT
		1.toByte() -> DamageType.MELEE
		2.toByte() -> DamageType.RANGED_CRIT
		3.toByte() -> DamageType.RANGED
		4.toByte() -> DamageType.MAGIC_CRIT
		5.toByte() -> DamageType.MAGIC
		6.toByte() -> DamageType.TRAP_CRIT
		7.toByte() -> DamageType.TRAP
		8.toByte() -> DamageType.RUNIC_CRIT
		9.toByte() -> DamageType.RUNIC
		10.toByte() -> DamageType.VOID_CRIT
		11.toByte() -> DamageType.ECHO_CRIT
		12.toByte() -> DamageType.ECHO
		else -> DamageType.MELEE_CRIT
	}
}
