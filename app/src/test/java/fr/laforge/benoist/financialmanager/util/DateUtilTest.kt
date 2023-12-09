package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.TransactionPeriod
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class DateUtilTest {
    @Test
    fun `Tests LocalDateTime toDate`() {
        val dateString = "2023-12-04T00:00:00"
        val localDateTime = LocalDateTime.parse(dateString)
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())

        val date: Date? = formatter.parse(dateString)
        date?.let {
            localDateTime.toDate().`should be equal to`(it)
        }
    }

    @Test
    fun `Tests Date toLocalDateTime`() {
        val dateString = "2023-12-04T00:00:00"
        val localDateTime = LocalDateTime.parse(dateString)
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())

        val date: Date? = formatter.parse(dateString)
        date?.toLocalDateTime()?.`should be equal to`(localDateTime)
    }

    @Test
    fun `Tests LocalDateTime next`() {
        val dateString = "2023-12-04T00:00:00"
        val localDateTime = LocalDateTime.parse(dateString)

        localDateTime.next(period = TransactionPeriod.Monthly).`should be equal to`(LocalDateTime.parse("2024-01-04T00:00:00"))
        localDateTime.next(period = TransactionPeriod.Weekly).`should be equal to`(LocalDateTime.parse("2023-12-11T00:00:00"))
        localDateTime.next(period = TransactionPeriod.Yearly).`should be equal to`(LocalDateTime.parse("2024-12-04T00:00:00"))
    }

    @Test
    fun `Tests LocalDateTime getFirstDayOfMonth`() {
        val testDate = LocalDateTime.parse("2024-02-24T00:00:00")
        testDate.getFirstDayOfMonth().`should be equal to`(LocalDateTime.parse("2024-02-01T00:00:00"))
    }

    @Test
    fun `Tests LocalDateTime getLastDayOfMonth`() {
        // Leap year
        var testDate = LocalDateTime.parse("2024-02-24T00:00:00")
        testDate.getLastDayOfMonth().`should be equal to`(LocalDateTime.parse("2024-02-29T00:00:00"))

        // Not leap year
        testDate = LocalDateTime.parse("2023-02-24T00:00:00")
        testDate.getLastDayOfMonth().`should be equal to`(LocalDateTime.parse("2023-02-28T00:00:00"))

        testDate = LocalDateTime.now()
        testDate.getLastDayOfMonth().`should be equal to`(LocalDateTime.parse("2023-12-31T00:00:00"))
    }

    @Test
    fun `Tests LocalDateTime getNumberOfRemainingDaysInMonth` () {
        val testDate = LocalDateTime.parse("2023-12-04T00:00:00")

        testDate.getNumberOfRemainingDaysInMonth().`should be equal to`(27)
    }
}
