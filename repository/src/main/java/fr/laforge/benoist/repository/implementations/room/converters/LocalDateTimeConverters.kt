package fr.laforge.benoist.repository.implementations.room.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverters {
    @TypeConverter
    fun fromString(value: String?): LocalDateTime? {
        return value?.let {LocalDateTime.parse(it)}
    }

    @TypeConverter
    fun toString(value: LocalDateTime?): String? {
        return value?.toString()
    }
}
