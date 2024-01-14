package fr.laforge.benoist.model

enum class TransactionType {
    Income,
    Expense;

    companion object {
        infix fun from(name: String): TransactionType? = entries.firstOrNull { it.name == name }
    }
}
