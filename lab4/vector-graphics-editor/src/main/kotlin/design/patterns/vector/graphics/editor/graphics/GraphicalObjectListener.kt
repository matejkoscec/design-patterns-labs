package design.patterns.vector.graphics.editor.graphics

interface GraphicalObjectListener {

    fun graphicalObjectChanged(go: GraphicalObject)

    fun graphicalObjectSelectionChanged(go: GraphicalObject)
}
