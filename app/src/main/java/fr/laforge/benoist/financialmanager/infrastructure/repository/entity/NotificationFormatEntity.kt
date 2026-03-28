package fr.laforge.benoist.financialmanager.infrastructure.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import java.util.UUID

/**
 * Room entity representing a [NotificationFormat] in the local database.
 *
 * [id] is stored as a [String] because Room has no native UUID column type.
 */
@Entity(tableName = "notification_format")
data class NotificationFormatEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "pattern") val pattern: String,
) {
    /** Converts this entity to its domain representation. */
    fun toModel(): NotificationFormat = NotificationFormat(
        id = UUID.fromString(id),
        description = description,
        pattern = pattern,
    )

    companion object {
        /** Creates a [NotificationFormatEntity] from a domain [NotificationFormat]. */
        fun fromModel(model: NotificationFormat) = NotificationFormatEntity(
            id = model.id.toString(),
            description = model.description,
            pattern = model.pattern,
        )
    }
}
