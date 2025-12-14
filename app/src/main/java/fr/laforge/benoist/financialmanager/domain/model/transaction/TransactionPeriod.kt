package fr.laforge.benoist.financialmanager.domain.model.transaction

enum class TransactionPeriod {
    None,
    Monthly,
    Weekly,
    Yearly;

    companion object {
        infix fun from(name: String): TransactionPeriod? = entries.firstOrNull { it.name == name }
    }
}
