package fr.laforge.benoist.model

enum class TransactionPeriod {
    None,
    Monthly,
    Weekly,
    Yearly;

    companion object {
        infix fun from(name: String): TransactionPeriod? = TransactionPeriod.values().firstOrNull { it.name == name }
    }
}
