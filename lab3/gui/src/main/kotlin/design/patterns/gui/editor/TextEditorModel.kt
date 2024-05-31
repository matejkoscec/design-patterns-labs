package design.patterns.gui.editor

import design.patterns.gui.Location
import design.patterns.gui.LocationRange


fun interface CursorObserver {
    fun updateCursorLocation(loc: Location)
}

fun interface SelectionRangeObserver {
    fun updateSelectionRange(range: LocationRange)
}

fun interface TextObserver {
    fun updateText()
}

class TextEditorModel(text: String) {

    val lines: MutableList<StringBuilder> = text.split("\n").map(::StringBuilder).toMutableList()
    val text get() = lines.map { it.toString() }

    var currentLine: StringBuilder
        get() = lines[cursorLocation.row]
        set(value) {
            lines[cursorLocation.row] = value
        }

    var cursorLocation = Location(0, 0)
    var selectionRange = LocationRange(Location(0, 0), Location(0, 0))

    private val cursorObservers = mutableSetOf<CursorObserver>()
    private val selectionRangeObservers = mutableSetOf<SelectionRangeObserver>()
    private val textObservers = mutableSetOf<TextObserver>()

    fun allLines(): Iterator<StringBuilder> = lines.iterator()
    fun linesRange(from: Int, to: Int): Iterator<StringBuilder> = lines.subList(from, to).iterator()

    fun addCursorObserver(observer: CursorObserver) = cursorObservers.add(observer)
    fun removeCursorObserver(observer: CursorObserver) = cursorObservers.remove(observer)
    fun notifyCursorObservers() = cursorObservers.forEach { it.updateCursorLocation(cursorLocation) }

    fun addSelectionRangeObserver(observer: SelectionRangeObserver) = selectionRangeObservers.add(observer)
    fun removeSelectionRangeObserver(observer: SelectionRangeObserver) = selectionRangeObservers.remove(observer)
    fun notifySelectionRangeObservers() = selectionRangeObservers.forEach { it.updateSelectionRange(selectionRange) }

    fun addTextObserver(observer: TextObserver) = textObservers.add(observer)
    fun removeTextObserver(observer: TextObserver) = textObservers.remove(observer)
    fun notifyTextObservers() = textObservers.forEach { it.updateText() }

    fun moveCursorLeft() {
        if (cursorLocation.col == 0 && cursorLocation.row != 0) {
            cursorLocation.moveUp(0)
            cursorLocation.col = currentLine.length
        } else {
            cursorLocation.moveLeft(0)
        }
        notifyCursorObservers()
    }

    fun moveCursorRight() {
        if (cursorLocation.col == currentLine.length && cursorLocation.row != lines.size - 1) {
            cursorLocation.moveDown(lines.size - 1)
            cursorLocation.col = 0
        } else {
            cursorLocation.moveRight(currentLine.length)
        }
        notifyCursorObservers()
    }

    fun moveCursorUp() {
        cursorLocation.moveUp(0)
        if (cursorLocation.col > currentLine.length) {
            cursorLocation.moveRight(currentLine.length)
        }
        notifyCursorObservers()
    }

    fun moveCursorDown() {
        cursorLocation.moveDown(lines.size - 1)
        if (cursorLocation.col > currentLine.length) {
            cursorLocation.moveRight(currentLine.length)
        }
        notifyCursorObservers()
    }

    fun moveCursorHome() {
        cursorLocation.col = 0
        notifyCursorObservers()
    }

    fun moveCursorEnd() {
        cursorLocation.col = currentLine.length
        notifyCursorObservers()
    }

    fun moveCursorToPreviousWord() {
        if (cursorLocation.row == 0 && cursorLocation.col == 0) {
            return
        }

        if (cursorLocation.col > 0) {
            do cursorLocation.moveLeft(0)
            while (cursorLocation.col > 0 && !currentLine[cursorLocation.col - 1].isWhitespace())
        } else {
            cursorLocation.moveUp(0)
            cursorLocation.col = currentLine.length
        }

        notifyCursorObservers()
    }

    fun moveCursorToNextWord() {
        if (cursorLocation.row == lines.size - 1 && cursorLocation.col == currentLine.length) {
            return
        }

        if (cursorLocation.col < currentLine.length) {
            do cursorLocation.moveRight(currentLine.length)
            while (cursorLocation.col < currentLine.length && !currentLine[cursorLocation.col].isWhitespace())
        } else {
            cursorLocation.moveDown(lines.size - 1)
            cursorLocation.col = 0
        }

        notifyCursorObservers()
    }

    fun moveCursorToStart() {
        cursorLocation.row = 0
        cursorLocation.col = 0
        notifyCursorObservers()
    }

