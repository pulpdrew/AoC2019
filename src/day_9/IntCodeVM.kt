package day_9

import java.math.BigInteger

fun main() {
    val program = shared.listFromDelimitedFile("data/day_9.txt").map{ BigInteger(it) }

    val vm = IntCodeVM(program)
    vm.inputBuffer.add(1.toBigInteger())
    vm.execute()
    println(vm.outputBuffer)

    val vm2 = IntCodeVM(program)
    vm2.inputBuffer.add(2.toBigInteger())
    vm2.execute()
    println(vm2.outputBuffer)
}

class IntCodeVM(program: List<BigInteger>) {

    private val memory: MutableList<BigInteger> = MutableList(program.size * 10) { 0.toBigInteger() }
    var state: IntCodeVMState = IntCodeVMState.RUNNING
    private var instructionPointer: Int = 0
    private var base: BigInteger = 0.toBigInteger()
    val inputBuffer: MutableList<BigInteger> = mutableListOf()
    val outputBuffer: MutableList<BigInteger> = mutableListOf()

    init {
        program.forEachIndexed { i, value -> memory[i] = value }
    }

    fun execute() {
        this.state = IntCodeVMState.RUNNING
        while (this.state == IntCodeVMState.RUNNING) {
            executeInstruction()
        }
    }

    private fun getParamValue(param: BigInteger, memory: List<BigInteger>, mode: IntCodeInst.Mode): BigInteger {
        return when (mode) {
            IntCodeInst.Mode.IMMEDIATE -> param
            IntCodeInst.Mode.POSITION ->  memory[param.toInt()]
            IntCodeInst.Mode.RELATIVE -> memory[(param + this.base).toInt()]
        }
    }

    private fun executeInstruction() {
        val inst = IntCodeInst(this.memory, this.instructionPointer)

        when (inst.opCode) {
            IntCodeInst.OpCode.SUM -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                val arg1 = getParamValue(inst.params[1], memory, inst.modes[1])
                val dest = (inst.params[2] + if (inst.modes[2] == IntCodeInst.Mode.RELATIVE) this.base else 0.toBigInteger()).toInt()
                this.memory[dest] = arg0 + arg1
                this.instructionPointer += inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.MULTIPLY -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                val arg1 = getParamValue(inst.params[1], memory, inst.modes[1])
                val dest = (inst.params[2] + if (inst.modes[2] == IntCodeInst.Mode.RELATIVE) this.base else 0.toBigInteger()).toInt()
                this.memory[dest] = arg0 * arg1
                this.instructionPointer += inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.INPUT -> {
                if (this.inputBuffer.isEmpty()) {
                    this.state = IntCodeVMState.AWAITING_INPUT
                } else {
                    val dest = (inst.params[0] + if (inst.modes[0] == IntCodeInst.Mode.RELATIVE) this.base else 0.toBigInteger()).toInt()
                    this.memory[dest] = this.inputBuffer.removeAt(0)
                    this.instructionPointer += inst.opCode.paramCount + 1
                }
            }
            IntCodeInst.OpCode.OUTPUT -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                this.outputBuffer.add(arg0)
                this.instructionPointer += inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.JUMP_IF_TRUE -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                val arg1 = getParamValue(inst.params[1], memory, inst.modes[1])
                this.instructionPointer = if (arg0 != 0.toBigInteger()) arg1.toInt() else this.instructionPointer + inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.JUMP_IF_FALSE -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                val arg1 = getParamValue(inst.params[1], memory, inst.modes[1])
                this.instructionPointer = if (arg0 == 0.toBigInteger()) arg1.toInt() else this.instructionPointer + inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.LESS_THAN -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                val arg1 = getParamValue(inst.params[1], memory, inst.modes[1])
                val dest = (inst.params[2] + if (inst.modes[2] == IntCodeInst.Mode.RELATIVE) this.base else 0.toBigInteger()).toInt()
                this.memory[dest] = if (arg0 < arg1) 1.toBigInteger() else 0.toBigInteger()
                this.instructionPointer += inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.EQUAL -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                val arg1 = getParamValue(inst.params[1], memory, inst.modes[1])
                val dest = (inst.params[2] + if (inst.modes[2] == IntCodeInst.Mode.RELATIVE) this.base else 0.toBigInteger()).toInt()
                this.memory[dest] = if (arg0 == arg1) 1.toBigInteger() else 0.toBigInteger()
                this.instructionPointer += inst.opCode.paramCount + 1
            }
            IntCodeInst.OpCode.HALT -> {
                this.state = IntCodeVMState.HALTED
            }
            IntCodeInst.OpCode.ADD_BASE -> {
                val arg0 = getParamValue(inst.params[0], memory, inst.modes[0])
                this.base += arg0
                this.instructionPointer += inst.opCode.paramCount + 1
            }
        }
    }
}

enum class IntCodeVMState {
    RUNNING, AWAITING_INPUT, HALTED
}

class IntCodeInst(memory: List<BigInteger>, instructionPointer: Int) {

    val opCode: OpCode
    val params: List<BigInteger>
    val modes: List<Mode>

    init {
        opCode = getOpCode(memory[instructionPointer])
        modes = getModes(memory[instructionPointer])
        params = getParameters(memory, instructionPointer, opCode)
    }

    enum class Mode(val code: Int) {
        IMMEDIATE(1),
        POSITION(0),
        RELATIVE(2)
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
        ADD_BASE(9, 1),
    }

    private fun getOpCode(memValue: BigInteger): OpCode = OpCode.values().find { it.code == (memValue.toInt() % 100) } ?: OpCode.HALT

    private fun getModes(memValue: BigInteger): List<Mode> = listOf(
        Mode.values().find { it.code == (memValue.toInt() / 100) % 10 } ?: Mode.IMMEDIATE,
        Mode.values().find { it.code == (memValue.toInt() / 1000) % 10 } ?: Mode.IMMEDIATE,
        Mode.values().find { it.code == (memValue.toInt() / 10000) % 10 } ?: Mode.IMMEDIATE
    )

    private fun getParameters(memory: List<BigInteger>, instructionPointer: Int, opCode: OpCode): List<BigInteger> {
        return memory.slice((instructionPointer + 1)..(instructionPointer + opCode.paramCount))
    }
}