package fr.laforge.benoist.model

enum class TransactionPeriod {
    None,
    Monthly,
    Weekly,
    Yearly;

    companion object {
        infix fun from(name: String): TransactionPeriod? = entries.firstOrNull { it.name == name }
    }
}
