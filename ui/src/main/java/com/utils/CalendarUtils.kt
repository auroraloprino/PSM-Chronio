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

fun firstDayOfWeekInMonth(ms: Long): Int = Calendar.getInstance().apply {
    timeInMillis = startOfMonth(ms)
}.get(Calendar.DAY_OF_WEEK)

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

fun dayTimestamp(monthMs: Long, dayOfMonth: Int): Long = Calendar.getInstance().apply {
    timeInMillis = monthMs
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis
