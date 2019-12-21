package day_17

import day_9.IntCodeVM
import java.math.BigInteger

fun isIntersection(ascii: List<String>, row: Int, col: Int): Boolean {
    val aboveIsScaffold = (row != 0 && ascii[row - 1][col] == '#')
    val belowIsScaffold = (row != ascii.size - 1 && ascii[row + 1][col] == '#')
    val leftIsScaffold = (col != 0 && ascii[row][col - 1] == '#')
    val rightIsScaffold = (col != ascii[row].length - 1 && ascii[row][col + 1] == '#')
    return aboveIsScaffold && belowIsScaffold && leftIsScaffold && rightIsScaffold && ascii[row][col] == '#'
}

fun getAlignmentParameters(ascii: List<String>): List<Int> {
    val intersections: MutableList<Pair<Int, Int>> = mutableListOf()
    for (row in ascii.indices) {
        for (col in ascii[row].indices) {
            if (isIntersection(ascii, row, col)) intersections.add(Pair(row, col))
        }
    }
    return intersections.map { it.first * it.second }
}


fun part2() {
    val program = shared.listFromDelimitedFile("data/day_17.txt", ",").map { BigInteger(it) }.toMutableList()
    program[0] = 2.toBigInteger()
    val vm = IntCodeVM(program)

    // Disclaimer: This was built by hand
    val movementProgram = """
        A,B,A,B,C,C,B,A,B,C
        L,12,L,10,R,8,L,12
        R,8,R,10,R,12
        L,10,R,12,R,8
        n
        
    """.trimIndent().map { it.toInt().toBigInteger() }
    vm.inputBuffer.addAll(movementProgram)

    vm.execute()
    println("Dust Collected: ${vm.outputBuffer.last()}")
}

fun part1() {
    val program = shared.listFromDelimitedFile("data/day_17.txt", ",").map { BigInteger(it) }
    val vm = IntCodeVM(program)
    vm.execute()
    val ascii = vm.outputBuffer.map { it.toInt().toChar() }
        .joinToString("")
        .split("\n")
        .filter { it.isNotEmpty() }
    println("Sum of alignment parameters: ${getAlignmentParameters(ascii).sum()}")
}

fun main() {
    part1()
    part2()
}