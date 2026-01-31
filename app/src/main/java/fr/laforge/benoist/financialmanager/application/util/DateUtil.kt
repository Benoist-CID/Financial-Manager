package fr.laforge.benoist.financialmanager.application.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

/**
 * Converts a LocalDateTime to milliseconds
 */
fun LocalDateTime.toMilliseconds(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

/**
 * Converts milliseconds to LocalDateTime
 */
fun toLocalDateTime(milliseconds: Long): LocalDateTime {
    return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

const val JANUARY = 1
const val FEBRUARY = 2
const val NOVEMBER = 11
const val DECEMBER = 12

/**
 * Gets boundaries LocalDate given the startDay value.
 * Handles end-of-month edge cases (e.g. startDay=31 in February -> Feb 28).
 */
fun getDateBoundaries(startDay: Int, currentDate: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate> {
    val currentYearMonth = YearMonth.from(currentDate)

    // 1. Determine if we are technically in the "current" cycle or the "previous" one.
    // If startDay is 31, but this month only has 30 days, we clamp to 30 for the comparison.
    val maxDayThisMonth = currentYearMonth.lengthOfMonth()
    val cycleTriggerDay = startDay.coerceAtMost(maxDayThisMonth)

    val startYearMonth = if (currentDate.dayOfMonth >= cycleTriggerDay) {
        // We passed the start day, so the cycle started this month
        currentYearMonth
    } else {
        // We haven't reached the start day yet, so the cycle started last month
        currentYearMonth.minusMonths(1)
    }

    val endYearMonth = startYearMonth.plusMonths(1)

    // 2. Calculate the actual dates.
    // We clamp the requested startDay to the valid number of days in that specific month.
    // This prevents "Feb 30" crashes.
    val startDate = startYearMonth.atDay(startDay.coerceAtMost(startYearMonth.lengthOfMonth()))
    val endDate = endYearMonth.atDay(startDay.coerceAtMost(endYearMonth.lengthOfMonth()))

    return Pair(startDate, endDate)
}
