package design.patterns.gui

import design.patterns.gui.editor.TextEditor
import design.patterns.gui.editor.UndoManager
import javax.swing.JButton
import javax.swing.JToolBar

class Toolbar(private val textEditor: TextEditor) : JToolBar() {

    init {
        add(JButton("Undo").apply {
            val undoManager = UndoManager.getInstance()
            addActionListener { undoManager.undo() }
            undoManager.addUndoStackObserver {
                isEnabled = !undoManager.isUndoStackEmpty()
            }
            isEnabled = false
        })
        add(JButton("Redo").apply {
            val undoManager = UndoManager.getInstance()
            addActionListener { undoManager.redo() }
            undoManager.addUndoStackObserver {
                isEnabled = !undoManager.isRedoStackEmpty()
            }
            isEnabled = false
        })
        add(JButton("Cut").apply {
            addActionListener { textEditor.CutCommand().executeDo() }
            textEditor.textEditorModel.addSelectionRangeObserver {
                isEnabled = it.isNotEmpty()
            }
            isEnabled = false
        })
        add(JButton("Copy").apply {
            addActionListener { textEditor.CopyCommand().executeDo() }
            textEditor.textEditorModel.addSelectionRangeObserver {
                isEnabled = it.isNotEmpty()
            }
            isEnabled = false
        })
        add(JButton("Paste").apply {
            addActionListener { textEditor.PasteCommand().executeDo() }
            textEditor.clipboard.addObserver {
                isEnabled = textEditor.clipboard.isNotEmpty()
            }
            isEnabled = false
        })
    }
}
