package design.patterns.vector.graphics.editor

import design.patterns.vector.graphics.editor.gui.GUI
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        GUI().isVisible = true
    }
}
