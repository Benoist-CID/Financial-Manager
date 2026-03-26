package fr.laforge.benoist.financialmanager.domain.util

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBeTrue
import org.junit.Test

class MathUtilTest {

    @Test
    fun `getProportions returns correct proportions for typical budget`() {
        val income = 100F
        val recurringExpenses = 20F
        val expenses = 40F
        val savings = 10F

        getProportions(income, recurringExpenses, expenses, savings)
            .`should be equal to`(listOf(0.3F, 0.2F, 0.4F, 0.1F))
    }

    @Test
    fun `getProportions returns all-zero remaining when expenses equal income`() {
        // Income is fully consumed by recurring + variable + savings
        val income = 100F
        val recurringExpenses = 50F
        val expenses = 30F
        val savings = 20F

        val result = getProportions(income, recurringExpenses, expenses, savings)

        result[0].`should be equal to`(0f)   // remaining balance proportion
        result[1].`should be equal to`(0.5f) // recurring
        result[2].`should be equal to`(0.3f) // variable expenses
        result[3].`should be equal to`(0.2f) // savings
    }

    @Test
    fun `getProportions returns negative first element when spending exceeds income`() {
        // Expenses and savings exceed income → first bucket (remaining) goes negative
        val income = 100F
        val recurringExpenses = 60F
        val expenses = 50F
        val savings = 10F

        val result = getProportions(income, recurringExpenses, expenses, savings)

        (result[0] < 0f).shouldBeTrue()  // remaining proportion is negative
        result[1].`should be equal to`(0.6f)
        result[2].`should be equal to`(0.5f)
        result[3].`should be equal to`(0.1f)
    }

    @Test
    fun `getProportions returns NaN values when income is zero`() {
        // Division by income = 0 → Float division produces NaN for non-zero numerators
        // and NaN for 0/0 as well; this documents current behaviour.
        val result = getProportions(
            income = 0F,
            recurringExpenses = 0F,
            expenses = 0F,
            savings = 0F
        )

        result.forEach { value ->
            value.isNaN().shouldBeTrue()
        }
    }
}
