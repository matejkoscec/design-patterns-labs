package design.patterns.vector.graphics.editor.graphics

import design.patterns.vector.graphics.editor.Renderer
import design.patterns.vector.graphics.editor.pop
import design.patterns.vector.graphics.editor.push

class CompositeShape(val children: List<GraphicalObject>) : AbstractGraphicalObject(arrayOf()) {

    constructor() : this(listOf())

    override val boundingBox: Rectangle
        get() {
            val boxes = children.map { it.boundingBox }

            val minX = boxes.minOf { it.x }
            val minY = boxes.minOf { it.y }
            val maxX = boxes.maxOf { it.x + it.width }
            val maxY = boxes.maxOf { it.y + it.height }

            return Rectangle(minX, minY, maxX - minX, maxY - minY)
        }

    override fun translate(delta: Point) {
        for (o in children) {
            o.translate(delta)
        }
        notifyObjectListeners()
    }

    override fun selectionDistance(mousePoint: Point): Double = children.minOf { it.selectionDistance(mousePoint) }

    override fun render(r: Renderer) {
        for (o in children) {
            o.render(r)
        }
    }

    override val shapeName: String get() = "Kompozit"

    override fun duplicate(): GraphicalObject = CompositeShape(children.toList())

    override val shapeID: String = "@COMP"

    override fun save(rows: MutableList<String>) {
        for (o in children) {
            o.save(rows)
        }
        rows.add("$shapeID ${children.size}")
    }

    override fun load(stack: ArrayDeque<GraphicalObject>, data: String) {
        val size = data.removePrefix("$shapeID ").toInt()
        val children = (0..<size).map { stack.pop() }
        stack.push(CompositeShape(children))
    }
}
