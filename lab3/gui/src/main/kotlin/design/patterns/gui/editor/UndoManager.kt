package design.patterns.gui.editor


interface EditAction {
    fun executeDo()
    fun executeUndo()
}

fun interface UndoStackObserver {
    fun updateUndoStack()
}

fun interface RedoStackObserver {
    fun updateRedoStack()
}

class UndoManager private constructor() : UndoStackObserver, RedoStackObserver {

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    private val undoStackObservers = mutableSetOf<UndoStackObserver>(this)
    private val redoStackObservers = mutableSetOf<RedoStackObserver>(this)

    fun addUndoStackObserver(observer: UndoStackObserver) = undoStackObservers.add(observer)
    fun removeUndoStackObserver(observer: UndoStackObserver) = undoStackObservers.remove(observer)
    private fun notifyUndoStackObservers() = undoStackObservers.forEach { it.updateUndoStack() }

    fun addRedoStackObserver(observer: RedoStackObserver) = redoStackObservers.add(observer)
    fun removeRedoStackObserver(observer: RedoStackObserver) = redoStackObservers.remove(observer)
    private fun notifyRedoStackObservers() = redoStackObservers.forEach { it.updateRedoStack() }

    fun undo() = undoStack.removeLastOrNull()?.let {
        it.executeUndo()
        redoStack.addLast(it)
        notifyUndoStackObservers()
        notifyRedoStackObservers()
    }

    fun push(action: EditAction) {
        redoStack.clear()
        undoStack.addLast(action)
        notifyUndoStackObservers()
        notifyRedoStackObservers()
    }

    fun redo() = redoStack.removeLastOrNull()?.let {
        it.executeDo()
        undoStack.addLast(it)
        notifyUndoStackObservers()
    }

    override fun updateUndoStack() {

    }

    override fun updateRedoStack() {

    }

    companion object StaticFields {

        @JvmStatic
        private var instance: UndoManager? = null

        @JvmStatic
        fun getInstance() = instance ?: UndoManager().also { instance = it }
    }
}
