package com.example.delivery.util.convertor

import androidx.room.TypeConverter

/**
 * Pair 는 룸에 저장할 수 없다.
 * 따라서 Pair 값을 String 으로 바꿔서 저장
 */
object RoomTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toString(pair: Pair<Int, Int>) : String {
        return "${pair.first},${pair.second}"
    }

    @TypeConverter
    @JvmStatic
    fun toIntPair(str: String) : Pair<Int,Int> {
        val splitedStr = str.split(",")
        return Pair(Integer.parseInt(splitedStr[0]), Integer.parseInt(splitedStr[1]))
    }
}