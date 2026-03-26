package fr.laforge.benoist.financialmanager.domain.model.transaction

/**
 * Budget category tag for a [Transaction].
 *
 * Categories allow users to understand where their money is going and
 * enable category-level filtering and reporting. [None] is the default
 * for uncategorised transactions.
 */
enum class TransactionCategory {
    /** No specific category assigned. */
    None,
    Food,
    Bank,
    EducationAndFamily,
    Saving,
    Taxes,
    Juridic,
    Accommodation,
    Leisure,
    Income,
    Health,
    Shopping,
    Transport,
    Sport,
    Vehicle,
    Telecom,
    Pet;

    companion object {
        /**
         * Returns the [TransactionCategory] whose [name] matches [name], or `null` if not found.
         *
         * @param name The string representation of the enum constant (case-sensitive).
         * @return The matching [TransactionCategory], or `null` if [name] does not match any constant.
         */
        infix fun from(name: String): TransactionCategory? =
            TransactionCategory.entries.firstOrNull { it.name == name }
    }
}
