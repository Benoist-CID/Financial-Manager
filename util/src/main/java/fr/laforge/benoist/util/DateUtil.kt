package fr.laforge.benoist.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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
 * Gets boundaries LocalDate given the startDay value (ex: startDay = 23, boundaries = yyyy-mm-23 -> yyyy-mm+1-22)
 */
fun getDateBoundaries(startDay: Int, currentDate: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate> {
    val startYear: Int
    val startMonth: Int
    val endYear: Int
    val endMonth: Int

    if(currentDate.dayOfMonth >= startDay) {
        if ((FEBRUARY ..NOVEMBER).contains(currentDate.monthValue)) {
            startMonth = currentDate.monthValue
            endMonth = currentDate.monthValue + 1
            startYear = currentDate.year
            endYear = currentDate.year
        } else if (currentDate.monthValue == JANUARY) {
            startMonth = currentDate.monthValue
            endMonth = currentDate.monthValue + 1
            startYear = currentDate.year
            endYear = currentDate.year
        } else {
            startMonth = currentDate.monthValue
            endMonth = JANUARY
            startYear = currentDate.year
            endYear = currentDate.year + 1
        }
    } else {
        if ((FEBRUARY..NOVEMBER).contains(currentDate.monthValue)) {
            startMonth = currentDate.monthValue - 1
            endMonth = currentDate.monthValue
            startYear = currentDate.year
            endYear = currentDate.year
        } else if (currentDate.monthValue == JANUARY) {
            startMonth = DECEMBER
            endMonth = currentDate.monthValue
            startYear = currentDate.year - 1
            endYear = currentDate.year
        } else {
            startMonth = currentDate.monthValue - 1
            endMonth = currentDate.monthValue
            startYear = currentDate.year
            endYear = currentDate.year
        }
    }

    val startDate = LocalDate.of(startYear, startMonth, startDay)
    val endDate = LocalDate.of(endYear, endMonth, startDay)

    return Pair(startDate, endDate)
}
