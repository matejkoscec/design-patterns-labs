package design.patterns.gui

import design.patterns.gui.editor.TextEditor
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.SwingConstants

class StatusBar(textEditor: TextEditor) : JLabel() {

    private val model = textEditor.textEditorModel

    init {
        text = getInfo()
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        )
        horizontalAlignment = SwingConstants.RIGHT

        model.addCursorObserver {
            text = getInfo()
        }
        model.addTextObserver {
            text = getInfo()
        }
    }

    fun getInfo(): String {
        val (row, col) = model.cursorLocation
        return " $row:$col    ${model.lines.size} lines"
    }
}
