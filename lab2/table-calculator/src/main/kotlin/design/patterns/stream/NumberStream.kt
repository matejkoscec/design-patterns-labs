package design.patterns.stream

import java.io.File
import java.time.Instant


fun main() {
    val numberStream = NumberStream(source = FileStream(File("nums.txt")))

    numberStream.subscribe(FileWriteAction(File("output.txt")))
    numberStream.subscribe(SumAction())
    numberStream.subscribe(AvgAction())
    numberStream.subscribe(MedianAction())

    numberStream.start()
}


interface IntStream : Iterable<Int>

class KeyboardStream : IntStream {

    override operator fun iterator() = object : Iterator<Int> {
        private var next: Int = 0

        init {
            println("Opening keyboard stream")
        }

        override fun hasNext(): Boolean {
            var input = readln()
            while (input != "") {
                val num = input.toIntOrNull()
                if (num != null) {
                    next = num
                    return true
                }

                println("Wrong number format '$input'. Only non-negative whole numbers are allowed")
                input = readln()
            }

            return false
        }

        override fun next(): Int = next
    }
}

class FileStream(private val file: File) : IntStream {

    override operator fun iterator() = object : Iterator<Int> {

        init {
            check(file.exists()) { "Input file does not exist" }
            check(file.isFile) { "Input file cannot be a folder" }
            println("Opening file stream '${file.name}'")
        }

        private val reader = file.bufferedReader()
        private var next: Int = 0

        override fun hasNext(): Boolean {
            val input = reader.readLine()
            if (input == null) {
                println("Reached EOF, closing file stream")
                reader.close()
                return false
            }

            val num = input.toIntOrNull()
            if (num != null && num >= 0) {
                next = num
                return true
            }

            println("Wrong number format '$input'. Only non-negative whole numbers are allowed")
            reader.close()

            return false
        }

        override fun next(): Int = next
    }
}

interface IntStreamListener {
    fun handle(values: Iterable<Int>)
}

class FileWriteAction(private val file: File) : IntStreamListener, AutoCloseable {

    private val writer by lazy { file.bufferedWriter() }

    override fun handle(values: Iterable<Int>) {
        writer.write("${Instant.now()} ${values.toList()}\n")
        writer.flush()
    }

    override fun close() = writer.close()
}

class SumAction : IntStreamListener {
    override fun handle(values: Iterable<Int>) = println("Sum: ${values.sum()}")
}

class AvgAction : IntStreamListener {
    override fun handle(values: Iterable<Int>) = println("Average: ${values.average()}")
}

class MedianAction : IntStreamListener {

    override fun handle(values: Iterable<Int>) = println("Median: ${values.median()}")

    private fun Iterable<Int>.median(): Double {
        val sorted = sorted()
        val size = sorted.size

        return if (size % 2 == 0) {
            (sorted[size / 2 - 1] + sorted[size / 2]) / 2.0
        } else {
            sorted[size / 2].toDouble()
        }
    }
}

interface Publisher {

    fun subscribe(listener: IntStreamListener)
    fun unsubscribe(listener: IntStreamListener)
    fun notifyListeners()
}

class NumberStream(
    private val source: IntStream,
    private val collectionIntervalMillis: Long = 1000,
) : Publisher {

    private val numbers = mutableListOf<Int>()
    private val listeners = mutableListOf<IntStreamListener>()

    fun start() {
        check(listeners.isNotEmpty()) { "There has to be at least one listener" }

        for (num in source) {
            println("\nReceived: $num")
            numbers.add(num)
            notifyListeners()
            Thread.sleep(collectionIntervalMillis)
        }

        for (listener in listeners) if (listener is AutoCloseable) listener.close()

        println("Stream closed")
    }

    override fun subscribe(listener: IntStreamListener) {
        listeners.add(listener)
    }

    override fun unsubscribe(listener: IntStreamListener) {
        listeners.remove(listener)
    }

    override fun notifyListeners() = listeners.forEach { it.handle(numbers) }
}
