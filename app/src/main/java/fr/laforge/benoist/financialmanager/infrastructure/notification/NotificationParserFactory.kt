package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationParser

/**
 * Selects the correct [NotificationParser] for a given notification body and delegates parsing.
 *
 * Parsers are evaluated in priority order; the first one whose [NotificationParser.canParse]
 * returns `true` is used. [GooglePayNotificationParser] is checked first because its format
 * (numeric amount before `€`) is more specific than the bank format.
 *
 * @property parsers Ordered list of available parsers. Defaults to the two known implementations.
 *
 * @note Injecting [parsers] as a constructor parameter makes the factory fully testable
 *   without relying on the Android context.
 */
class NotificationParserFactory(
    private val parsers: List<NotificationParser> = listOf(
        GooglePayNotificationParser(),
        BankNotificationParser(),
    )
) {
    /**
     * Returns `true` if at least one registered parser can handle [body].
     *
     * @param body The raw notification body text.
     */
    fun canParse(body: String): Boolean = parsers.any { it.canParse(body) }

    /**
     * Parses [body] using the first matching parser.
     *
     * @param title The notification title.
     * @param body  The notification body.
     * @return [Result.success] with a [ParsedNotification], or [Result.failure] if no
     *   parser matched or parsing failed.
     */
    fun parse(title: String, body: String): Result<ParsedNotification> {
        val parser = parsers.firstOrNull { it.canParse(body) }
            ?: return Result.failure(IllegalArgumentException("No parser found for: $body"))
        return parser.parse(title, body)
    }
}
