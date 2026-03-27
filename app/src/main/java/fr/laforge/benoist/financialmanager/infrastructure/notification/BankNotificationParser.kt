package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationParser

/**
 * Parses proprietary bank (Samsung Wallet) notifications.
 *
 * ## Format
 * - **Title**: not used for parsing
 * - **Body**: `"{description}€{amount}"` — description is **before** `€`,
 *   amount is **after** `€`, comma as decimal separator
 *   (e.g. `"Starbucks€12,50"`)
 *
 * Detection: the substring before `€` (trimmed) is non-numeric text.
 */
class BankNotificationParser : NotificationParser {

    override fun canParse(body: String): Boolean {
        if (!body.contains(EURO_SYMBOL)) return false
        val beforeEuro = body.substringBefore(EURO_SYMBOL).trim()
        return beforeEuro.replace(',', '.').toFloatOrNull() == null
    }

    override fun parse(title: String, body: String): Result<ParsedNotification> {
        val parts = body.split(EURO_SYMBOL)
        if (parts.size < 2) {
            return Result.failure(IllegalArgumentException("Missing amount in: $body"))
        }

        val description = parts[0].trim()
        val amountStr = parts[1].trim().replace(',', '.')
        val amount = amountStr.toFloatOrNull()
            ?: return Result.failure(IllegalArgumentException("Cannot parse amount from: $body"))

        return Result.success(
            ParsedNotification(
                amount = amount,
                description = description,
                source = NotificationSource.BANK,
            )
        )
    }

    companion object {
        private const val EURO_SYMBOL = '€'
    }
}
