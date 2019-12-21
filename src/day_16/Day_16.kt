package day_16

import shared.listFromFile
import kotlin.math.absoluteValue

fun getDigit(input: List<Int>, digit: Int): Int {
    return input.mapIndexed { index, value ->
        when (((index + 1)/digit) % 4) {
            1 -> value
            3 -> -value
            else -> 0
        }
    }.sum().absoluteValue % 10
}

fun fft(input: List<Int>): List<Int> {
    return input.indices.map { index ->
        getDigit(input, index + 1)
    }
}

fun digitsFrom(string: String): List<Int> {
    return string.map { it.toInt() - '0'.toInt() }
}

fun part1() {
    var input = digitsFrom(listFromFile("data/day_16.txt").first())
    for (i in 1..100) {
        input = fft(input)
    }
    println(input.take(8).joinToString(""))
}

fun part2() {
    val input = digitsFrom(listFromFile("data/day_16.txt").first())

    val repeats = 10000
    val offset = input.subList(0, 7).joinToString("").toInt()

    // Only transform the portion of input after the offset
    val inputAfterOffset = MutableList(input.size * repeats - offset) { index -> input[(index + offset) % input.size] }
    for (phase in 1..100) {
        for (i in inputAfterOffset.size - 2 downTo 0) {
            // Since the offset is in the second half of the input sequence, the "pattern" will be all 1s,
            // So every number is simply added (mod 10) with the remaining digits in the sequence.
            inputAfterOffset[i] = (inputAfterOffset[i] + inputAfterOffset[i + 1]) % 10
        }
    }
    println(inputAfterOffset.take(8).joinToString(""))
}

fun main() {
    part1()
    part2()
}