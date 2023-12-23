package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.TransactionPeriod
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

fun displayDate(date: Date): String {
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    return formatter.format(date)
}

/**
 * Converts a LocalDateTime to a Date
 */
fun LocalDateTime.toDate(): Date {
    return Date(this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
}

/**
 * Converts a Date to a LocalDateTime
 */
fun Date.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
}

/**
 * Returns the next date after TransactionPeriod
 */
fun LocalDateTime.next(period: TransactionPeriod): LocalDateTime {
    return when(period) {
        TransactionPeriod.None -> this
        TransactionPeriod.Monthly -> this.plusMonths(1)
        TransactionPeriod.Weekly -> this.plusWeeks(1)
        TransactionPeriod.Yearly -> this.plusYears(1)
    }
}

/**
 * Returns first day of month
 */
fun LocalDateTime.getFirstDayOfMonth(): LocalDateTime {
    return this.withDayOfMonth(1)
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
}

/**
 * Returns last day of month
 */
fun LocalDateTime.getLastDayOfMonth(): LocalDateTime {

    return this.withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .withDayOfMonth(this.month.length(this.toLocalDate().isLeapYear))
}

/**
 * Returns the number of days remaining in month
 */
fun LocalDateTime.getNumberOfRemainingDaysInMonth(): Int {
    return this.getLastDayOfMonth().dayOfMonth - this.dayOfMonth
}

/**
 * Returns the number of days remaining in sliding month starting at startDay, until next month startDay - 1
 */
fun LocalDateTime.getNumberOfRemainingDaysInPeriod(startDay: Int): Int {
    val dayOfMonth = this.dayOfMonth

    return if ((startDay..31).contains(dayOfMonth)) {
        this.getLastDayOfMonth().dayOfMonth - this.dayOfMonth + startDay
    } else {
        startDay - dayOfMonth
    }
}
