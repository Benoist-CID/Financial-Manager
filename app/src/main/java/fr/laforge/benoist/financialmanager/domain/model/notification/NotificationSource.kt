package fr.laforge.benoist.financialmanager.domain.model.notification

/**
 * Identifies the origin system of a financial notification.
 *
 * Each value maps to a known notification format that the infrastructure
 * layer knows how to parse.
 */
enum class NotificationSource {
    /** Google Pay / Google Wallet: amount before €, merchant name in title. */
    GOOGLE_PAY,

    /** Proprietary bank app: description before €, amount after €. */
    BANK,

    /** User-defined format stored in [NotificationFormatRepository]. */
    CUSTOM,
}
