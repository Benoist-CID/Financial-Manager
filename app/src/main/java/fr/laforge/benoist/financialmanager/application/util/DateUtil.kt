package fr.laforge.benoist.financialmanager.application.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Converts a LocalDateTime to milliseconds
 */
fun LocalDateTime.toMilliseconds(): Long {
    return this.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
}

/**
 * Converts milliseconds to LocalDateTime
 */
fun toLocalDateTime(milliseconds: Long): LocalDateTime {
    return Instant.ofEpochMilli(milliseconds).atZone(ZoneOffset.UTC).toLocalDateTime()
}
