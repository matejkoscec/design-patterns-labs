package design.patterns.vector.graphics.editor.graphics

abstract class AbstractGraphicalObject(startingHotPoints: Array<Point>) : GraphicalObject {

    private val hotPoints = startingHotPoints
    private val hotPointSelected = BooleanArray(startingHotPoints.size)

    override var isSelected: Boolean = false
        set(value) {
            field = value
            notifySelectionListeners()
        }

    private val listeners = mutableListOf<GraphicalObjectListener>()

    override val numberOfHotPoints: Int get() = hotPoints.size

    override fun getHotPoint(index: Int): Point = hotPoints[index]

    override fun setHotPoint(index: Int, point: Point) {
        hotPoints[index] = point
        notifyObjectListeners()
    }

    override fun getHotPointDistance(index: Int, mousePoint: Point): Double = hotPoints[index] distanceTo mousePoint

    override fun isHotPointSelected(index: Int): Boolean = hotPointSelected[index]

    override fun setHotPointSelected(index: Int, selected: Boolean) {
        hotPointSelected[index] = selected
        notifyObjectListeners()
    }

    override fun translate(delta: Point) {
        for ((i, p) in hotPoints.withIndex()){
            hotPoints[i] = p + delta
        }
        notifyObjectListeners()
    }

    override fun getHotPointBoundingBox(index: Int): Rectangle {
        val hp = getHotPoint(index)
        return Rectangle(hp.x - 5, hp.y - 5, 10, 10)
    }

    override fun addGraphicalObjectListener(l: GraphicalObjectListener) {
        listeners.add(l)
    }

    override fun removeGraphicalObjectListener(l: GraphicalObjectListener) {
        listeners.remove(l)
    }

    protected fun notifyObjectListeners() = listeners.forEach { it.graphicalObjectChanged(this) }

    protected fun notifySelectionListeners() = listeners.forEach { it.graphicalObjectSelectionChanged(this) }
}
