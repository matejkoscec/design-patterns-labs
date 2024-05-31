package design.patterns.composite.v1


val symbols = mutableMapOf<String, Double>()

fun parseExpression(strInput: String): Expression {
    for (operator in arrayOf("+-", "*/")) {
        var depth = 0
        for (p in strInput.indices.reversed()) {
            val c = strInput[p]
            if (c == ')') {
                depth += 1
            } else if (c == '(') {
                depth -= 1
            } else if (depth == 0 && c in operator) {
                val left = parseExpression(strInput.substring(0..<p))
                val right = parseExpression(strInput.substring(p + 1..<strInput.length))
                return when (c) {
                    '+' -> Sum(left, right)
                    '-' -> Subtract(left, right)
                    '*' -> Multiply(left, right)
                    '/' -> Divide(left, right)
                    else -> TODO()
                }
            }
        }
    }
    val trimmed = strInput.trim()
    if (trimmed[0] == '(') {
        return parseExpression(trimmed.substring(1..<trimmed.length - 1))
    }

    val value = trimmed.toDoubleOrNull() ?: return Variable(trimmed)

    return Number(value)
}
