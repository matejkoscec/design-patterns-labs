package design.patterns.vector.graphics.editor

import design.patterns.vector.graphics.editor.graphics.Point

interface Renderer {

    fun drawLine(s: Point, e: Point)

    fun fillPolygon(points: Array<Point>)
}
