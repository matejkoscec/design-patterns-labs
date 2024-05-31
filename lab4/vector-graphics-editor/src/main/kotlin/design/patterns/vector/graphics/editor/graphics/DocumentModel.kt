package design.patterns.vector.graphics.editor.graphics


class DocumentModel {

    private val _objects = mutableListOf<GraphicalObject>()
    val objects get() = _objects.toList()

    private val _selectedObjects = mutableListOf<GraphicalObject>()
    val selectedObjects get() = _selectedObjects.toList()

    private val listeners = mutableListOf<DocumentModelListener>()

    private val goListener = object : GraphicalObjectListener {

        override fun graphicalObjectChanged(go: GraphicalObject) {
            notifyDocumentListeners()
        }

        override fun graphicalObjectSelectionChanged(go: GraphicalObject) {
            if (go.isSelected && !_selectedObjects.contains(go)) {
                _selectedObjects.add(go)
            } else {
                _selectedObjects.remove(go)
            }
            notifyDocumentListeners()
        }
    }

    fun clear() {
        _objects.forEach { it.removeGraphicalObjectListener(goListener) }
        _objects.clear()
        _selectedObjects.forEach { it.removeGraphicalObjectListener(goListener) }
        _selectedObjects.clear()
        notifyDocumentListeners()
    }

    fun addGraphicalObject(go: GraphicalObject) {
        go.addGraphicalObjectListener(goListener)
        _objects.add(go)
        notifyDocumentListeners()
    }

    fun removeGraphicalObject(go: GraphicalObject) {
        go.removeGraphicalObjectListener(goListener)
        _objects.remove(go)
        _selectedObjects.remove(go)
        notifyDocumentListeners()
    }

    fun list(): List<GraphicalObject> = objects

    fun addDocumentModelListener(l: DocumentModelListener) {
        listeners.add(l)
    }

    fun removeDocumentModelListener(l: DocumentModelListener) {
        listeners.remove(l)
    }

    fun notifyDocumentListeners() = listeners.forEach { it.documentChange() }

    fun increaseZ(go: GraphicalObject) {
        val i = _objects.indexOf(go)
        if (i == _objects.size - 1) {
            return
        }
        _objects.removeAt(i)
        _objects.add(i + 1, go)
        notifyDocumentListeners()
    }

    fun decreaseZ(go: GraphicalObject) {
        val i = _objects.indexOf(go)
        if (i == 0) {
            return
        }
        _objects.removeAt(i)
        _objects.add(i - 1, go)
        notifyDocumentListeners()

    }

    fun findSelectedGraphicalObject(mousePoint: Point): GraphicalObject? {
        for (i in _objects.indices.reversed()) {
            val obj = _objects[i]
            if (obj.selectionDistance(mousePoint) <= SELECTION_PROXIMITY) {
                return obj
            }
        }

        return null
    }

    fun findSelectedHotPoint(go: GraphicalObject, mousePoint: Point): Int {
        val obj = _objects[_objects.indexOf(go)]
        val inProximity = mutableListOf<Pair<Int, Double>>()

        for (i in 0..<obj.numberOfHotPoints) {
            val d = obj.getHotPoint(i) distanceTo mousePoint
            if (d <= SELECTION_PROXIMITY) {
                inProximity.add(Pair(i, d))
            }
        }

        return inProximity.minByOrNull { it.second }?.first ?: -1
    }

    companion object {
        const val SELECTION_PROXIMITY: Double = 10.0
    }
}
