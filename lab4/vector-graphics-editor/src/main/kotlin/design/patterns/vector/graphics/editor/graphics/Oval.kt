package design.patterns.vector.graphics.editor.graphics

import design.patterns.vector.graphics.editor.Renderer
import design.patterns.vector.graphics.editor.push
import kotlin.math.cos
import kotlin.math.sin

class Oval(bottom: Point, right: Point) : AbstractGraphicalObject(arrayOf(bottom, right)) {

    private val bottom: Point get() = getHotPoint(0)
    private val right: Point get() = getHotPoint(1)

    private val center: Point get() = Point(bottom.x, right.y)

    private val a get() = right.x - center.x
    private val b get() = bottom.y - center.y

    constructor() : this(Point(40, 0), Point(0, 60))

    override val boundingBox: Rectangle
        get() = Rectangle(center.x - a, center.y - b, 2 * a, 2 * b)

    override fun selectionDistance(mousePoint: Point): Double {
        val firstPoint = Point((center.x + a * cos(0.0)).toInt(), (center.y + b * sin(0.0)).toInt())
        var min = mousePoint distanceTo firstPoint
        val c = center
        (1..<360).forEach { deg ->
            val radians = deg * Math.PI / 180
            val point = Point((center.x + a * cos(radians)).toInt(), (center.y + b * sin(radians)).toInt())
            if (mousePoint in point..c) {
                return 0.0
            }

            val distance = mousePoint distanceTo point
            if (distance < min) {
                min = distance
            }
        }

        return mousePoint distanceTo center
    }

    override val shapeName: String = "Oval"

    override fun duplicate(): GraphicalObject = Oval(right, bottom)

    override fun render(r: Renderer) {
        val points = (0..<360).map { deg ->
            val radians = deg * Math.PI / 180
            Point((center.x + a * cos(radians)).toInt(), (center.y + b * sin(radians)).toInt())
        }

        r.fillPolygon(points.toTypedArray())
    }

    override val shapeID: String = "@OVAL"

    override fun save(rows: MutableList<String>) {
        rows.add("$shapeID ${right.x} ${right.y} ${bottom.x} ${bottom.y}")
    }

    override fun load(stack: ArrayDeque<GraphicalObject>, data: String) {
        val (x1, y1, x2, y2) = data.removePrefix("$shapeID ").split(' ').map { it.toInt() }
        stack.push(Oval(right = Point(x1, y1), bottom = Point(x2, y2)))
    }

    override fun toString(): String = "Oval(bottom=$bottom, right=$right)"
}
