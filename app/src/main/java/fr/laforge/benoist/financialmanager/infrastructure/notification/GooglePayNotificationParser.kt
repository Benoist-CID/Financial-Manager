package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationParser

/**
 * Parses Google Pay / Google Wallet notifications.
 *
 * ## Format
 * - **Title**: merchant name (e.g. `"DUMMY VENDOR"`)
 * - **Body**: `"{amount} € {any text}"` — amount is **before** `€`, comma as decimal separator
 *   (e.g. `"10,00 € dummy string"`)
 *
 * Detection: the substring before `€` (trimmed) is numeric.
 */
class GooglePayNotificationParser : NotificationParser {

    override fun canParse(body: String): Boolean {
        val beforeEuro = body.substringBefore(EURO_SYMBOL).trim()
        return beforeEuro.replace(',', '.').toFloatOrNull() != null
    }

    override fun parse(title: String, body: String): Result<ParsedNotification> {
        val amountStr = body.substringBefore(EURO_SYMBOL).trim().replace(',', '.')
        val amount = amountStr.toFloatOrNull()
            ?: return Result.failure(IllegalArgumentException("Cannot parse amount from: $body"))

        if (amount < 0) return Result.failure(IllegalArgumentException("Negative amount: $amount"))

        return Result.success(
            ParsedNotification(
                amount = amount,
                description = title,
                source = NotificationSource.GOOGLE_PAY,
            )
        )
    }

    companion object {
        private const val EURO_SYMBOL = '€'
    }
}
