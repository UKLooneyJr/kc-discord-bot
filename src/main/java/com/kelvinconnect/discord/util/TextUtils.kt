package com.kelvinconnect.discord.util

import kotlin.math.absoluteValue

fun Int.asOrdinal(): String {
    val abs = absoluteValue
    return when (abs % 100) {
        11, 12, 13 -> "${this}th"
        else -> when (abs % 10) {
            1 -> "${this}st"
            2 -> "${this}nd"
            3 -> "${this}rd"
            else -> "${this}th"
        }
    }
}