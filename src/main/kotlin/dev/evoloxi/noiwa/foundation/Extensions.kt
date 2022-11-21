@file:Suppress("unused")

package dev.evoloxi.noiwa.foundation

import dev.evoloxi.noiwa.foundation.component.CustomComponents
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qkl.wrapper.minecraft.text.TextBuilder
import org.quiltmc.qsl.networking.api.ServerPlayNetworking
import java.awt.Color
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Extensions {
	
	private val formatDecimalCache = HashMap<String, String>()
	private const val phi = 2.39996322973
	
	inline fun client(world: World, function: () -> Unit) {
		if (world.isClient) function()
	}
	fun Int.toVec3f(): Vec3f {
		val r = (this shr 16 and 0xFF) / 255.0f
		val g = (this shr 8 and 0xFF) / 255.0f
		val b = (this and 0xFF) / 255.0f
		return Vec3f(r, g, b)
	}
	
	fun Vec3f.toInt(): Int {
		val r = (x * 255).toInt()
		val g = (y * 255).toInt()
		val b = (z * 255).toInt()
		return r shl 16 or (g shl 8) or b
	}
	
	fun fibonacciSphere(samples: Int, r: Double): ArrayList<Vec3d> {
		val points = ArrayList<Vec3d>()
		//val m = bui1
		for (i in 0 .. samples) {
			val y = 1 - (i / (samples - 1.0)) * 2
			val radius = sqrt(1 - y * y)
			val theta = phi * i
			val x = cos(theta) * radius
			val z = sin(theta) * radius
			points.add(Vec3d(x, y, z).multiply(r))
		}
		return points
	}
	// scheduling function as a shortcut for coroutines
	inline fun schedule(delay: Long, crossinline function: () -> Unit) {
		CoroutineScope(Dispatchers.Default).launch {
			delay(delay)
			function()
		}
	}
	// create a function like client(World, () -> Unit) but that does not require a world parameter
	
	inline fun server(world: World, function: () -> Unit) {
		if (!world.isClient) {
			function()
		}
	}
	
	inline fun common(function: () -> Unit) {
		function()
	}
	
	fun ByteArray.remove(b: Byte) {
		for (i in this.indices) {
			if (this[i] == b) {
				this[i] = 0
			}
		}
	}
	fun IntArray.remove(i: Int) {
		this.filter { it != i }
	}
	val LivingEntity.isGhost get() = this.getComponent(CustomComponents.STATS).isGhost
	
	fun Vec3d.reflect(normal: Vec3d): Vec3d {
		// get the reflection of the vector
		val projection: Double = this.dotProduct(normal)
		return this.add(normal.multiply(-2 * projection))
	}
	
	fun String.toTitleCase(): String {
		return this.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
	}
	
//	fun String.formatDecimal(): String {
//		// format the plate (add commas) e.g. 1000000 -> 1,000,000
//		var string = this
//		val decimalFormat = DecimalFormat("#,###")
//		decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
//		string = decimalFormat.format(string.toDouble())
//		return string
//	}
//
//	fun String.formatDecimalFast(): String {
//		if (formatDecimalCache.containsKey(this)) return formatDecimalCache[this]!!
//		val result = this.formatDecimal()
//		formatDecimalCache[this] = result
//		return result
//	}
	
	fun broadcastPacket(
		buf: PacketByteBuf,
		id: Identifier,
		world: World,
		origin: Vec3d,
		radius: Int
	) {
		if (world is ServerWorld) {
			for (player in world.players) {
				if (radius <= 0 || player.pos.distanceTo(origin) < radius) {
					ServerPlayNetworking.send(
						player as ServerPlayerEntity,
						id,
						buf
					)
				}
			}
		}
	}
	
	fun ItemStack.getBase(): NbtCompound {
		return this.getOrCreateSubNbt("Base")
	}
	
	private val symbols = mapOf(
		"1" to "\uF811",
		"2" to "\uF812",
		"3" to "\uF813",
		"4" to "\uF814",
		"5" to "\uF815",
		"6" to "\uF816",
		"7" to "\uF817",
		"8" to "\uF818",
		"9" to "\uF819",
		"0" to "\uF81A",
		"K" to "\uF81B",
		"M" to "\uF81C",
		"B" to "\uF81D",
		"T" to "\uF81E",
	)
	
	fun unicode(input: String): String {
		var output = ""
		for (c in input) {
			output += symbols[c.toString()] ?: c
		}
		return output
	}
	
	// function to shorten a number eg 1000 -> 1k, 1200 -> 1.2k, 1000000 -> 1m, 1200000 -> 1.2m, etc
	
	private var fmt: NumberFormat = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT)
	
	fun Long.shortenNumber(): String {
		fmt.maximumFractionDigits = 1
		return fmt.format(this)
	}

	
	// this regex looks for a hex color code and continues until another one is found, if none is found it will continue until the end of the plate
	private val hexColorCodePattern = Pattern.compile("<#[0-9a-fk-or]{6}>")
	
	fun parseRGB(string: String): MutableText {
		val matcher = hexColorCodePattern.matcher(string)
		val temp = TextBuilder()
		val color: MutableList<String> = ArrayList()
		
		while (matcher.find()) color.add(matcher.group())
		
		if (color.size > 0) {
			
			val split = string.split(hexColorCodePattern.toRegex()).toTypedArray()
			val split2 = split.copyOfRange(1, split.size)
			if (split[0].isNotEmpty()) temp.text.append(split[0])
			
			for ((i, s) in split2.withIndex()) {
				val decoded = Color.decode(color[i].replace("[<>]+".toRegex(), "")).rgb and 0xFFFFFF
				temp.text.append(Text.literal(s).styled { it.withColor(decoded) })
			}
			
		} else { temp.text.append(string) }
		
		temp.text.fillStyle(Style.EMPTY.withItalic(false))
		return temp.build() as MutableText
	}
	
	fun PlayerEntity.notify(message: String) {
		val formatted = parseRGB("§f[§6!§f] $message")
		this.sendMessage(formatted, false)
	}
	
	fun scheduleWithTimer(delay: Long, task: () -> Unit) {
		Timer().schedule(object : TimerTask() {
			override fun run() {
				task()
			}
		}, delay)
	}

    fun Vec3d.squareXZ(): Float {
		return this.x.toFloat() * this.x.toFloat() + this.z.toFloat() * this.z.toFloat()
    }

    val LivingEntity.stat: Stats
		get() = Stats(this)
	
}

