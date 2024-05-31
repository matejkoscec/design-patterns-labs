package design.patterns.gui.editor

import design.patterns.gui.action
import java.awt.*
import java.awt.event.*
import javax.swing.JComponent
import javax.swing.KeyStroke

class TextEditor : JComponent() {

    private val fontSize = 16
    private val lineHeight = 1.2
    private val font = Font("Monospaced", Font.PLAIN, fontSize)

    private val padding = 40

    var startSelection = false
        set(value) {
            field = value
            textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
            textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
            textEditorModel.notifySelectionRangeObservers()
        }

    val textEditorModel = TextEditorModel("line\nsledeca lajna brate\nnext next line\n")

    val clipboard = ClipboardStack()
    val undoManager = UndoManager.getInstance()

    init {
        layout = BorderLayout()

        val cursor = Cursor(textEditorModel, font, lineHeight, padding)
        add(cursor)

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                requestFocus()
            }
        })

        textEditorModel.addCursorObserver {
            repaint()
        }
        textEditorModel.addTextObserver {
            repaint()
        }

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "cursor.move.left")
        actionMap.put("cursor.move.left", action {
            if (startSelection) {
                val from = textEditorModel.selectionRange.from
                val to = textEditorModel.selectionRange.to
                textEditorModel.cursorLocation = if (from <= to) from.copy() else to.copy()
                textEditorModel.notifyCursorObservers()
                startSelection = false
            } else {
                textEditorModel.moveCursorLeft()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "cursor.move.right")
        actionMap.put("cursor.move.right", action {
            if (startSelection) {
                val from = textEditorModel.selectionRange.from
                val to = textEditorModel.selectionRange.to
                textEditorModel.cursorLocation = if (from > to) from.copy() else to.copy()
                textEditorModel.notifyCursorObservers()
                startSelection = false
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
            textEditorModel.notifySelectionRangeObservers()
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "cursor.move.right_shift")
        actionMap.put("cursor.move.right_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
                textEditorModel.moveCursorRight()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
                textEditorModel.notifySelectionRangeObservers()
            } else {
                textEditorModel.moveCursorRight()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
                textEditorModel.notifySelectionRangeObservers()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "cursor.move.up_shift")
        actionMap.put("cursor.move.up_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
                textEditorModel.moveCursorUp()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
                textEditorModel.notifySelectionRangeObservers()
            } else {
                textEditorModel.moveCursorUp()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
                textEditorModel.notifySelectionRangeObservers()
            }
        })
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "cursor.move.down_shift")
        actionMap.put("cursor.move.down_shift", action {
            if (!startSelection) {
                startSelection = true
                textEditorModel.selectionRange.from = textEditorModel.cursorLocation.copy()
                textEditorModel.moveCursorDown()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
                textEditorModel.notifySelectionRangeObservers()
            } else {
                textEditorModel.moveCursorDown()
                textEditorModel.selectionRange.to = textEditorModel.cursorLocation.copy()
                textEditorModel.notifySelectionRangeObservers()
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
                        KeyEvent.VK_C -> CopyCommand().executeDo()
                        KeyEvent.VK_X -> CutCommand().executeDo()
                        KeyEvent.VK_V -> PasteCommand().executeDo()
                        KeyEvent.VK_Z -> undoManager.undo()
                        KeyEvent.VK_Y -> undoManager.redo()
                    }
                    return
                }
                if (e.isControlDown && e.isShiftDown && e.keyCode == KeyEvent.VK_V) {
                    PasteCommand(take = true).executeDo()
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

        g as Graphics2D

        g.font = font
        val fontMetrics = g.getFontMetrics(font)
        for ((i, line) in textEditorModel.text.withIndex()) {
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)

            val digitWidth = fontMetrics.stringWidth((i % 10).toString())
            val numberWidth = fontMetrics.stringWidth(i.toString())
            g.drawString(i.toString(), padding - (numberWidth + digitWidth), (i + 1) * (fontSize * lineHeight).toInt())
            g.drawLine(
                padding - 5,
                i * (fontSize * lineHeight).toInt(),
                padding - 5,
                (i + 1) * (fontSize * lineHeight).toInt()
            )
            g.drawString(line, padding, (i + 1) * (fontSize * lineHeight).toInt())

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

            val alpha = 0.4f
            val composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
            g.composite = composite

            if (from.row == to.row) {
                g.fillRect(
                    padding + fontMetrics.stringWidth(line.substring(0..<from.col)),
                    i * (fontSize * lineHeight).toInt(),
                    fontMetrics.stringWidth(line.substring(from.col..<to.col)),
                    (fontSize * lineHeight).toInt()
                )
                continue
            }

            when (i) {
                from.row -> {
                    g.fillRect(
                        padding + fontMetrics.stringWidth(line.substring(0..<from.col)),
                        i * (fontSize * lineHeight).toInt(),
                        fontMetrics.stringWidth(line.substring(from.col..<line.length)),
                        (fontSize * lineHeight).toInt()
                    )
                }

                to.row -> {
                    g.fillRect(
                        padding,
                        i * (fontSize * lineHeight).toInt(),
                        fontMetrics.stringWidth(line.substring(0..<to.col)),
                        (fontSize * lineHeight).toInt()
                    )
                }

                else -> {
                    g.fillRect(
                        padding,
                        i * (fontSize * lineHeight).toInt(),
                        fontMetrics.stringWidth(line),
                        (fontSize * lineHeight).toInt()
                    )
                }
            }
        }
    }

    inner class CopyCommand : EditAction {

        override fun executeDo() {
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
        }

        override fun executeUndo(): Nothing = throw UnsupportedOperationException()
    }

    inner class CutCommand : EditAction {

        private lateinit var command: DeleteRangeCommand

        override fun executeDo() {
            if (!startSelection) {
                return
            }
            val from = textEditorModel.selectionRange.from
            val to = textEditorModel.selectionRange.to
            val left = if (from <= to) from else to
            val right = if (from > to) from else to
            if (left.row == right.row) {
                clipboard.push(textEditorModel.currentLine.substring(left.col..<right.col))
                command = DeleteRangeCommand(textEditorModel)
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
            command = DeleteRangeCommand(textEditorModel)
            command.executeDo()
            undoManager.push(command)
            startSelection = false
        }

        override fun executeUndo() = command.executeUndo()
    }

    inner class PasteCommand(private val take: Boolean = false) : EditAction {

        private lateinit var command: EditAction

        override fun executeDo() {
            if (clipboard.isEmpty()) {
                return
            }

            val text = if (take) clipboard.pop() else clipboard.peek()

            if (startSelection) {
                command = DeleteRangeCommand(textEditorModel)
                command.executeDo()
                InsertTextCommand(textEditorModel, text).executeDo()
                startSelection = false
                undoManager.push(command)
                return
            }

            command = InsertTextCommand(textEditorModel, text)
            command.executeDo()
            undoManager.push(command)
        }

        override fun executeUndo() = command.executeUndo()
    }
}
