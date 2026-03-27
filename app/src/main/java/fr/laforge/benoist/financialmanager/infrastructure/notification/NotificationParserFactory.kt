package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationParser

/**
 * Selects the correct [NotificationParser] for a given notification body and delegates parsing.
 *
 * ## Parser priority
 * 1. Built-in parsers ([builtInParsers]) are tried first, in the order they are supplied.
 *    [GooglePayNotificationParser] precedes [BankNotificationParser] because its detection
 *    criterion (numeric text before `€`) is more specific.
 * 2. User-defined parsers ([userDefinedFormats]) are tried afterwards, in insertion order.
 *
 * Keeping user parsers as a separate list (rather than merging into [builtInParsers]) makes
 * it straightforward to update them at runtime without replacing the whole factory.
 *
 * @property builtInParsers  Ordered list of hard-coded parsers. Defaults to Google Pay + Bank.
 * @property userDefinedFormats  User-created [NotificationFormat] entries converted to parsers on demand.
 */
class NotificationParserFactory(
    private val builtInParsers: List<NotificationParser> = listOf(
        GooglePayNotificationParser(),
        BankNotificationParser(),
    ),
    private val userDefinedFormats: List<NotificationFormat> = emptyList(),
) {
    /**
     * All parsers evaluated in priority order: built-ins first, then user-defined.
     */
    private val allParsers: List<NotificationParser>
        get() = builtInParsers + userDefinedFormats.map { UserDefinedNotificationParser(it) }

    /**
     * Returns `true` if at least one registered parser can handle [body].
     *
     * @param body The raw notification body text.
     */
    fun canParse(body: String): Boolean = allParsers.any { it.canParse(body) }

    /**
     * Parses [body] using the first matching parser (built-ins before user-defined).
     *
     * @param title The notification title.
     * @param body  The notification body.
     * @return [Result.success] with a [ParsedNotification], or [Result.failure] if no
     *   parser matched or parsing failed.
     */
    fun parse(title: String, body: String): Result<ParsedNotification> {
        val parser = allParsers.firstOrNull { it.canParse(body) }
            ?: return Result.failure(IllegalArgumentException("No parser found for: $body"))
        return parser.parse(title, body)
    }
}
