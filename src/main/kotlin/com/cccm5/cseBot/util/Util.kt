package com.cccm5.cseBot.util

import java.util.*

internal infix fun Int.ceilDivide(divisor: Int): Int {
    return (this + divisor - 1) / divisor
}
/**
 * @param [range] a range of numbers to create a random number between
 */
internal fun Random.nextInt(range: IntRange): Int {
    return range.start + nextInt(range.last - range.start)
}