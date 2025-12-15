package com.duotech.studysmart.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val DISPLAY_FMT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a")

fun toEpochMillis(date: LocalDate, time: LocalTime): Long {
    val zdt: ZonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault())
    return zdt.toInstant().toEpochMilli()
}

fun formatEpochMillis(epochMillis: Long): String {
    val zdt = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
    return DISPLAY_FMT.format(zdt)
}
