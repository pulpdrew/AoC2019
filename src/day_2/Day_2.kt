package day_2

import java.lang.Integer.parseInt

fun main() {
    part1()
    part2()
}

fun part1() {
    val program = shared.listFromDelimitedFile("data/day_2.txt").map { parseInt(it) }.toMutableList()
    program[1] = 12
    program[2] = 2
    runProgram(program)
    println("program[0] = ${program[0]}")
}

fun part2() {
    val program = shared.listFromDelimitedFile("data/day_2.txt").map { parseInt(it) }
    print("100 * noun + verb = ${findNounAndVerb(program, 19690720)}")
}

fun findNounAndVerb(program: List<Int>, output: Int): Int {
    for (noun in 0..99) {
        for (verb in 0..99) {
            val memory = program.toMutableList()
            memory[1] = noun
            memory[2] = verb
            runProgram(memory)
            if (memory[0] == output) return 100 * noun + verb
        }
    }
    throw Exception("No noun + verb combo produces the desired output")
}

fun runProgram(program: MutableList<Int>) {
    var opIndex = 0
    while (program[opIndex] != 99) {
        opIndex = performOperation(program, opIndex)
    }
}

fun performOperation(program: MutableList<Int>, opIndex: Int): Int {
    val op1 = program[opIndex + 1]
    val op2 = program[opIndex + 2]
    val dest = program[opIndex + 3]

    return when (program[opIndex]) {
        1 -> {
            program[dest] = program[op1] + program[op2]
            return opIndex + 4
        }
        2 -> {
            program[dest] = program[op1] * program[op2]
            return opIndex + 4
        }
        else -> -1
    }
}