    fun moveCursorToEnd() {
        cursorLocation.row = lines.size - 1
        cursorLocation.col = lines.last().length
        notifyCursorObservers()
    }

    fun deleteBefore() {
        if (cursorLocation.col == 0) {
            if (cursorLocation.row == 0) {
                return
            }

            val prevLine = currentLine
            cursorLocation.moveUp(0)
            val lineAbove = currentLine
            cursorLocation.col = lineAbove.length

            lineAbove.append(prevLine)
            lines.removeAt(cursorLocation.row + 1)

            notifyTextObservers()
            return
        }

        cursorLocation.moveLeft(0)
        currentLine.deleteCharAt(cursorLocation.col)
        notifyTextObservers()
    }

    fun deleteAfter() {
        if (cursorLocation.col == currentLine.length) {
            if (cursorLocation.row == lines.size - 1) {
                return
            }

            currentLine.append(lines[cursorLocation.row + 1])
            lines.removeAt(cursorLocation.row + 1)

            notifyTextObservers()
            return
        }

        currentLine.deleteCharAt(cursorLocation.col)
        notifyTextObservers()
    }

    fun deleteRange(range: LocationRange) {
        val from = if (range.from <= range.to) range.from else range.to
        val to = if (range.from > range.to) range.from else range.to

        if (from.row == to.row) {
            lines[from.row].deleteRange(from.col, to.col)
            cursorLocation = from
            notifyTextObservers()
            return
        }

        lines[from.row].deleteRange(from.col, lines[from.row].length)

        lines[to.row].deleteRange(0, to.col)
        lines[from.row].append(lines[to.row])
        lines.removeAt(to.row)

        for (row in from.row + 1..<to.row) {
            lines.removeAt(from.row + 1)
        }

        cursorLocation = from
        notifyTextObservers()
    }

    fun insert(c: Char) {
        if (c == '\n') {
            val oldStr = currentLine.substring(0..<cursorLocation.col)
            val newStr = currentLine.substring(cursorLocation.col..<currentLine.length)
            currentLine = StringBuilder(oldStr)
            if (cursorLocation.row == lines.size - 1) {
                lines.add(StringBuilder(newStr))
            } else {
                lines.add(cursorLocation.row + 1, StringBuilder(newStr))
            }
            cursorLocation.row += 1
            cursorLocation.col = 0
            notifyTextObservers()
            return
        }

        currentLine.insert(cursorLocation.col, c)
        cursorLocation.moveRight(currentLine.length)
        notifyTextObservers()
    }

    fun insert(text: String) {
        val newLines = text.split('\n')
        if (newLines.size == 1) {
            currentLine.insert(cursorLocation.col, newLines.first())
            cursorLocation.col += newLines.first().length
            notifyTextObservers()
            return
        }

        val movedText = currentLine.substring(cursorLocation.col)
        currentLine.deleteRange(cursorLocation.col, currentLine.length)
        for ((i, line) in newLines.withIndex()) when (i) {
            0 -> {
                currentLine.append(line)
            }

            newLines.lastIndex -> {
                lines.add(cursorLocation.row + i, StringBuilder(line + movedText))
                cursorLocation.row += i
                cursorLocation.col = line.length
            }

            else -> {
                lines.add(cursorLocation.row + i, StringBuilder(line))
            }
        }

        notifyTextObservers()
    }

    fun clear() {
        moveCursorToStart()
        lines.clear()
        lines.add(StringBuilder())
        notifyTextObservers()
    }
}

class DeleteBeforeCommand(private val model: TextEditorModel) : EditAction {

    private val prevCursor: Location = model.cursorLocation.copy()
    private val deletedChar: Char? = when {
        model.cursorLocation.row == 0 && model.cursorLocation.col == 0 -> null
        model.cursorLocation.col == 0 -> '\n'
        else -> model.currentLine[model.cursorLocation.col - 1]
    }

    private lateinit var eolCursor: Location

    override fun executeDo() {
        model.cursorLocation = prevCursor.copy()
        model.deleteBefore()
        eolCursor = model.cursorLocation.copy()
    }

    override fun executeUndo() {
        model.cursorLocation = prevCursor.copy()

        when (deletedChar) {
            null -> {
                return
            }

            '\n' -> {
                val newLineStr = model.lines[prevCursor.row - 1].substring(eolCursor.col)
                model.lines[prevCursor.row - 1].deleteRange(eolCursor.col, model.lines[prevCursor.row - 1].length)
                model.lines.add(prevCursor.row, StringBuilder(newLineStr))
            }

            else -> {
                model.lines[prevCursor.row].insert(prevCursor.col - 1, deletedChar)
            }
        }

        model.notifyTextObservers()
    }
}

class DeleteAfterCommand(private val model: TextEditorModel) : EditAction {

