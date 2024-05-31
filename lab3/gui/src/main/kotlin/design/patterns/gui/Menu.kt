package design.patterns.gui

import design.patterns.gui.editor.DeleteRangeCommand
import design.patterns.gui.editor.TextEditor
import design.patterns.gui.editor.UndoManager
import java.io.File
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import kotlin.system.exitProcess

class Menu(private val textEditor: TextEditor) : JMenuBar() {

    private val plugins = File("./src/main/kotlin/design/patterns/gui/plugins")
        .listFiles()!!
        .map {
            Class.forName("design.patterns.gui.plugins.${it.nameWithoutExtension}")
                .getConstructor()
                .newInstance() as Plugin
        }

    init {
        val fileMenu = JMenu("File").apply {
            add(JMenuItem("Open").apply { addActionListener {} })
            add(JMenuItem("Save").apply { addActionListener {} })
            add(JMenuItem("Exit").apply {
                addActionListener { exitProcess(0) }
            })
        }

        val editMenu = JMenu("Edit").apply {
            add(JMenuItem("Undo")).apply {
                val undoManager = UndoManager.getInstance()
                addActionListener { undoManager.undo() }
                undoManager.addUndoStackObserver {
                    isEnabled = !undoManager.isUndoStackEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Redo")).apply {
                val undoManager = UndoManager.getInstance()
                addActionListener { undoManager.redo() }
                undoManager.addUndoStackObserver {
                    isEnabled = !undoManager.isRedoStackEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Cut")).apply {
                addActionListener { textEditor.CutCommand().executeDo() }
                textEditor.textEditorModel.addSelectionRangeObserver {
                    isEnabled = it.isNotEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Copy")).apply {
                addActionListener { textEditor.CopyCommand().executeDo() }
                textEditor.textEditorModel.addSelectionRangeObserver {
                    isEnabled = it.isNotEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Paste")).apply {
                addActionListener { textEditor.PasteCommand().executeDo() }
                textEditor.clipboard.addObserver {
                    isEnabled = textEditor.clipboard.isNotEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Paste and Take")).apply {
                addActionListener { textEditor.PasteCommand(take = true).executeDo() }
                textEditor.clipboard.addObserver {
                    isEnabled = textEditor.clipboard.isNotEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Delete selection")).apply {
                addActionListener {
                    val command = DeleteRangeCommand(textEditor.textEditorModel)
                    command.executeDo()
                    textEditor.undoManager.push(command)
                    textEditor.startSelection = false
                }
                textEditor.textEditorModel.addSelectionRangeObserver {
                    isEnabled = it.isNotEmpty()
                }
                isEnabled = false
            }
            add(JMenuItem("Clear document")).apply {
                addActionListener { textEditor.textEditorModel.clear() }
            }
        }

        val moveMenu = JMenu("Move").apply {
            add(JMenuItem("Cursor to document start")).apply {
                addActionListener { textEditor.textEditorModel.moveCursorToStart() }
            }
            add(JMenuItem("Cursor to document end")).apply {
                addActionListener { textEditor.textEditorModel.moveCursorToEnd() }
            }
        }

        val pluginMenu = JMenu("Plugins").apply {
            plugins.forEach { plug ->
                add(JMenuItem(plug.getName())).apply {
                    addActionListener {
                        plug.execute(textEditor.textEditorModel, textEditor.undoManager, textEditor.clipboard)
                    }
                }
            }
        }

        add(fileMenu)
        add(editMenu)
        add(moveMenu)
        add(pluginMenu)
    }
}
