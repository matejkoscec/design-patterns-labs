package design.patterns.vector.graphics.editor.graphics

import design.patterns.vector.graphics.editor.Renderer
import java.awt.Color
import java.awt.Graphics2D

class G2DRendererImpl(private val g2d: Graphics2D) : Renderer {

    override fun drawLine(s: Point, e: Point) {
        g2d.color = Color.BLUE
        g2d.drawLine(s.x, s.y, e.x, e.y)
    }

    override fun fillPolygon(points: Array<Point>) {
        val x = IntArray(points.size)
        val y = IntArray(points.size)
        for ((i, p) in points.withIndex()) {
            x[i] = p.x
            y[i] = p.y
        }

        g2d.color = Color.BLUE
        g2d.fillPolygon(x, y, points.size)

        g2d.color = Color.RED
        g2d.drawPolygon(x, y, points.size)
    }
}
