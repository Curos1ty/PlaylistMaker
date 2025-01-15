package com.example.playlistmaker.util

import android.content.Context
import android.util.TypedValue

fun Context.toPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
)

object TimeUtils {
    fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

fun getCorrectForm(count: Int, forms: List<String>): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod100 in 11..19 -> forms[2]
        mod10 == 1 -> forms[0]
        mod10 in 2..4 -> forms[1]
        else -> forms[2]
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object NetworkError : Result<Nothing>()
}