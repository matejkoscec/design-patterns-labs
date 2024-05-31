package design.patterns.vector.graphics.editor.graphics

import design.patterns.vector.graphics.editor.Renderer


interface GraphicalObject {

    // Podrška za uređivanje objekta
    var isSelected: Boolean

    val numberOfHotPoints: Int

    fun getHotPoint(index: Int): Point
    fun setHotPoint(index: Int, point: Point)
    fun isHotPointSelected(index: Int): Boolean
    fun setHotPointSelected(index: Int, selected: Boolean)
    fun getHotPointDistance(index: Int, mousePoint: Point): Double

    // Geometrijska operacija nad oblikom
    fun translate(delta: Point)
    val boundingBox: Rectangle
    fun getHotPointBoundingBox(index: Int): Rectangle

    fun selectionDistance(mousePoint: Point): Double

    // Podrška za crtanje (dio mosta)
    fun render(r: Renderer)

    // Observer za dojavu promjena modelu
    fun addGraphicalObjectListener(l: GraphicalObjectListener)
    fun removeGraphicalObjectListener(l: GraphicalObjectListener)

    // Podrška za prototip (alatna traka, stvaranje objekata u crtežu, ...)
    val shapeName: String

    fun duplicate(): GraphicalObject

    // Podrška za snimanje i učitavanje
    val shapeID: String

    fun load(stack: ArrayDeque<GraphicalObject>, data: String)
    fun save(rows: MutableList<String>)
}
