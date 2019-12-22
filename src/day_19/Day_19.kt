package day_19

import day_9.IntCodeVM
import java.math.BigInteger

val program = shared.listFromDelimitedFile("data/day_19.txt").map { BigInteger(it) }

fun isTractor(x: Int, y: Int): Boolean {
    val vm = IntCodeVM(program)
    vm.inputBuffer.add(x.toBigInteger())
    vm.inputBuffer.add(y.toBigInteger())
    vm.execute()
    return vm.outputBuffer.first().toInt() == 1
}

fun beamFitsSquare(x: Int, y: Int, size: Int) = isTractor(
    x,
    y + size - 1
) && isTractor(x + size - 1, y)

fun beamStart(y: Int): Int {
    var xStart = 0
    while (!isTractor(xStart, y)) { xStart++ }
    return xStart
}

fun beamEnd(y: Int, start: Int): Int {
    var xEnd = start
    while (isTractor(xEnd, y)) { xEnd++ }
    return xEnd
}

fun beamWidth(y: Int): Int {
    val start = beamStart(y)
    val end = beamEnd(y, start)
    return end - start
}

fun part1() {
    val locationsAffected = (0..49).map { x -> (0..49).map { y -> isTractor(x, y) } }.flatten().count { it }
    println("Locations Affected: $locationsAffected")
}

fun part2() {

    // Find the first row for which the beam is at least 100 wide
    var y = 100
    while(beamWidth(y) < 100) { y += 10}

    // Find out how much more width is needed due to the beams slope
    val additional = beamStart(y + 99) - beamStart(y)

    // Find the first row where the beam is at least that wide
    while(beamWidth(y) < 100 + additional) { y += 10}

    // Find the x position at which the ship should be
    var x = beamStart(y + 99)

    // Adjust while possible
    while (true) {
        if (beamFitsSquare(x - 1, y - 1, 100)) {
            y--
            x--
        } else if (beamFitsSquare(x - 1, y, 100)) {
            x --
        } else if (beamFitsSquare(x, y - 1, 100)) {
            y--
        } else break
    }

    println("Part 2: ($x, $y) -> ${10000*x + y}")
}

fun main() {
    part1()
    part2()
}