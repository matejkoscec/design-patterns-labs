package design.patterns.composite.v1

interface Expression {
    fun evaluate(): Double
    override fun toString(): String
}

class Sum(private val left: Expression, private val right: Expression) : Expression {
    override fun evaluate(): Double = left.evaluate() + right.evaluate()
    override fun toString(): String = "($left + $right)"
}

class Subtract(private val left: Expression, private val right: Expression) : Expression {
    override fun evaluate(): Double = left.evaluate() - right.evaluate()
    override fun toString(): String = "($left - $right)"
}

class Multiply(private val left: Expression, private val right: Expression) : Expression {
    override fun evaluate(): Double = left.evaluate() * right.evaluate()
    override fun toString(): String = "($left * $right)"
}

class Divide(private val left: Expression, private val right: Expression) : Expression {
    override fun evaluate(): Double = left.evaluate() / right.evaluate()
    override fun toString(): String = "($left / $right)"
}

class Number(private val value: Double) : Expression {
    override fun evaluate(): Double = value
    override fun toString(): String = value.toString()
}

class Variable(private val symbol: String) : Expression {
    override fun evaluate(): Double = symbols[symbol] ?: throw Exception("Symbol not found")
    override fun toString(): String = symbol
}