class Stats(entity: LivingEntity) {
	val echo by lazy { entity.getAttributeInstance(AttributeRegistry.ECHO)?.value ?: 0.0 }
	val damage by lazy { entity.getAttributeInstance(AttributeRegistry.DAMAGE)?.value ?: 0.0 }
	val strength by lazy { entity.getAttributeInstance(AttributeRegistry.STRENGTH)?.value ?: 0.0 }
	val critChance by lazy { entity.getAttributeInstance(AttributeRegistry.CRIT_CHANCE)?.value ?: 0.0 }
	val critDamage by lazy { entity.getAttributeInstance(AttributeRegistry.CRIT_DAMAGE)?.value ?: 0.0 }
	val defense by lazy { entity.getAttributeInstance(AttributeRegistry.DEFENSE)?.value ?: 0.0 }
	val health by lazy { entity.getComponent(CustomComponents.STATS).health }
	val mana by lazy { entity.getComponent(CustomComponents.STATS).mana }
	val maxHealth by lazy { entity.getAttributeInstance(AttributeRegistry.HEALTH)?.value ?: 0.0 }
	val maxMana by lazy { entity.getAttributeInstance(AttributeRegistry.MANA)?.value ?: 0.0 }
	val speed by lazy { entity.getAttributeInstance(AttributeRegistry.SPEED)?.value ?: 0.0 }
	val attackSpeed by lazy { entity.getAttributeInstance(AttributeRegistry.ATTACK_SPEED)?.value ?: 0.0 }
	val luck by lazy { entity.getAttributeInstance(AttributeRegistry.LUCK)?.value ?: 0.0 }
	
}
