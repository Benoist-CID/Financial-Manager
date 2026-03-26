package fr.laforge.benoist.financialmanager.domain.model.transaction

/**
 * Classifies a [Transaction] as either money received or money spent.
 *
 * Income transactions increase the running balance; Expense transactions decrease it.
 */
enum class TransactionType {
    /** Money received (salary, bonus, gift, etc.). */
    Income,

    /** Money spent (rent, groceries, subscription, etc.). */
    Expense;

    companion object {
        /**
         * Returns the [TransactionType] whose [name] matches [name], or `null` if not found.
         *
         * @param name The string representation of the enum constant (case-sensitive).
         * @return The matching [TransactionType], or `null` if [name] does not match any constant.
         */
        infix fun from(name: String): TransactionType? = entries.firstOrNull { it.name == name }
    }
}
