package day_25

import day_9.IntCodeVM
import day_9.IntCodeVMState
import java.math.BigInteger

val allItems = listOf(
    "hologram",
    "shell",
    "fuel cell",
    "fixed point",
    "polygon",
    "antenna",
    "candy cane"
)

fun tryCombo(combo: Int): List<BigInteger> {
    val pickup = allItems.mapIndexed { index, item ->
        if (combo.shr(index) % 2 == 1) {
            "take $item\n"
        } else {
            "drop $item\n"
        }
    }.joinToString("")

    return (pickup + "west\n").map { it.toInt().toBigInteger() }
}

fun main() {
    val program = shared.listFromDelimitedFile("data/day_25.txt").map { BigInteger(it) }
    val vm = IntCodeVM(program)

    // This results from manual exploration
    val collectItems = """
            south
            take fixed point
            north
            west
            west
            west
            take hologram
            east
            east
            east
            north
            take candy cane
            west
            take antenna
            west
            take shell
            east
            east
            north
            north
            take polygon
            south
            west
            take fuel cell
            west
            
        """.trimIndent().map { it.toInt().toBigInteger() }

    // Collect all of the items and move to the pressure-sensitive lock
    vm.inputBuffer.addAll(collectItems)
    vm.execute()
    println(vm.outputBuffer.map { it.toInt().toChar() }.joinToString(""))
    vm.outputBuffer.clear()

    // keep trying all combinations of items until the door is unlocked
    var comboIndex = 0
    while (vm.state != IntCodeVMState.HALTED) {
        vm.inputBuffer.addAll(tryCombo(comboIndex++))
        vm.execute()
    }

    // Print the output to read the key
    println(vm.outputBuffer.map { it.toInt().toChar() }.joinToString(""))
}