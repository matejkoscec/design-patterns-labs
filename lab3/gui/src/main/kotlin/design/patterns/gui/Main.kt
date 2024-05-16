package design.patterns.gui

import design.patterns.gui.editor.TextEditor
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        EditorFrame().isVisible = true
    }
}

class EditorFrame : JFrame() {
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1270, 720)
        layout = BorderLayout()
        setLocationRelativeTo(null)
//        pack()

        add(TextEditor())
    }
}