    private val prevCursor: Location = model.cursorLocation.copy()
    private val deletedChar: Char? = when {
        model.cursorLocation.row == model.lines.size - 1 && model.cursorLocation.col == model.currentLine.length -> null
        model.cursorLocation.col == model.currentLine.length -> '\n'
        else -> model.currentLine[model.cursorLocation.col]
    }

    override fun executeDo() {
        model.cursorLocation = prevCursor.copy()
        model.deleteAfter()
    }

    override fun executeUndo() {
        model.cursorLocation = prevCursor.copy()

        when (deletedChar) {
            null -> {
                return
            }

            '\n' -> {
                val newLineStr = model.lines[prevCursor.row].substring(prevCursor.col)
                model.lines[prevCursor.row].deleteRange(prevCursor.col, model.lines[prevCursor.row].length)
                model.lines.add(prevCursor.row + 1, StringBuilder(newLineStr))
            }

            else -> {
                model.lines[prevCursor.row].insert(prevCursor.col, deletedChar)
            }
        }

        model.notifyTextObservers()
    }
}

class DeleteRangeCommand(private val model: TextEditorModel) : EditAction {

    private val range: LocationRange = model.selectionRange.copy()

    private val from = if (range.from <= range.to) range.from.copy() else range.to.copy()
    private val to = if (range.from > range.to) range.from.copy() else range.to.copy()

    private val text = mutableListOf<StringBuilder>()

    override fun executeDo() {
        model.cursorLocation = from.copy()

        if (from.row == to.row) {
            text.add(StringBuilder(model.currentLine.substring(from.col..<to.col)))
            model.deleteRange(range)
            return
        }

        for (row in from.row..to.row) when (row) {
            from.row -> text.add(StringBuilder(model.lines[row].substring(from.col..<model.lines[row].length)))
            to.row -> text.add(StringBuilder(model.lines[row].substring(0..<to.col)))
            else -> text.add(StringBuilder(model.lines[row]))
        }
        model.deleteRange(range)
        println(text)
    }

    override fun executeUndo() {
        model.cursorLocation = to.copy()

        if (from.row == to.row) {
            model.lines[from.row].insert(from.col, text[0])
            model.notifyTextObservers()
            return
        }

        var movedString = ""
        for ((i, row) in (from.row..to.row).withIndex()) when (row) {
            from.row -> {
                movedString = model.lines[from.row].substring(from.col..<model.lines[from.row].length)
                model.lines[from.row].deleteRange(from.col, model.lines[from.row].length)
                model.lines[from.row].append(text[i])
            }

            to.row -> {
                model.lines.add(from.row + i, text[i].append(movedString))
            }

            else -> {
                model.lines.add(from.row + i, text[i])
            }
        }
        model.notifyTextObservers()
    }
}

class InsertCharCommand(private val model: TextEditorModel, private val c: Char) : EditAction {

    private val prevCursor: Location = model.cursorLocation.copy()

    override fun executeDo() {
        model.cursorLocation = prevCursor.copy()
        model.insert(c)
    }

    override fun executeUndo() {
        if (c == '\n') {
            model.lines[prevCursor.row].append(model.lines[prevCursor.row + 1])
            model.lines.removeAt(prevCursor.row + 1)
            model.cursorLocation = prevCursor.copy()
            model.notifyTextObservers()
            return
        }

        if (model.lines[prevCursor.row].isNotEmpty()) {
            model.lines[prevCursor.row].deleteAt(prevCursor.col)
        }
        model.cursorLocation = prevCursor.copy()
        model.notifyTextObservers()
    }
}

class InsertTextCommand(private val model: TextEditorModel, private val text: String) : EditAction {

    private val prevCursor: Location = model.cursorLocation.copy()

    override fun executeDo() {
        model.cursorLocation = prevCursor.copy()
        model.insert(text)
    }

    override fun executeUndo() {
        val textLines = text.split('\n')
        if (textLines.size == 1) {
            model.lines[prevCursor.row].deleteRange(prevCursor.col, prevCursor.col + textLines.first().length)
            model.cursorLocation = prevCursor.copy()
            model.notifyTextObservers()
            return
        }

        for ((i, line) in textLines.withIndex()) when (i) {
            0 -> {
                model.lines[prevCursor.row].deleteRange(prevCursor.col, prevCursor.col + line.length)
            }

            textLines.lastIndex -> {
                model.lines[prevCursor.row + 1].deleteRange(0, line.length)
                model.lines[prevCursor.row].append(model.lines[prevCursor.row + 1])
                model.lines.removeAt(prevCursor.row + 1)
            }

            else -> {
                model.lines.removeAt(prevCursor.row + 1)
            }
        }

        model.cursorLocation = prevCursor.copy()
        model.notifyTextObservers()
    }
}
