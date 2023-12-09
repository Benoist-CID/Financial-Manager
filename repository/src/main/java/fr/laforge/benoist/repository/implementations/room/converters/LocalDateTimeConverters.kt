package fr.laforge.benoist.repository.implementations.room.converters

import androidx.room.TypeConverter
import fr.laforge.benoist.util.toLocalDateTime
import fr.laforge.benoist.util.toMilliseconds
import java.time.LocalDateTime

class LocalDateTimeConverters {
    @TypeConverter
    fun fromLong(value: Long?): LocalDateTime? {
        return value?.let { toLocalDateTime(value) }
    }

    @TypeConverter
    fun toLong(value: LocalDateTime?): Long? {
        return value?.toMilliseconds()
    }
}
