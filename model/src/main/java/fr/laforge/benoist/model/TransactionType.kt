package fr.laforge.benoist.model

enum class TransactionType {
    Income,
    Expense;

    companion object {
        infix fun from(name: String): TransactionType? = TransactionType.values().firstOrNull { it.name == name }
    }
}
