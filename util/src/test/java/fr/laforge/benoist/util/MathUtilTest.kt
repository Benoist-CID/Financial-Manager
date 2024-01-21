package fr.laforge.benoist.util

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class MathUtilTest {
    @Test
    fun `Tests getProportion`() {
        val income = 100F
        val recurringExpenses = 20F
        val expenses = 40F
        val savings = 10F

        getProportions(income, recurringExpenses, expenses, savings).`should be equal to`(listOf(0.3F, 0.2F, 0.4F, 0.1F))
    }
}