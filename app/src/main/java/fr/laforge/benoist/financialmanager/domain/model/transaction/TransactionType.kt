package fr.laforge.benoist.financialmanager.domain.model.transaction

enum class TransactionType {
    Income,
    Expense;

    companion object {
        infix fun from(name: String): TransactionType? = entries.firstOrNull { it.name == name }
    }
}
