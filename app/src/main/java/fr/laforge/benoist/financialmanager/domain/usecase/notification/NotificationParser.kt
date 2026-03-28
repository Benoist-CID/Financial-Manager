package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification

/**
 * Domain contract for parsing a raw notification into a [ParsedNotification].
 *
 * Each implementation handles one specific notification format (e.g. Google Pay,
 * proprietary bank). A factory in the infrastructure layer selects the correct
 * implementation at runtime based on the notification body shape.
 *
 * @note Implementations must be stateless and free of Android/framework imports.
 */
interface NotificationParser {

    /**
     * Returns `true` if this parser can handle the given notification body.
     *
     * @param body The raw `android.text` string from the status-bar notification.
     */
    fun canParse(body: String): Boolean

    /**
     * Parses the notification into a [ParsedNotification].
     *
     * Must only be called after [canParse] returns `true`.
     *
     * @param title The notification title (typically the app or merchant name).
     * @param body  The notification body text containing the amount.
     * @return [Result.success] with the parsed [ParsedNotification], or
     *   [Result.failure] if extraction fails despite [canParse] returning `true`.
     */
    fun parse(title: String, body: String): Result<ParsedNotification>
}
