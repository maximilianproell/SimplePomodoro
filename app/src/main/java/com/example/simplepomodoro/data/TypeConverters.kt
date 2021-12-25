package com.example.simplepomodoro.data

import androidx.room.TypeConverter
import java.time.LocalDateTime

class TypeConverters {
    @TypeConverter
    fun fromStringToDate(dateString: String?): LocalDateTime =
        LocalDateTime.parse(dateString)

    @TypeConverter
    fun fromDateToString(date: LocalDateTime): String {
        return date.toString()
    }
}
