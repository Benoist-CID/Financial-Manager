package fr.laforge.benoist.financialmanager.application.util

import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDateTime

class DateUtilTest {
    @Test
    fun `Tests toMilliseconds and toLocalDateTime functions`() {
        val dateTime = LocalDateTime.parse("2023-12-09T11:49:00")
        val ms = dateTime.toMilliseconds()
        ms.`should be equal to`(1702122540000)
        toLocalDateTime(ms).`should be equal to`(dateTime)
    }
}