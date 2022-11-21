package dev.evoloxi.noiwa.calculation

import java.lang.reflect.Array
import java.util.*

object Storage {
    var attackScale = 1f
    var map: MutableMap<UUID, Float> = HashMap()

    //static Map statsMap = new HashMap<>();
    var statsMap = HashMap<UUID, Array>()
    var iscrit: MutableMap<UUID, Boolean> = HashMap()
    var atktext: MutableMap<UUID, String> = HashMap()
    fun setiscrit(uuid: UUID, iscrit: Boolean) {
        Storage.iscrit[uuid] = iscrit
    }

    fun getiscrit(uuid: UUID): Boolean {
        return iscrit[uuid]!!
    }

    fun setatktext(uuid: UUID, atktext: String) {
        Storage.atktext[uuid] = atktext
    }

    fun getatktext(uuid: UUID): String? {
        return atktext[uuid]
    }

    @JvmStatic
    fun setAttackScale(uuid: UUID, attackCooldownProgress: Float) {
        map[uuid] = attackCooldownProgress
    }

    fun getStats(uuid: UUID): Array? {
        return statsMap[uuid]
    }

    @JvmStatic
    fun getAttackScale(uuid: UUID): Float {
        return map[uuid]!!
    }
}
