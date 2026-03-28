package fr.laforge.benoist.financialmanager.infrastructure.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.laforge.benoist.financialmanager.domain.model.notification.DescriptionSource
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import java.util.UUID

/**
 * Room entity representing a [NotificationFormat] in the local database.
 *
 * [id] is stored as a [String] because Room has no native UUID column type.
 * [descriptionSource] is stored as the enum name (e.g. `"BODY"` or `"TITLE"`) so that
 * new values added in future releases can be introduced without a schema migration.
 */
@Entity(tableName = "notification_format")
data class NotificationFormatEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "pattern") val pattern: String,
    @ColumnInfo(name = "description_source") val descriptionSource: String,
) {
    /**
     * Converts this entity to its domain representation.
     *
     * Unknown [descriptionSource] values fall back to [DescriptionSource.BODY] to remain
     * forwards-compatible if a newer app version wrote a value this build does not recognise.
     */
    fun toModel(): NotificationFormat = NotificationFormat(
        id = UUID.fromString(id),
        description = description,
        pattern = pattern,
        descriptionSource = runCatching { DescriptionSource.valueOf(descriptionSource) }
            .getOrDefault(DescriptionSource.BODY),
    )

    companion object {
        /** Creates a [NotificationFormatEntity] from a domain [NotificationFormat]. */
        fun fromModel(model: NotificationFormat) = NotificationFormatEntity(
            id = model.id.toString(),
            description = model.description,
            pattern = model.pattern,
            descriptionSource = model.descriptionSource.name,
        )
    }
}
