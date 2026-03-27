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
 * 2. User-defined parsers, obtained by calling [userFormatsProvider] on every invocation,
 *    are tried afterwards in insertion order.
 *
 * Using a **provider lambda** rather than a static list ensures that formats added or deleted
 * by the user in Settings are picked up by the next incoming notification without restarting
 * the app or rebuilding the factory singleton.
 *
 * @property builtInParsers     Ordered list of hard-coded parsers. Defaults to Google Pay + Bank.
 * @property userFormatsProvider Lambda that returns the current list of [NotificationFormat]
 *   entries each time [canParse] or [parse] is called. Defaults to `{ emptyList() }`.
 */
class NotificationParserFactory(
    private val builtInParsers: List<NotificationParser> = listOf(
        GooglePayNotificationParser(),
        BankNotificationParser(),
    ),
    private val userFormatsProvider: () -> List<NotificationFormat> = { emptyList() },
) {
    /**
     * All parsers evaluated in priority order: built-ins first, then user-defined.
     * Re-evaluated on every call so that newly added user formats are included immediately.
     */
    private val allParsers: List<NotificationParser>
        get() = builtInParsers + userFormatsProvider().map { UserDefinedNotificationParser(it) }

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
