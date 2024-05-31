package design.patterns.vector.graphics.editor.svg

import design.patterns.vector.graphics.editor.Renderer
import design.patterns.vector.graphics.editor.graphics.Point
import java.io.File

class SVGRendererImpl(private val fileName: String) : Renderer, AutoCloseable {

    private val lines = mutableListOf(
        "<svg xmlns=\"http://www.w3.org/2000/svg\"\n    xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
    )

    override fun close() {
        lines += "</svg>"
        File(fileName).writeText(lines.joinToString("\n"))
        println("SVG exported")
    }

    override fun drawLine(s: Point, e: Point) {
        lines += "  <line x1=\"${s.x}\" y1=\"${s.y}\" x2=\"${e.x}\" y2=\"${e.y}\" style=\"stroke:#006600;\"/>"
    }

    override fun fillPolygon(points: Array<Point>) {
        lines += "  <polygon points=\"${points.joinToString("  ") { "${it.x},${it.y}" }}\" " +
                "style=\"stroke:#660000; fill:#cc3333; stroke-width: 3;\"/>"
    }
}
