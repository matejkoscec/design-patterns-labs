package design.patterns.gui.plugins

import design.patterns.gui.Plugin
import design.patterns.gui.editor.ClipboardStack
import design.patterns.gui.editor.TextEditorModel
import design.patterns.gui.editor.UndoManager
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class StatisticsPlugin : Plugin {

    override fun getName(): String = "Statistics"
    override fun getDescription(): String = "info about lines, words and letters"

    override fun execute(model: TextEditorModel, undoManager: UndoManager, clipboardStack: ClipboardStack) {
        val lines = model.lines.size
        val words = model.lines.filter { it.isNotBlank() }.sumOf { it.count(Char::isWhitespace) + 1 }
        val letters = model.lines.sumOf { it.count(Char::isLetter) }

        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(
                null,
                "Lines: $lines\n" +
                        "Words: $words\n" +
                        "Letters: $letters",
                "Information",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
}
