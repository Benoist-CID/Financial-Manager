package fr.laforge.benoist.util

import java.time.Instant
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
