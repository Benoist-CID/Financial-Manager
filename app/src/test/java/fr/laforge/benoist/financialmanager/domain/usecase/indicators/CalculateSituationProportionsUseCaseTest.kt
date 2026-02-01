package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class CalculateSituationProportionsUseCaseTest {

    private val useCase = CalculateSituationProportionsUseCase()

    @Test
    fun `invoke should return normalized proportions when total is positive`() {
        val result = useCase(
            income = 50f,
            recurringExpenses = 25f,
            regularExpenses = 25f,
            savingsTarget = 0f
        )

        // Total = 100. Income is 50/100 = 0.5, etc.
        result `should be equal to` listOf(0.5f, 0.25f, 0.25f, 0.0f)
    }

    @Test
    fun `invoke should return zeros when total is zero to avoid NaN`() {
        val result = useCase(0f, 0f, 0f, 0f)

        result `should be equal to` listOf(0f, 0f, 0f, 0f)
    }
}
