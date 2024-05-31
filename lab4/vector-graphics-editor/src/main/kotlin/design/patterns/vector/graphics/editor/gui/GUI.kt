package design.patterns.vector.graphics.editor.gui

import design.patterns.vector.graphics.editor.graphics.DocumentModel
import java.awt.BorderLayout
import javax.swing.JFrame

class GUI : JFrame() {

    var currentState: State = IdleState
        set(value) {
            field.onLeaving()
            field = value
        }

    val documentModel = DocumentModel()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1270, 720)
        layout = BorderLayout()
        setLocationRelativeTo(null)

        add(Toolbar(this), BorderLayout.NORTH)
        add(Canvas(this))

        documentModel.addDocumentModelListener {
            repaint()
        }
    }
}
