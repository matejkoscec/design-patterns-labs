package design.patterns.gui

import design.patterns.gui.editor.ClipboardStack
import design.patterns.gui.editor.TextEditorModel
import design.patterns.gui.editor.UndoManager

interface Plugin {
    fun getName(): String
    fun getDescription(): String
    fun execute(model: TextEditorModel, undoManager: UndoManager, clipboardStack: ClipboardStack)
}
