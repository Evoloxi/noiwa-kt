package dev.evoloxi.noiwa.foundation.handler

import dev.evoloxi.noiwa.foundation.DamageType
import dev.evoloxi.noiwa.foundation.Extensions.parseRGB
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.awt.Color
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

object Processor {
    fun wrapAround(text: ArrayList<String>, length: Int): ArrayList<String> {
        // group entries of text together into entries, up to length and separated by spaces and a comma
        val entries = ArrayList<String>()
        var entry = StringBuilder()
        for (s in text) {
            if (entry.length + s.length + 2 > length) {
                entries.add(entry.toString())
                entry = StringBuilder()
            }
            if (text.indexOf(s) == text.size - 1) {
                entry.append(s)
            } else {
                entry.append(s).append(", ")
            }
        }
        if (entry.isNotEmpty()) {
            entries.add(entry.toString())
        }
        return entries
    }

    fun PoN(i: Int): String {
	    val x = i.toString()
        return if (i > 0) "+$x" else x
    }

    private val roman = TreeMap<Int, String>().apply {
		put(1000, "M")
		put(900, "CM")
		put(500, "D")
		put(400, "CD")
		put(100, "C")
		put(90, "XC")
		put(50, "L")
		put(40, "XL")
		put(10, "X")
		put(9, "IX")
		put(5, "V")
		put(4, "IV")
		put(1, "I")
	    put(0, "ZERO")
    }

    fun toRoman(number: Int): String? {
	    val l = roman.floorKey(number)
        return if (number == l) {
            roman[number]
        } else roman[l] + toRoman(
            number - l
        )
    }
	
	// create a map conatining the keys 0-9, with a unique unicode symbol for each
	
	var formatter = DecimalFormat("#,###")

    fun crit(
        text: String,
        damageType: DamageType
    ): Text {
	    // format the number with formatter and color every third letter red, the next gold, the next yellow, the next white also ignore commas
	    val split = text.split("")
	    var result = ""
	    val colors = damageType.format
	    var i = 0
	    if (colors.isEmpty()) return parseRGB(damageType.prefix + text + damageType.suffix)
	
	    for (s in split) {
		    if (s == "," || s == ".") {
			    result += s
			    continue
		    }
		    // prevent division by 0
		    result += colors[i % colors.size] + s
		    i++
	    }
	    return parseRGB(damageType.prefix + result + damageType.suffix)
    }
}
