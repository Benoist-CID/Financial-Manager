package fr.laforge.benoist.financialmanager.infrastructure.repository.converters

import androidx.room.TypeConverter
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource

/**
 * Room [TypeConverter] for [List]<[NotificationSource]>.
 *
 * Serialises the list as a comma-separated string of enum names
 * (e.g. `"GOOGLE_PAY,BANK"`) for storage in a single TEXT column.
 */
class NotificationSourceConverters {

    @TypeConverter
    fun fromString(value: String?): List<NotificationSource> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").mapNotNull { name ->
            NotificationSource.entries.firstOrNull { it.name == name }
        }
    }

    @TypeConverter
    fun toCommaSeparatedString(sources: List<NotificationSource>?): String {
        return sources?.joinToString(",") { it.name } ?: ""
    }
}
