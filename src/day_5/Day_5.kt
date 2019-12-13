package day_5

class Instruction(memory: List<Int>, instructionPointer: Int) {

    val opCode: OpCode
    val params: List<Int>
    val modes: List<Mode>

    init {
        opCode = getOpCode(memory[instructionPointer])
        modes = getModes(memory[instructionPointer])
        params = getParameters(memory, instructionPointer, opCode)
    }

    enum class Mode {
        IMMEDIATE,
        POSITION
    }

    enum class OpCode(val code: Int, val paramCount: Int) {
        SUM(1, 3),
        MULTIPLY(2, 3),
        HALT(99, 0),
        INPUT(3, 1),
        OUTPUT(4, 1),
        JUMP_IF_TRUE(5, 2),
        JUMP_IF_FALSE(6, 2),
        LESS_THAN(7, 3),
        EQUAL(8, 3),
    }

    private fun getOpCode(memValue: Int): OpCode = OpCode.values().find { it.code == (memValue % 100) } ?: OpCode.HALT

    private fun getModes(memValue: Int): List<Mode> = listOf(
        if ((memValue / 100) % 10 == 0) Mode.POSITION else Mode.IMMEDIATE,
        if ((memValue / 1000) % 10 == 0) Mode.POSITION else Mode.IMMEDIATE,
        if ((memValue / 10000) % 10 == 0) Mode.POSITION else Mode.IMMEDIATE
    )

    private fun getParameters(memory: List<Int>, instructionPointer: Int, opCode: OpCode): List<Int> {
        return memory.slice((instructionPointer + 1)..(instructionPointer + opCode.paramCount))
    }
}

fun executeInstruction(memory: MutableList<Int>, instructionPointer: Int): Int {
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
            print("Input Requested: ")
            val input = readLine()!!.toInt()
            memory[inst.params[0]] = input
            return instructionPointer + inst.opCode.paramCount + 1
        }
        Instruction.OpCode.OUTPUT -> {
            val arg0 = if (inst.modes[0] == Instruction.Mode.IMMEDIATE) inst.params[0] else memory[inst.params[0]]
            println(arg0)
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
        else -> throw Exception("Unrecognized instruction ${memory[instructionPointer]} at index $instructionPointer")
    }
}

fun runProgram(program: MutableList<Int>) {
    var instructionPointer = 0
    while (program[instructionPointer] != Instruction.OpCode.HALT.code) {
        instructionPointer = executeInstruction(program, instructionPointer)
    }
}

fun main() {
    val program = shared.listFromDelimitedFile("data/day_5.txt").map { Integer.parseInt(it) }.toMutableList()
    runProgram(program)
    println("Done.")
}