package design.patterns.vector.graphics.editor.gui

import design.patterns.vector.graphics.editor.Renderer
import design.patterns.vector.graphics.editor.graphics.*
import java.awt.event.KeyEvent


sealed interface State {

    fun mouseDown(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean)

    fun mouseUp(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean)

    fun mouseDragged(mousePoint: Point)

    fun keyPressed(keyCode: Int)

    fun afterDraw(r: Renderer, go: GraphicalObject)

    fun afterDraw(r: Renderer)

    fun onLeaving()
}


data object IdleState : State {

    override fun mouseDown(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) {}

    override fun mouseUp(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) {}

    override fun mouseDragged(mousePoint: Point) {}

    override fun keyPressed(keyCode: Int) {}

    override fun afterDraw(r: Renderer, go: GraphicalObject) {}

    override fun afterDraw(r: Renderer) {}

    override fun onLeaving() {}
}

class AddShapeState(private val prototype: GraphicalObject, private val model: DocumentModel) : State {

    override fun mouseDown(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) {
        val go = prototype.duplicate()
        go.translate(mousePoint)
        model.addGraphicalObject(go)
    }

    override fun mouseUp(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) = Unit

    override fun mouseDragged(mousePoint: Point) = Unit

    override fun keyPressed(keyCode: Int) = Unit

    override fun afterDraw(r: Renderer, go: GraphicalObject) = Unit

    override fun afterDraw(r: Renderer) = Unit

    override fun onLeaving() = Unit
}

class SelectShapeState(private val model: DocumentModel) : State {

    private lateinit var lastSelected: GraphicalObject
    private lateinit var lastSelectionPoint: Point
    private var hotBox = -1
    private var ctrlWasDown = false

    override fun mouseDown(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) {
        val go = model.findSelectedGraphicalObject(mousePoint)

        ctrlWasDown = ctrlDown
        if (!ctrlDown) {
            if (go == null) {
                for (o in model.selectedObjects) o.isSelected = false
                return
            }

            lastSelected = go
            lastSelectionPoint = mousePoint.copy()

            hotBox = (0..<go.numberOfHotPoints).firstOrNull { mousePoint in go.getHotPointBoundingBox(it) } ?: -1

            if (go.isSelected) return

            for (o in model.selectedObjects) o.isSelected = false
            go.isSelected = true

            return
        }

        if (go == null) return

        go.isSelected = !go.isSelected
    }

    override fun mouseUp(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) = Unit

    override fun mouseDragged(mousePoint: Point) {
        if (!::lastSelected.isInitialized || !lastSelected.isSelected || ctrlWasDown) {
            return
        }

        if (hotBox != -1) {
            val hp = lastSelected.getHotPoint(hotBox)
            lastSelected.setHotPoint(hotBox, hp + (mousePoint - lastSelectionPoint))
            lastSelectionPoint = mousePoint.copy()
            return
        }

        for (o in model.selectedObjects) {
            o.translate(mousePoint - lastSelectionPoint)
        }
        lastSelectionPoint = mousePoint.copy()
    }

    override fun keyPressed(keyCode: Int) {
        when (keyCode) {
            KeyEvent.VK_LEFT -> model.selectedObjects.forEach { o ->
                o.translate(Point(-1, 0).also {
                    lastSelectionPoint = it
                })
            }

            KeyEvent.VK_RIGHT -> model.selectedObjects.forEach { o ->
                o.translate(Point(1, 0).also {
                    lastSelectionPoint = it
                })
            }

            KeyEvent.VK_UP -> model.selectedObjects.forEach { o ->
                o.translate(Point(0, -1).also {
                    lastSelectionPoint = it
                })
            }

            KeyEvent.VK_DOWN -> model.selectedObjects.forEach { o ->
                o.translate(Point(0, 1).also {
                    lastSelectionPoint = it
                })
            }

            61, 107 /* PLUS */ -> model.selectedObjects.forEach(model::increaseZ)
            KeyEvent.VK_MINUS -> model.selectedObjects.forEach(model::decreaseZ)

            KeyEvent.VK_G -> {
                val selected = model.selectedObjects
                if (selected.isEmpty()) {
                    return
                }

                val go = CompositeShape(children = selected.map {
                    model.removeGraphicalObject(it)
                    it.isSelected = false
                    it
                })
                model.addGraphicalObject(go)
                go.isSelected = true
            }

            KeyEvent.VK_U -> {
                val selected = model.selectedObjects
                val composite = selected.singleOrNull { it is CompositeShape } as CompositeShape?
                if (composite == null) {
                    return
                }

                model.removeGraphicalObject(composite)
                for (go in composite.children) {
                    model.addGraphicalObject(go)
                    go.isSelected = true
                }
            }
        }
    }

    override fun afterDraw(r: Renderer, go: GraphicalObject) {
        if (!go.isSelected) {
            return
        }

        go.boundingBox.run {
            val topLeft = Point(x, y)
            val topRight = Point(x + width, y)
            val bottomRight = Point(x + width, y + height)
            val bottomLeft = Point(x, y + height)

            r.drawLine(topLeft, topRight)
            r.drawLine(topRight, bottomRight)
            r.drawLine(bottomRight, bottomLeft)
            r.drawLine(bottomLeft, topLeft)
        }

        for (i in 0..<go.numberOfHotPoints) {
            val (topLeft, topRight, bottomRight, bottomLeft) = go.getHotPointBoundingBox(i).points()

            r.drawLine(topLeft, topRight)
            r.drawLine(topRight, bottomRight)
            r.drawLine(bottomRight, bottomLeft)
            r.drawLine(bottomLeft, topLeft)
        }
    }

    override fun afterDraw(r: Renderer) = Unit

    override fun onLeaving() = Unit
}

class EraserState(private val model: DocumentModel) : State {

    private val points = mutableListOf<Point>()

    override fun mouseDown(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) {
        points.add(mousePoint)
        model.notifyDocumentListeners()
    }

    override fun mouseUp(mousePoint: Point, shiftDown: Boolean, ctrlDown: Boolean) {
        for (o in model.objects) {
            if (points.any { o.selectionDistance(it) <= DocumentModel.SELECTION_PROXIMITY }) {
                model.removeGraphicalObject(o)
            }
        }
        points.clear()
        model.notifyDocumentListeners()
    }

    override fun mouseDragged(mousePoint: Point) {
        points.add(mousePoint)
        model.notifyDocumentListeners()
    }

    override fun keyPressed(keyCode: Int) = Unit

    override fun afterDraw(r: Renderer, go: GraphicalObject) = Unit

    override fun afterDraw(r: Renderer) {
        points.zipWithNext(r::drawLine)
    }

    override fun onLeaving() = Unit
}
