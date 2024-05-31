package design.patterns.gui.editor

fun interface ClipboardObserver {
    fun updateClipboard(clipboard: List<String>)
}

class ClipboardStack {

    private val texts = ArrayDeque<String>()

    private val observers = mutableSetOf<ClipboardObserver>()

    fun addObserver(observer: ClipboardObserver) = observers.add(observer)
    fun removeObserver(observer: ClipboardObserver) = observers.remove(observer)
    fun notifyObservers() = observers.forEach { it.updateClipboard(texts) }

    fun push(string: String) {
        texts.addLast(string)
        notifyObservers()
    }

    fun pop(): String {
        val text = texts.removeLast()
        notifyObservers()
        return text
    }

    fun peek(): String = texts.last()
    fun isEmpty() = texts.isEmpty()
    fun isNotEmpty() = texts.isNotEmpty()

    fun clear() {
        texts.clear()
        notifyObservers()
    }

    override fun toString(): String = "Clipboard($texts)"
}
