package design.patterns.table


fun main() {
    val s = Sheet(5, 5)
    println()

    s["A1"] = "2"
    s["A2"] = "5"
    s["A3"] = "A1+A2"
    s.print()

    s["A1"] = "4"
    s["A4"] = "A1+A3"
    s.print()

    try {
        s["A1"] = "A3"
    } catch (e: Exception) {
        println("${e.message}\n")
    }

    s["A1"] = "100"
    s.print()
    s["E4"] = "200"
    s["A2"] = "E4"
    s["E2"] = "200+200"
    s["E4"] = "E2"
    s["C2"] = "A1+200+E4"
    s.print()
}


interface Observer {
    fun update()
}

interface Observable {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun updateAll()
}

class Cell(private val sheet: Sheet, val id: String, exp: String = "") : Observer, Observable {

    var exp: String = exp
        set(value) {
            check(!hasCircularRefs(Node(Cell(sheet, id, value)))) { "Circular reference detected" }

            field = value
            this.value = sheet.evaluate(this)

            for (ref in sheet.getRefs(this)) ref.addObserver(this)
            updateAll()
        }

    var value: Int = 0
        private set

    private val observers = mutableSetOf<Observer>()

    override fun update() {
        exp = exp
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun updateAll() = observers.toList().forEach(Observer::update)

    private data class Node(val cell: Cell, val parent: Node? = null)

    private fun hasCircularRefs(node: Node): Boolean {
        var parent = node.parent
        while (parent != null) {
            if (node.cell.id == parent.cell.id) {
                return true
            }
            parent = parent.parent
        }

        return sheet.getRefs(node.cell).any { hasCircularRefs(Node(it, node)) }
    }

    override fun toString(): String = "Cell(id='$id', exp='$exp', value=$value, observers=$observers)"
}

class Sheet(rows: Int, cols: Int) {

    init {
        require(rows > 0 && cols > 0) { "There has to be at least 1 row and column" }
        require(cols <= 'Z' - 'A') { "Only 'A'..'Z' is supported" }
    }

    private val table = Array(rows) { i ->
        Array(cols) { j ->
            Cell(this, "${('A'..'Z').elementAt(j)}${i + 1}")
        }
    }

    operator fun get(ref: String) = cell(ref)

    fun cell(ref: String): Cell {
        val letter = ref[0]
        val number = ref.filter { it.isDigit() }.toInt()
        return table[number][letter - 'A']
    }

    operator fun set(ref: String, content: String) {
        val cell = cell(ref)
        cell.exp = content
    }

    fun getRefs(cell: Cell): List<Cell> =
        Regex("[A-Z]+[0-9]+").findAll(cell.exp).map { cell(it.value) }.toList()

    fun evaluate(cell: Cell): Int {
        if (Regex("[0-9]+[A-Z]+").containsMatchIn(cell.exp)) {
            throw Exception("Invalid cell input")
        }
        return eval(cell)
    }

    private fun eval(cell: Cell): Int {
        if (cell.exp == "") {
            return 0
        }

        val numSum = Regex("\\b\\d+\\b").findAll(cell.exp).sumOf { it.value.toInt() }
        val refSum = getRefs(cell).sumOf(::eval)
        return numSum + refSum
    }

    override fun toString(): String {
        val sb = StringBuilder()

        val size = table[0].size
        sb.append("     +-").append("-----------+".repeat(size)).append('\n')
        sb.append("     | ")
        ('A'..'Z').take(size).forEach {
            sb.append(" ".repeat(9))
                .append(it)
                .append(" |")
        }
        sb.append('\n').append("+----+-").append("-----------+".repeat(size)).append('\n')
        for ((i, row) in table.withIndex()) {
            sb.append("| ").append(String.format("%2d", i)).append(" |")
            for (cell in row) {
                sb.append(' ')
                sb.append(String.format("%10d", cell.value))
                sb.append(' ')
            }
            sb.append('|').append('\n')
        }
        sb.append("+----+-").append("-----------+".repeat(size)).append('\n')

        return sb.toString()
    }

    fun print() = println(this)
}
