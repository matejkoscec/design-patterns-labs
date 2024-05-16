package design.patterns.distribution

import kotlin.math.ceil


fun main() {
    DistributionTester(
        SequentialGenerator(1..10),
        method = ::linearInterpolation
    ).test()

    DistributionTester(
        generator = GaussianGenerator(10, stdDev = 100.0),
        method = ::nearestRank
    ).test()

    DistributionTester(
        generator = FibonacciGenerator(10),
        method = ::linearInterpolation
    ).test()
}


interface IntGenerator : Iterable<Int>

class SequentialGenerator(private val range: IntRange, private val step: Int = 1) : IntGenerator {
    override fun iterator() = range.step(step).iterator()
}

class GaussianGenerator(
    private val n: Int,
    private val mean: Double = 0.0,
    private val stdDev: Double = 100.0,
) : IntGenerator {
    private val random = java.util.Random()

    override fun iterator() = object : Iterator<Int> {
        private var i = 0

        override fun hasNext(): Boolean = i < n
        override fun next(): Int = random.nextGaussian(mean, stdDev).toInt().also { i++ }
    }
}

class FibonacciGenerator(private val n: Int) : IntGenerator {
    override fun iterator() = object : Iterator<Int> {
        private var i = 0
        private var a = 0
        private var b = 1

        override fun hasNext(): Boolean = i < n
        override fun next(): Int = a.also { a = b; b += it; i++ }
    }
}

fun interface PercentileMethod {
    fun calculate(values: Iterable<Int>, p: Int): Int
}

fun nearestRank(values: Iterable<Int>, p: Int): Int {
    val sorted = values.sorted()
    val rank = ceil(p / 100.0 * sorted.size).toInt()

    return sorted[rank - 1]
}

fun linearInterpolation(values: Iterable<Int>, p: Int): Int {
    val sortedValues = values.sorted()
    val n = sortedValues.size

    if (p <= getPercentile(1, n)) {
        return sortedValues.first()
    }

    if (p >= getPercentile(n, n)) {
        return sortedValues.last()
    }

    val i = sortedValues.indices.first { getPercentile(it + 2, n) > p }
    val v_i = sortedValues[i].toDouble()
    val v_i1 = sortedValues[i + 1].toDouble()
    val p_i = getPercentile(i + 1, n)

    return (v_i + n * (p - p_i) * (v_i1 - v_i) / 100.0).toInt()
}

fun getPercentile(i: Int, n: Int): Double = 100 * (i - 0.5) / n

class DistributionTester(private val generator: IntGenerator, private val method: PercentileMethod) {

    fun test() {
        val values = generator.toList()
        println("values: $values")
        for (p in 10..90 step 10) {
            println("$p-th percentile: ${method.calculate(values, p)}")
        }
        println()
    }
}
