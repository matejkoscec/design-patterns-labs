package design.patterns.gui

import kotlin.math.max
import kotlin.math.min

data class Location(var row: Int, var col: Int) : Comparable<Location> {

    fun moveLeft(min: Int): Int {
        col = max(min, --col)
        return col
    }

    fun moveRight(max: Int): Int {
        col = min(max, ++col)
        return col
    }

    fun moveUp(min: Int): Int {
        row = max(min, --row)
        return row
    }

    fun moveDown(max: Int): Int {
        row = min(max, ++row)
        return row
    }

    override fun compareTo(other: Location): Int = when {
        row < other.row -> -1
        row > other.row -> 1
        else -> when {
            col < other.col -> -1
            col > other.col -> 1
            else -> 0
        }
    }
}

data class LocationRange(var from: Location, var to: Location) {
    fun isEmpty(): Boolean = from == to
    fun isNotEmpty(): Boolean = !isEmpty()
}
