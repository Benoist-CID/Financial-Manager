package fr.laforge.benoist.model

enum class TransactionCategory {
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
        infix fun from(name: String): TransactionCategory? = TransactionCategory.entries.firstOrNull { it.name == name }
    }
}