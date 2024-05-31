package design.patterns.gui.plugins

import design.patterns.gui.Plugin
import design.patterns.gui.editor.ClipboardStack
import design.patterns.gui.editor.TextEditorModel
import design.patterns.gui.editor.UndoManager

class CapitalizePlugin : Plugin {

    override fun getName(): String = "Capitalize"
    override fun getDescription(): String = "capitalize first letter of every word"

    override fun execute(model: TextEditorModel, undoManager: UndoManager, clipboardStack: ClipboardStack) {
        for (line in model.lines) {
            var foundSpace = true
            for ((i, c) in line.withIndex()) {
                if (c.isWhitespace()) {
                    foundSpace = true
                    continue
                }
                if (foundSpace) {
                    foundSpace = false
                    line[i] = c.uppercaseChar()
                }
            }
        }
        model.notifyTextObservers()
    }
}
