package design.patterns.gui.editor


interface EditAction {
    fun executeDo()
    fun executeUndo()
}

fun interface StackObserver {
    fun updateStack()
}

class UndoManager private constructor() : StackObserver {

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    private val stackObservers = mutableSetOf<StackObserver>(this)

    fun addUndoStackObserver(observer: StackObserver) = stackObservers.add(observer)
    fun removeUndoStackObserver(observer: StackObserver) = stackObservers.remove(observer)
    private fun notifyUndoStackObservers() = stackObservers.forEach { it.updateStack() }

    fun undo() = undoStack.removeLastOrNull()?.let {
        it.executeUndo()
        redoStack.addLast(it)
        notifyUndoStackObservers()
    }

    fun push(action: EditAction) {
        redoStack.clear()
        undoStack.addLast(action)
        notifyUndoStackObservers()
    }

    fun redo() = redoStack.removeLastOrNull()?.let {
        it.executeDo()
        undoStack.addLast(it)
        notifyUndoStackObservers()
    }

    override fun updateStack() {}

    fun isUndoStackEmpty() = undoStack.isEmpty()
    fun isRedoStackEmpty() = redoStack.isEmpty()

    companion object StaticFields {

        @JvmStatic
        private var instance: UndoManager? = null

        @JvmStatic
        fun getInstance() = instance ?: UndoManager().also { instance = it }
    }
}
