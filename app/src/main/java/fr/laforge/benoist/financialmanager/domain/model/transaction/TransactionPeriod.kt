package fr.laforge.benoist.financialmanager.domain.model.transaction

/**
 * Describes how often a periodic [Transaction] template recurs.
 *
 * Only meaningful when [Transaction.isPeriodic] is `true`. For one-off or child
 * transactions the value should be [None].
 */
enum class TransactionPeriod {
    /** No recurrence — used for manual entries and child instances. */
    None,

    /** Recurs once per calendar month. */
    Monthly,

    /** Recurs once per calendar week (every 7 days). */
    Weekly,

    /** Recurs once per calendar year. */
    Yearly;

    companion object {
        /**
         * Returns the [TransactionPeriod] whose [name] matches [name], or `null` if not found.
         *
         * @param name The string representation of the enum constant (case-sensitive).
         * @return The matching [TransactionPeriod], or `null` if [name] does not match any constant.
         */
        infix fun from(name: String): TransactionPeriod? = entries.firstOrNull { it.name == name }
    }
}
