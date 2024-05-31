package design.patterns.vector.graphics.editor.gui

import design.patterns.vector.graphics.editor.Renderer
import design.patterns.vector.graphics.editor.graphics.G2DRendererImpl
import design.patterns.vector.graphics.editor.graphics.Point
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import javax.swing.JComponent

class Canvas(private val gui: GUI) : JComponent() {

    private val model = gui.documentModel

    private val state get() = gui.currentState

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                requestFocusInWindow()
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                state.mouseDown(Point(e.x, e.y), e.isShiftDown, e.isControlDown)
            }

            override fun mouseReleased(e: MouseEvent) {
                state.mouseUp(Point(e.x, e.y), e.isShiftDown, e.isControlDown)
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                state.mouseDragged(Point(e.x, e.y))
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                state.keyPressed(e.keyCode)
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val renderer: Renderer = G2DRendererImpl(g as Graphics2D)
        for (o in model.objects) {
            o.render(renderer)
            state.afterDraw(renderer, o)
        }
        state.afterDraw(renderer)
    }
}
