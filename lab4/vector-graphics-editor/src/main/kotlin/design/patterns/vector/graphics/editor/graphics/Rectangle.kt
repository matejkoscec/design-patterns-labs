package design.patterns.vector.graphics.editor.graphics

data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int)

operator fun Rectangle.contains(point: Point): Boolean {
    return point.x >= x && point.x <= x + width &&
            point.y >= y && point.y <= y + height
}

fun Rectangle.points() = listOf(
    Point(x, y),
    Point(x + width, y),
    Point(x + width, y + height),
    Point(x, y + height),
)
