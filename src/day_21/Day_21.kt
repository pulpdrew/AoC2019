package day_21

import day_9.IntCodeVM
import java.math.BigInteger

fun part1() {
    val program = shared.listFromDelimitedFile("data/day_21.txt").map { BigInteger(it) }

    // !A || (D && !(B || C))
    val springScript = """
        NOT B T
        NOT T T
        AND C T
        NOT T J
        AND D J
        NOT A T
        OR T J
        WALK
        
    """.trimIndent().map { it.toInt().toBigInteger() }

    val vm = IntCodeVM(program)
    vm.inputBuffer.addAll(springScript)
    vm.execute()
    println(vm.outputBuffer.last())
}

fun part2() {
    val program = shared.listFromDelimitedFile("data/day_21.txt").map { BigInteger(it) }

    // !A || (D && (H || E) && !(B && C))
    val springScript = """
        NOT B T
        NOT T T
        AND C T
        NOT T J
        NOT E T
        NOT T T
        OR H T
        AND T J
        AND D J
        NOT A T
        OR T J
        RUN
        
    """.trimIndent().map { it.toInt().toBigInteger() }

    val vm = IntCodeVM(program)
    vm.inputBuffer.addAll(springScript)
    vm.execute()
    println(vm.outputBuffer.last())
}

fun main() {
    part1()
    part2()
}