package design.patterns.composite

import design.patterns.composite.v1.parseExpression
import design.patterns.composite.v1.symbols

fun main() {
    val tree = parseExpression("6*(x+4)/2-3-x")
    println(tree)

    try {
        tree.evaluate()
    } catch (e: Exception) {
        println(e.message)
    }

    symbols["x"] = 5.0
    println(tree.evaluate())
    symbols["x"] = 4.0
    println(tree.evaluate())
}
