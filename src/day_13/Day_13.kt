package day_13

import day_9.IntCodeVM
import day_9.IntCodeVMState
import java.math.BigInteger

enum class Tile(val code: Int) {
    EMPTY(0), WALL(1), BLOCK(2), PADDLE(3), BALL(4)
}

fun tileFromCode(code: Int): Tile = Tile.values().find { it.code == code }!!
fun handleOutput(output: MutableList<BigInteger>, screen: MutableMap<Pair<Int, Int>, Tile>) {
    val x = output.removeAt(0).toInt()
    val y = output.removeAt(0).toInt()

    if (x == -1 && y == 0) {
        println("Score: ${output.removeAt(0)}")
    } else {
        val tile = tileFromCode(output.removeAt(0).toInt())
        screen[Pair(x, y)] = tile
    }
}

fun printScreen(screen: Map<Pair<Int, Int>, Tile>) {
    val minX: Int = screen.minBy { it.key.first }!!.key.first
    val maxX: Int = screen.maxBy { it.key.first }!!.key.first
    val minY: Int = screen.minBy { it.key.second }!!.key.second
    val maxY: Int = screen.maxBy { it.key.second }!!.key.second

    for (row in maxY downTo minY) {
        for (col in minX..maxX) {
            when (screen.getOrDefault(Pair(col, row), Tile.EMPTY)) {
                Tile.EMPTY -> print(" ")
                Tile.WALL -> print("#")
                Tile.BLOCK -> print("X")
                Tile.PADDLE -> print("_")
                Tile.BALL -> print("O")
            }
        }
        println()
    }
}

fun part1() {
    val program = shared.listFromDelimitedFile("data/day_13.txt").map{ BigInteger(it) }
    val screen: MutableMap<Pair<Int, Int>, Tile> = mutableMapOf()
    val vm = IntCodeVM(program)
    vm.execute()
    while(vm.outputBuffer.isNotEmpty()) handleOutput(vm.outputBuffer, screen)
    val countOfBlocks = screen.count { it.value == Tile.BLOCK }
    println("Count of Blocks: $countOfBlocks")
}

fun part2() {
    val program = shared.listFromDelimitedFile("data/day_13.txt").map{ BigInteger(it) }.toMutableList()
    program[0] = 2.toBigInteger()
    val screen: MutableMap<Pair<Int, Int>, Tile> = mutableMapOf()
    val vm = IntCodeVM(program)

    // Play the game autonomously
    while (vm.state != IntCodeVMState.HALTED) {
        vm.execute()
        while(vm.outputBuffer.isNotEmpty()) handleOutput(vm.outputBuffer, screen)
        val ballCoords: Pair<Int, Int> = screen.filterValues { it == Tile.BALL }.keys.take(1).first()
        val paddleCoords: Pair<Int, Int> = screen.filterValues { it == Tile.PADDLE }.keys.take(1).first()
        vm.inputBuffer.add((ballCoords.first - paddleCoords.first).toBigInteger())
    }
}

fun main() {
    part1()
    part2()
}