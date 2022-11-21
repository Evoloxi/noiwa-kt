package dev.evoloxi.noiwa.foundation.util

import dev.evoloxi.noiwa.foundation.spell.ISpellEnum
import dev.evoloxi.noiwa.foundation.spell.SpellManager.getSpellByName
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.util.crash.CrashException
import net.minecraft.util.crash.CrashReport
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

object NBTHelper {
    fun spellListFromNBT(NbtList: NbtList): List<ISpellEnum> {
        val spellEnumList: MutableList<ISpellEnum> = ArrayList()
        for (i in NbtList.indices) {
            val spellName = NbtList.getString(i)
            val spellEnum = getSpellByName(spellName)
            if (spellEnum != null) {
                spellEnumList.add(spellEnum)
                LogManager.getLogger().warn("Not matching spell for name: \"$spellName\"")
            }
        }
        return spellEnumList
    }

    fun spellNBTFromList(spellEnumList: List<ISpellEnum?>): NbtList {
        val NbtList = NbtList()
        spellEnumList.stream().map<Any> { spellEnum: ISpellEnum? -> spellEnum!!.name }.forEach { spellName: Any? -> NbtList.add(NbtString.of(spellName as String)) }
	    return NbtList
    }

    fun getByte(NbtCompound: NbtCompound, key: String?): Optional<Byte> {
        try {
            if (NbtCompound.contains(key, 99)) {
                return Optional.of((NbtCompound[key] as AbstractNbtNumber?)!!.byteValue())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getShort(NbtCompound: NbtCompound, key: String?): Optional<Short> {
        try {
            if (NbtCompound.contains(key, 99)) {
                return Optional.of((NbtCompound[key] as AbstractNbtNumber?)!!.shortValue())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getInt(NbtCompound: NbtCompound, key: String?): Optional<Int> {
        try {
            if (NbtCompound.contains(key, 99)) {
                return Optional.of((NbtCompound[key] as AbstractNbtNumber?)!!.intValue())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getLong(NbtCompound: NbtCompound, key: String?): Optional<Long> {
        try {
            if (NbtCompound.contains(key, 99)) {
                return Optional.of((NbtCompound[key] as AbstractNbtNumber?)!!.longValue())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getFloat(NbtCompound: NbtCompound, key: String?): Optional<Float> {
        try {
            if (NbtCompound.contains(key, 99)) {
                return Optional.of((NbtCompound[key] as AbstractNbtNumber?)!!.floatValue())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getDouble(NbtCompound: NbtCompound, key: String?): Optional<Double> {
        try {
            if (NbtCompound.contains(key, 99)) {
                return Optional.of((NbtCompound[key] as AbstractNbtNumber?)!!.doubleValue())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getBoolean(NbtCompound: NbtCompound, key: String?): Optional<Boolean> {
        return getByte(NbtCompound, key).flatMap { aByte: Byte -> Optional.of(aByte.toInt() != 0) }
    }

    fun getUuid(NbtCompound: NbtCompound, key: String): Optional<UUID> {
        return getLong(NbtCompound, key + "Most").flatMap { aLongMost: Long? ->
            getLong(NbtCompound, key + "Least").flatMap { aLongLeast: Long? ->
                Optional.of(
                    UUID(
                        aLongMost!!, aLongLeast!!
                    )
                )
            }
        }
    }

    fun getString(NbtCompound: NbtCompound, key: String?): Optional<String> {
        try {
            if (NbtCompound.contains(key, NBTTypes.STRING.ordinal)) {
                return Optional.of(NbtCompound[key]!!.asString())
            }
        } catch (ignored: ClassCastException) {
        }
        return Optional.empty()
    }

    fun getList(NbtCompound: NbtCompound, key: String?, elementType: NBTTypes): Optional<NbtList> {
        try {
            if (NbtCompound.getType(key).toInt() == NBTTypes.LIST.ordinal) {
                val NbtList = NbtCompound[key] as NbtList?
                return if (!NbtList!!.isEmpty() && NbtList.type.toInt() != elementType.ordinal) {
                    Optional.empty()
                } else Optional.of(NbtList)
            }
        } catch (classcastexception: ClassCastException) {
            throw CrashException(CrashReport("Reading NBT data", classcastexception))
        }
        return Optional.empty()
    }

    fun getCompound(NbtCompound: NbtCompound, key: String?): Optional<NbtCompound> {
        try {
            if (NbtCompound.contains(key, NBTTypes.COMPOUND.ordinal)) {
                return Optional.of(NbtCompound[key] as NbtCompound)
            }
        } catch (classcastexception: ClassCastException) {
            throw CrashException(CrashReport("Reading NBT data", classcastexception))
        }
        return Optional.empty()
    }

    fun getCompound(itemStack: ItemStack): Optional<NbtCompound> {
        return if (itemStack.hasNbt()) Optional.of(itemStack.nbt!!) else Optional.empty()
    }

    fun makeCompound(modifier: Consumer<NbtCompound?>): NbtCompound {
        val NbtCompound = NbtCompound()
        modifier.accept(NbtCompound)
        return NbtCompound
    }

    fun makeItemWithTag(item: Item?, count: Int, NbtCompound: NbtCompound?): ItemStack {
        val itemStack = ItemStack(item, count)
        itemStack.nbt = NbtCompound
        return itemStack
    }

    private fun crashReport(NbtCompound: NbtCompound, key: String, type: NBTTypes, e: ClassCastException): CrashReport {
        val crashreport = CrashReport.create(e, "Reading NBT data")
        val crashReportSection = crashreport.addElement("Corrupt NBT tag", 1)
        //        crashreportcategory.addDetail("Tag type found", () -> NbtCompound.get(key).getType().getName().func_225648_a_());
//        crashreportcategory.addDetail("Tag type expected", type::func_225648_a_);
        crashReportSection.add("Tag name", key)
        return crashreport
    }

    enum class NBTTypes {
        END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY, LONG_ARRAY
    }
}
