package dev.evoloxi.noiwa.content.entities.coin

import java.util.*

enum class CoinVariant(val id: Int) {
    SMALL_0(0),
    SMALL_1(1),
    SMALL_2(2),
    NORMAL_0(3),
    NORMAL_1(4),
    NORMAL_2(5),
    LARGE_0(6),
    LARGE_1(7),
    LARGE_2(8),
    LARGE_3(9),
    LARGE_4(10);

    companion object {
        val BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt { obj: CoinVariant -> obj.id })
            .toArray<CoinVariant> { size: Int -> arrayOfNulls(size) } // FIXME -> MIGHT BREAK IN FUTURE

        //
        val VALUES = values()

        val randomVariant: CoinVariant
            get() = BY_ID[(Math.random() * BY_ID.size).toInt()]

        fun byId(id: Int): CoinVariant {
            return BY_ID[id % BY_ID.size]
        }
    }
}
