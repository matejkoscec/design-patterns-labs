package design.patterns.vector.graphics.editor.graphics

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int) : Comparable<Point> {

    override fun compareTo(other: Point): Int {
        val compare = x.compareTo(other.x)
        return if (compare != 0) compare else y.compareTo(other.y)
    }

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
}

infix fun Point.distanceTo(other: Point): Double =
    sqrt((x - other.x).toDouble().pow(2) + (y - other.y).toDouble().pow(2))

fun Point.distanceToLine(start: Point, end: Point): Double {
    val lineLengthSquared = (end.x - start.x).toDouble().pow(2) + (end.y - start.y).toDouble().pow(2)

    if (lineLengthSquared == 0.0) return this distanceTo start

    val t = ((this.x - start.x) * (end.x - start.x) + (this.y - start.y) * (end.y - start.y)) / lineLengthSquared

    return if (t < 0.0) this distanceTo start
    else if (t > 1.0) this distanceTo end
    else {
        val projection = Point((start.x + t * (end.x - start.x)).toInt(), (start.y + t * (end.y - start.y)).toInt())
        this distanceTo projection
    }
}

operator fun Point.rangeTo(other: Point): Rectangle {
    val xLeft = min(x, other.x)
    val xRight = max(x, other.x)
    val yLeft = min(y, other.y)
    val yRight = max(y, other.y)

    return Rectangle(xLeft, yLeft, xRight - xLeft, yRight - yLeft)
}
