package day_7

import day_5.Instruction
import java.lang.Integer.parseInt
import kotlin.assert
import kotlin.math.max

fun main() {
    part1()
    part2()
}

fun part1() {
    val program = shared.listFromDelimitedFile("data/day_7.txt").map{parseInt(it)}
    var maxSignal = 0
    getSettingsCombos(0, 4).forEach {
        maxSignal = max(maxSignal, runWithoutFeedbackLoop(program, it))
    }
    println("Part 1: $maxSignal")
}

fun part2() {

    val program = shared.listFromDelimitedFile("data/day_7.txt").map{parseInt(it)}
    var maxSignal = 0
    getSettingsCombos(5, 9).forEach {
        maxSignal = max(maxSignal, runWithFeedbackLoop(program, it))
    }
    println("Part 2: $maxSignal")
}

fun getSettingsCombos(min: Int, max: Int): List<List<Int>> {
    return ((min * 11111)..(max * 11111)).map {
        listOf(
            it / 1 % 10,
            it / 10 % 10,
            it / 100 % 10,
            it / 1000 % 10,
            it / 10000 % 10
        )
    }.filter {
        (min..max).all { digit -> it.contains(digit) }
    }
}

fun runWithFeedbackLoop(program: List<Int>, settings: List<Int>): Int {

    val amplifierMemories: List<MutableList<Int>> = listOf(
        program.toMutableList(),
        program.toMutableList(),
        program.toMutableList(),
        program.toMutableList(),
        program.toMutableList()
    )

    val amplifierStates: MutableList<ProgramState> = mutableListOf(
        ProgramState.RUNNING,
        ProgramState.RUNNING,
        ProgramState.RUNNING,
        ProgramState.RUNNING,
        ProgramState.RUNNING
    )

    val amplifierInputs: List<MutableList<Int>> = listOf(
        mutableListOf(settings[0], 0),
        mutableListOf(settings[1]),
        mutableListOf(settings[2]),
        mutableListOf(settings[3]),
        mutableListOf(settings[4])
    )

    val amplifierIPs: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)

    var currentAmplifier = 0
    var lastOutput = 0
    while (!amplifierStates.all { it == ProgramState.HALTED }) {
        assert(amplifierStates[currentAmplifier] != ProgramState.HALTED)
        amplifierStates[currentAmplifier] = ProgramState.RUNNING

        val output = mutableListOf<Int>()
        amplifierIPs[currentAmplifier] = executeProgram(
            amplifierMemories[currentAmplifier],
            amplifierInputs[currentAmplifier],
            output,
            amplifierIPs[currentAmplifier],
            amplifierStates.subList(currentAmplifier, currentAmplifier + 1)
        )

        assert(output.size == 1) { "Incorrect Number of outputs: ${output.size}" }
        lastOutput = output.first()

        currentAmplifier = (currentAmplifier + 1) % 5
        amplifierInputs[currentAmplifier].add(lastOutput)
    }

    return lastOutput
}

fun runWithoutFeedbackLoop(program: List<Int>, settings: List<Int>): Int {
    var input = 0
    for (i in 0..4) {
        val output = mutableListOf<Int>()
        executeProgram(program.toMutableList(), mutableListOf(settings[i], input), output)
        input = output[0]
    }
    return input
}

enum class ProgramState {
    RUNNING, AWAITING_INPUT, HALTED
}

/**
 * Executes the given program starting at the given instruction pointer.
 *
 * Execution stops when the program halts or when the program is awaiting input.
 *
 * Input will be read and removed from the front of input. If input is
 * requested and there is none available, then this function will return
 * and status will contain ProgramState.AWAITING_INPUT.
 *
 * Output will be appended to the given output list.
 *
 * The return value of this function is the address (memory index) of the
 * next instruction that should be executed.
 */
fun executeProgram(
    program: MutableList<Int>,
    input: MutableList<Int>,
    output: MutableList<Int>,
    instructionPointer: Int = 0,
    status: MutableList<ProgramState> = mutableListOf(ProgramState.RUNNING)
): Int {
    var tempIP = instructionPointer
    while (status[0] == ProgramState.RUNNING) {
        tempIP = executeInstruction(program, tempIP, input, output, status)
    }
    return tempIP
}

/**
 * Executes a single instruction at memory[instructionPointer].
 *
 * Input will be read and removed from the front of input. If input is
 * requested and there is none available, then this function will return
 * and status will contain ProgramState.AWAITING_INPUT.
 *
 * Output will be appended to the given output list.
 *
 * The return value of this function is the address (memory index) of the
 * next instruction that should be executed.
 */
fun executeInstruction(
    memory: MutableList<Int>,
    instructionPointer: Int,
    input: MutableList<Int>,
    output: MutableList<Int>,
    status: MutableList<ProgramState> = mutableListOf(ProgramState.RUNNING)
): Int {
    val inst = Instruction(memory, instructionPointer)

    when (inst.opCode) {
        Instruction.OpCode.SUM -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            val arg1 = if (inst.modes[1] == Instruction.Mode.IMMEDIATE) inst.params[1] else memory[inst.params[1]]
            memory[inst.params[2]] = arg0 + arg1
            return instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.MULTIPLY -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            val arg1 = if (inst.modes[1] == Instruction.Mode.IMMEDIATE) inst.params[1] else memory[inst.params[1]]
            memory[inst.params[2]] = arg0 * arg1
            return instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.INPUT -> {
            return if (input.isEmpty()) {
                status[0] = ProgramState.AWAITING_INPUT
                instructionPointer
            } else {
                memory[inst.params[0]] = input.removeAt(0)
                instructionPointer + inst.opCode.paramCount + 1
            }
        }
        Instruction.OpCode.OUTPUT -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            output.add(arg0)
            return instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.JUMP_IF_TRUE -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            val arg1 = if (inst.modes[1] == Instruction.Mode.IMMEDIATE) inst.params[1] else memory[inst.params[1]]
            return if (arg0 != 0) arg1 else instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.JUMP_IF_FALSE -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            val arg1 = if (inst.modes[1] == Instruction.Mode.IMMEDIATE) inst.params[1] else memory[inst.params[1]]
            return if (arg0 == 0) arg1 else instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.LESS_THAN -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            val arg1 = if (inst.modes[1] == Instruction.Mode.IMMEDIATE) inst.params[1] else memory[inst.params[1]]
            memory[inst.params[2]] = if (arg0 < arg1) 1 else 0
            return instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.EQUAL -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            val arg1 = if (inst.modes[1] == Instruction.Mode.IMMEDIATE) inst.params[1] else memory[inst.params[1]]
            memory[inst.params[2]] = if (arg0 == arg1) 1 else 0
            return instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.HALT -> {
            status[0] = ProgramState.HALTED
            return instructionPointer
        }
        else -> throw Exception("Unrecognized instruction ${memory[instructionPointer]} at index $instructionPointer")
    }
}

