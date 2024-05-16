package design.patterns.gui.editor

import design.patterns.gui.action
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.security.Key
import javax.swing.JComponent
import javax.swing.KeyStroke

class TextEditor : JComponent() {

    private val fontSize = 16
    private val lineHeight = 1.2
    private val font = Font("Monospaced", Font.PLAIN, fontSize)

    private var startSelection = false

    private val textEditorModel = TextEditorModel("line\nsledeca lajna brate\nnext next line\n")

    private val clipboard = ClipboardStack()
    private val undoManager = UndoManager.getInstance()

    init {
        textEditorModel.addCursorObserver {
            repaint()
        }
        textEditorModel.addTextObserver {
            repaint()
        }
        clipboard.addObserver {
            println(clipboard)
        }

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "cursor.move.left")
        actionMap.put("cursor.move.left", action {
            if (startSelection) {
                startSelection = false
                val from = textEditorModel.selectionRange.from
                val to = textEditorModel.selectionRange.to
                textEditorModel.cursorLocation = if (from <= to) from.copy() else to.copy()
                textEditorModel.notifyCursorObservers()
            } else {
                textEditorModel.moveCursorLeft()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "cursor.move.right")
        actionMap.put("cursor.move.right", action {
            if (startSelection) {
                startSelection = false
                val from = textEditorModel.selectionRange.from
                val to = textEditorModel.selectionRange.to
                textEditorModel.cursorLocation = if (from > to) from.copy() else to.copy()
                textEditorModel.notifyCursorObservers()
            } else {
                textEditorModel.moveCursorRight()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "cursor.move.up")
        actionMap.put("cursor.move.up", action {
            if (startSelection) {
                startSelection = false
                val from = textEditorModel.selectionRange.from
                val to = textEditorModel.selectionRange.to
                textEditorModel.cursorLocation = if (from <= to) from.copy() else to.copy()
                textEditorModel.moveCursorUp()
            } else {
                textEditorModel.moveCursorUp()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "cursor.move.down")
        actionMap.put("cursor.move.down", action {
            if (startSelection) {
                startSelection = false
                val from = textEditorModel.selectionRange.from
                val to = textEditorModel.selectionRange.to
                textEditorModel.cursorLocation = if (from <= to) from.copy() else to.copy()
                textEditorModel.moveCursorDown()
            } else {
                textEditorModel.moveCursorDown()
            }
        })

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), "cursor.move.left_shift")
        actionMap.put("cursor.move.left_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
            }
            textEditorModel.moveCursorLeft()
            textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "cursor.move.right_shift")
        actionMap.put("cursor.move.right_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
                textEditorModel.moveCursorRight()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            } else {
                textEditorModel.moveCursorRight()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "cursor.move.up_shift")
        actionMap.put("cursor.move.up_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
                textEditorModel.moveCursorUp()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            } else {
                textEditorModel.moveCursorUp()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "cursor.move.down_shift")
        actionMap.put("cursor.move.down_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
                textEditorModel.moveCursorDown()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            } else {
                textEditorModel.moveCursorDown()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            }
        })

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "key.backspace")
        actionMap.put("key.backspace", action {
            if (startSelection) {
                val command = DeleteRangeCommand(textEditorModel)
                command.executeDo()
                undoManager.push(command)
                startSelection = false
            } else {
                if (textEditorModel.cursorLocation.row == 0 && textEditorModel.cursorLocation.col == 0) {
                    return@action
                }
                val command = DeleteBeforeCommand(textEditorModel)
                command.executeDo()
                undoManager.push(command)
            }
        })

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "key.delete")
        actionMap.put("key.delete", action {
            if (startSelection) {
                val command = DeleteRangeCommand(textEditorModel)
                command.executeDo()
                undoManager.push(command)
                startSelection = false
            } else {
                if (textEditorModel.cursorLocation.row == textEditorModel.lines.size - 1
                    && textEditorModel.cursorLocation.col == textEditorModel.currentLine.length
                ) {
                    return@action
                }
                val command = DeleteAfterCommand(textEditorModel)
                command.executeDo()
                undoManager.push(command)
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.isControlDown && e.isActionKey) {
                    when (e.keyCode) {
                        KeyEvent.VK_LEFT -> textEditorModel.moveCursorToPreviousWord()
                        KeyEvent.VK_RIGHT -> textEditorModel.moveCursorToNextWord()
                        KeyEvent.VK_HOME -> textEditorModel.moveCursorToStart()
                        KeyEvent.VK_END -> textEditorModel.moveCursorToEnd()
                    }
                    startSelection = false
                }
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_UNDEFINED) {
                    return
                }
                if (e.isMetaDown) {
                    return
                }
                if (e.isControlDown && !e.isShiftDown) {
                    when (e.keyCode) {
                        KeyEvent.VK_C -> {
                            if (!startSelection) {
                                return
                            }
                            val from = textEditorModel.selectionRange.from
                            val to = textEditorModel.selectionRange.to
                            val left = if (from <= to) from else to
                            val right = if (from > to) from else to
                            if (left.row == right.row) {
                                clipboard.push(textEditorModel.currentLine.substring(left.col..<right.col))
                                return
                            }

                            val text = StringBuilder()
                            for ((row, line) in textEditorModel.text.withIndex()) {
                                if (row !in left.row..right.row) {
                                    continue
                                }
                                when (row) {
                                    left.row -> text.appendLine(line.substring(left.col..<line.length))
                                    right.row -> text.append(line.substring(0..<right.col))
                                    else -> text.appendLine(line)
                                }
                            }

                            clipboard.push(text.toString())
                            return
                        }

                        KeyEvent.VK_X -> {
                            if (!startSelection) {
                                return
                            }
                            val from = textEditorModel.selectionRange.from
                            val to = textEditorModel.selectionRange.to
                            val left = if (from <= to) from else to
                            val right = if (from > to) from else to
                            if (left.row == right.row) {
                                clipboard.push(textEditorModel.currentLine.substring(left.col..<right.col))
                                val command = DeleteRangeCommand(textEditorModel)
                                command.executeDo()
                                undoManager.push(command)
                                startSelection = false
                                return
                            }

                            val text = StringBuilder()
                            for ((row, line) in textEditorModel.text.withIndex()) {
                                if (row !in left.row..right.row) {
                                    continue
                                }
                                when (row) {
                                    left.row -> text.appendLine(line.substring(left.col..<line.length))
                                    right.row -> text.append(line.substring(0..<right.col))
                                    else -> text.appendLine(line)
                                }
                            }

                            clipboard.push(text.toString())
                            val command = DeleteRangeCommand(textEditorModel)
                            command.executeDo()
                            undoManager.push(command)
                            startSelection = false
                            return
                        }

                        KeyEvent.VK_V -> {
                            if (clipboard.isEmpty()) {
                                return
                            }

                            val command = InsertTextCommand(textEditorModel, clipboard.peek())
                            command.executeDo()
                            undoManager.push(command)
                            return
                        }

                        KeyEvent.VK_Z -> {
                            undoManager.undo()
                            return
                        }

                        KeyEvent.VK_Y -> {
                            undoManager.redo()
                            return
                        }
                    }
                }
                if (e.isControlDown && e.isShiftDown && e.keyCode == KeyEvent.VK_V) {
                    if (clipboard.isEmpty()) {
                        return
                    }

                    textEditorModel.insert(clipboard.pop())
                    return
                }
                if (e.isActionKey) {
                    when (e.keyCode) {
                        KeyEvent.VK_HOME -> {
                            textEditorModel.moveCursorHome()
                            startSelection = false
                        }
                        KeyEvent.VK_END -> {
                            textEditorModel.moveCursorEnd()
                            startSelection = false
                        }
                    }
                    return
                }
                if (e.keyCode in arrayOf(
                        KeyEvent.VK_BACK_SPACE,
                        KeyEvent.VK_DELETE,
                        KeyEvent.VK_SHIFT,
                        KeyEvent.VK_CONTROL,
                        KeyEvent.VK_ALT,
                        KeyEvent.VK_ALT_GRAPH,
                        KeyEvent.VK_META,
                        KeyEvent.VK_ESCAPE
                    )
                ) {
                    return
                }

                if (startSelection) {
                    val command = DeleteRangeCommand(textEditorModel)
                    command.executeDo()
                    InsertCharCommand(textEditorModel, e.keyChar).executeDo()
                    undoManager.push(command)
                    startSelection = false
                    return
                }
                val command = InsertCharCommand(textEditorModel, e.keyChar)
                command.executeDo()
                undoManager.push(command)
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.font = font
        val fontMetrics = g.getFontMetrics(font)
        for ((i, line) in textEditorModel.text.withIndex()) {
            g.drawString(line, 0, (i + 1) * (fontSize * lineHeight).toInt())
            if (i == textEditorModel.cursorLocation.row) {
                val color = g.color
                g.color = Color.RED

                val col = textEditorModel.cursorLocation.col
                val sw = fontMetrics.stringWidth(line.substring(0..<col))

                g.drawLine(
                    sw,
                    i * (fontSize * lineHeight).toInt(),
                    sw,
                    (i + 1) * (fontSize * lineHeight).toInt()
                )

                g.color = color
            }

            if (!startSelection) {
                continue
            }

            val left = textEditorModel.selectionRange.from
            val right = textEditorModel.selectionRange.to
            val from = if (left <= right) left else right
            val to = if (left > right) left else right

            if (i !in from.row..to.row) {
                continue
            }

            if (from.row == to.row) {
                g.drawRect(
                    fontMetrics.stringWidth(line.substring(0..<from.col)),
                    i * (fontSize * lineHeight).toInt(),
                    fontMetrics.stringWidth(line.substring(from.col..<to.col)),
                    (fontSize * lineHeight).toInt()
                )
                continue
            }

            when (i) {
                from.row -> {
                    g.drawRect(
                        fontMetrics.stringWidth(line.substring(0..<from.col)),
                        i * (fontSize * lineHeight).toInt(),
                        fontMetrics.stringWidth(line.substring(from.col..<line.length)),
                        (fontSize * lineHeight).toInt()
                    )
                }

                to.row -> {
                    g.drawRect(
                        0,
                        i * (fontSize * lineHeight).toInt(),
                        fontMetrics.stringWidth(line.substring(0..<to.col)),
                        (fontSize * lineHeight).toInt()
                    )
                }

                else -> {
                    g.drawRect(
                        0,
                        i * (fontSize * lineHeight).toInt(),
                        fontMetrics.stringWidth(line),
                        (fontSize * lineHeight).toInt()
                    )
                }
            }
        }
    }
}
