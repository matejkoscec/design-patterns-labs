package design.patterns.vector.graphics.editor

fun <T> ArrayDeque<T>.push(t: T) = addLast(t)

fun <T> ArrayDeque<T>.pop() = removeLast()
