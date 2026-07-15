package com.unibo.android.ui.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun startOfDay(ms: Long): Long = Calendar.getInstance().apply {
    timeInMillis = ms
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun endOfDay(ms: Long): Long = Calendar.getInstance().apply {
    timeInMillis = ms
    set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
}.timeInMillis

fun startOfMonth(ms: Long): Long = Calendar.getInstance().apply {
    timeInMillis = ms
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun daysInMonth(ms: Long): Int = Calendar.getInstance().apply { timeInMillis = ms }
    .getActualMaximum(Calendar.DAY_OF_MONTH)

fun firstDayOfWeekInMonth(ms: Long): Int {
    val cal = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        timeInMillis = startOfMonth(ms)
    }
    return (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
}

fun isSameDay(ms1: Long, ms2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = ms1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = ms2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

fun formatTime(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(ms)

fun formatDate(ms: Long): String =
    SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(ms)

fun formatMonthYear(ms: Long): String =
    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(ms)

fun addMonths(ms: Long, amount: Int): Long = Calendar.getInstance().apply {
    timeInMillis = ms
    add(Calendar.MONTH, amount)
}.timeInMillis

fun addWeeks(ms: Long, amount: Int): Long = Calendar.getInstance().apply {
    timeInMillis = ms
    add(Calendar.WEEK_OF_YEAR, amount)
}.timeInMillis

fun addDays(ms: Long, amount: Int): Long = Calendar.getInstance().apply {
    timeInMillis = ms
    add(Calendar.DAY_OF_YEAR, amount)
}.timeInMillis

fun startOfWeek(ms: Long): Long = Calendar.getInstance().apply {
    firstDayOfWeek = Calendar.MONDAY
    timeInMillis = ms
    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun isSameWeek(ms1: Long, ms2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY; timeInMillis = ms1 }
    val c2 = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY; timeInMillis = ms2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR)
}

fun formatWeekRange(ms: Long): String {
    val start = startOfWeek(ms)
    val end = start + 6 * 24 * 3600_000L
    val fmt = SimpleDateFormat("d MMM", Locale.getDefault())
    return "${fmt.format(start)} – ${fmt.format(end)}"
}

fun dayTimestamp(monthMs: Long, dayOfMonth: Int): Long = Calendar.getInstance().apply {
    timeInMillis = monthMs
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun formatDayShort(ms: Long): String =
    SimpleDateFormat("EEE\nd", Locale.getDefault()).format(ms)

fun formatDayFull(ms: Long): String =
    SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault()).format(ms)
