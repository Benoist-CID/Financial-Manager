package fr.laforge.benoist.financialmanager.application.util

import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class DateUtilTest {
    @Test
    fun `Tests toMilliseconds and toLocalDateTime functions`() {
        val dateTime = LocalDateTime.parse("2023-12-09T11:49:00")
        val ms = dateTime.toMilliseconds()
        ms.`should be equal to`(1702118940000)
        toLocalDateTime(ms).`should be equal to`(dateTime)
    }

    @Test
    fun `Tests getDateBoundaries`() {
        var currentDate = LocalDate.parse("2023-12-21")
        val startDay = 24

        // Regular date
        getDateBoundaries(startDay, currentDate).`should be equal to`(
            Pair(LocalDate.parse("2023-11-24"), LocalDate.parse("2023-12-23"))
        )

        // Last month date
        currentDate = LocalDate.parse("2023-12-27")
        getDateBoundaries(startDay, currentDate).`should be equal to`(
            Pair(LocalDate.parse("2023-12-24"), LocalDate.parse("2024-01-23"))
        )

        // First month date
        currentDate = LocalDate.parse("2024-01-12")
        getDateBoundaries(startDay, currentDate).`should be equal to`(
            Pair(LocalDate.parse("2023-12-24"), LocalDate.parse("2024-01-23"))
        )

        // First day of period
        currentDate = LocalDate.parse("2023-12-24")
        getDateBoundaries(startDay, currentDate).`should be equal to`(
            Pair(LocalDate.parse("2023-12-24"), LocalDate.parse("2024-01-23"))
        )
    }

    @Test
    fun `Tests getDateBoundaries with start day to 1`() {
        // --- Arrange ---
        var currentDate = LocalDate.parse("2023-01-04")
        val startDay = 2

        // --- Act ---

        // Regular date
        getDateBoundaries(startDay, currentDate).`should be equal to`(
            Pair(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-31"))
        )

        // --- Assert ---
    }
}