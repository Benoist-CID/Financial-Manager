package fr.laforge.benoist.financialmanager.domain.model.indicator

data class LifestyleState(
    val ratio: Float = 0f,
    val status: LifestyleStatus = LifestyleStatus.Healthy
)

