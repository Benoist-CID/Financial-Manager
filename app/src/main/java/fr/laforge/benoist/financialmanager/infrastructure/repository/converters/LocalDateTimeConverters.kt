package fr.laforge.benoist.financialmanager.infrastructure.repository.converters

import androidx.room.TypeConverter
import fr.laforge.benoist.financialmanager.domain.util.toLocalDateTime
import fr.laforge.benoist.financialmanager.domain.util.toMilliseconds
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
