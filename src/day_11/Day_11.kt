package day_11

import day_9.IntCodeVM
import day_9.IntCodeVMState
import java.math.BigInteger



class HullPainter(program: List<BigInteger>) {

    enum class Direction { UP, RIGHT, DOWN, LEFT }
    enum class Color { BLACK, WHITE }

    private val vm: IntCodeVM = IntCodeVM(program)
    val hullColors: MutableMap<Pair<Int, Int>, Color> = mutableMapOf()
    val painted: MutableSet<Pair<Int, Int>> = mutableSetOf()

    private var coordinates: Pair<Int, Int> = Pair(0, 0)
    private var direction: Direction = Direction.UP

    fun paintHull() {

        // Setup for part 2 (remove for part 1)
        setHullColor(Pair(0, 0), Color.WHITE)

        while (this.vm.state != IntCodeVMState.HALTED) {
            this.vm.execute()

            while (this.vm.outputBuffer.isNotEmpty()) {
                parseVMOutput()
            }

            if (this.vm.state == IntCodeVMState.AWAITING_INPUT) {
                val input = (if (this.getHullColor(this.coordinates) == Color.WHITE) 1 else 0).toBigInteger()
                this.vm.inputBuffer.add(input)
            }
        }

        while (this.vm.outputBuffer.isNotEmpty()) {
            parseVMOutput()
        }
    }

    private fun parseVMOutput() {
        val color: Color = if (vm.outputBuffer.removeAt(0).toInt() == 0) Color.BLACK else Color.WHITE
        val turn: Int = vm.outputBuffer.removeAt(0).toInt()

        setHullColor(this.coordinates, color)
        this.turn(turn)
        this.move()
    }


    private fun getHullColor(coords: Pair<Int, Int>): Color = hullColors.getOrDefault(coords, Color.BLACK)

    private fun setHullColor(coords: Pair<Int, Int>, color: Color) {
        hullColors[coords] = color
        painted.add(coords)
    }

    private fun turn(turn: Int) {
        val directions = listOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        val curDirectionIndex = directions.indexOf(this.direction)
        val directionAdjustment = if (turn == 0) 3 else 1
        this.direction = directions[(curDirectionIndex + directionAdjustment) % 4]
    }

    private fun move() {
        this.coordinates = when (this.direction) {
            Direction.UP -> Pair(this.coordinates.first, this.coordinates.second + 1)
            Direction.DOWN -> Pair(this.coordinates.first, this.coordinates.second - 1)
            Direction.LEFT -> Pair(this.coordinates.first - 1, this.coordinates.second)
            Direction.RIGHT -> Pair(this.coordinates.first + 1, this.coordinates.second)
        }
    }
}

fun printHull(hull: Map<Pair<Int, Int>, HullPainter.Color>) {

    val minX: Int = hull.minBy { it.key.first }!!.key.first
    val maxX: Int = hull.maxBy { it.key.first }!!.key.first
    val minY: Int = hull.minBy { it.key.second }!!.key.second
    val maxY: Int = hull.maxBy { it.key.second }!!.key.second

    for (row in maxY downTo minY) {
        for (col in minX..maxX) {
            if (hull.getOrDefault(Pair(col, row), HullPainter.Color.BLACK) == HullPainter.Color.WHITE) {
                print("#")
            } else {
                print(" ")
            }
        }
        println()
    }
}

fun main() {
    val program = shared.listFromDelimitedFile("data/day_11.txt").map{ BigInteger(it) }
    val painter = HullPainter(program)
    painter.paintHull()

    val totalPainted = painter.painted.size
    println("Total painted (Part 1): $totalPainted")

    printHull(painter.hullColors)
}