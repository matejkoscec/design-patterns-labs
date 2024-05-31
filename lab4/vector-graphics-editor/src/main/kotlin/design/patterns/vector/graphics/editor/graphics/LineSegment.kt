package design.patterns.vector.graphics.editor.graphics

import design.patterns.vector.graphics.editor.Renderer
import design.patterns.vector.graphics.editor.push
import kotlin.math.abs

class LineSegment(start: Point, end: Point) : AbstractGraphicalObject(arrayOf(start, end)) {

    private val start: Point get() = getHotPoint(0)
    private val end: Point get() = getHotPoint(1)

    constructor() : this(Point(0, 0), Point(40, 40))

    override val boundingBox: Rectangle
        get() = Rectangle(
            x = if (start.x <= end.x) start.x else end.x,
            y = if (start.y <= end.y) start.y else end.y,
            width = abs(start.x - end.x),
            height = abs(start.y - end.y)
        )

    override fun selectionDistance(mousePoint: Point): Double = mousePoint.distanceToLine(start, end)

    override val shapeName: String = "Linija"

    override fun duplicate(): GraphicalObject = LineSegment(start, end)

    override fun render(r: Renderer) {
        r.drawLine(start, end)
    }

    override val shapeID: String = "@LINE"

    override fun save(rows: MutableList<String>) {
        rows.add("$shapeID ${start.x} ${start.y} ${end.x} ${end.y}")
    }

    override fun load(stack: ArrayDeque<GraphicalObject>, data: String) {
        val (x1, y1, x2, y2) = data.removePrefix("$shapeID ").split(' ').map { it.toInt() }
        stack.push(LineSegment(Point(x1, y1), Point(x2, y2)))
    }

    override fun toString(): String = "LineSegment(start=$start, end=$end)"
}
