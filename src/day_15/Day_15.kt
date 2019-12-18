package day_15

import day_9.IntCodeVM
import java.lang.Exception
import java.math.BigInteger

data class Coordinates(val x: Int, val y: Int)
data class LocationInfo(var parent: Coordinates, var distance: Int, var isOxygen: Boolean = false)

class Droid(program: List<BigInteger>) {

    enum class Direction(val code: Int) { NORTH(1), SOUTH(2), WEST(3), EAST(4) }

    enum class Status(val code: Int) { WALL(0), SUCCESS(1), OXYGEN(2) }
    private fun BigInteger.toStatus(): Status = Status.values().find { it.code == this.toInt()}!!

    private val vm: IntCodeVM = IntCodeVM(program)
    var coords: Coordinates = Coordinates(0, 0)
    private val history: MutableList<Coordinates> = mutableListOf()

    fun move(direction: Direction, keepHistory: Boolean = true): Status {
        vm.inputBuffer.add(direction.code.toBigInteger())
        vm.execute()
        val status = vm.outputBuffer.removeAt(0).toStatus()

        if (status != Status.WALL) {
            if (keepHistory) this.history.add(this.coords)
            this.coords = when (direction) {
                Direction.NORTH -> Coordinates(this.coords.x, this.coords.y + 1)
                Direction.SOUTH -> Coordinates(this.coords.x, this.coords.y - 1)
                Direction.EAST -> Coordinates(this.coords.x + 1, this.coords.y)
                Direction.WEST -> Coordinates(this.coords.x - 1, this.coords.y)
            }
        }

        return status
    }

    fun backtrack(): Status {
        if (history.isEmpty()) return Status.SUCCESS

        val direction: Direction = getDirection(this.coords, this.history.removeAt(this.history.size - 1))
        return this.move(direction, false)
    }

    fun previousCoords(): Coordinates = if (this.history.isEmpty()) Coordinates(0, 0) else this.history.last()
}

fun isCompletelyExplored(unexplored: MutableMap<Coordinates, MutableList<Droid.Direction>>) = unexplored.none { it.value.isNotEmpty() }

fun getDirection(from: Coordinates, to: Coordinates): Droid.Direction {
    if (from.x > to.x) return Droid.Direction.WEST
    if (from.x < to.x) return Droid.Direction.EAST
    if (from.y > to.y) return Droid.Direction.SOUTH
    if (from.y < to.y) return Droid.Direction.NORTH
    throw Exception("Error: from == to")
}

fun buildMap(program: List<BigInteger>): Map<Coordinates, LocationInfo> {

    val droid = Droid(program)
    val map: MutableMap<Coordinates, LocationInfo> = mutableMapOf()

    val unexploredDirections: MutableMap<Coordinates, MutableList<Droid.Direction>> = mutableMapOf()
    unexploredDirections[droid.coords] = Droid.Direction.values().toMutableList()

    // Explore until every direction has been tried from every reachable place
    while (!isCompletelyExplored(unexploredDirections)) {

        if (unexploredDirections[droid.coords]!!.isEmpty()) {
            droid.backtrack()
        } else {

            // Move in a direction that hasn't been tried yet (from this location)
            val direction = unexploredDirections[droid.coords]!!.removeAt(0)
            val status = droid.move(direction)

            // if a wall was hit, explore from here again
            if (status == Droid.Status.WALL) continue

            if (map.containsKey(droid.coords)) {

                // If we've already been here, update parents if this is a better route
                val stepsToHere = map[droid.previousCoords()]!!.distance + 1
                if (stepsToHere < map[droid.coords]!!.distance) {
                    map[droid.coords]!!.parent = droid.previousCoords()
                    map[droid.coords]!!.distance = stepsToHere
                }

            } else {

                // Otherwise, this is new and we should explore from here after setting a parent
                val stepsToHere = (map[droid.previousCoords()]?.distance ?: 0) + 1
                map[droid.coords] = LocationInfo(droid.previousCoords(), stepsToHere, status == Droid.Status.OXYGEN)

                unexploredDirections[droid.coords] = Droid.Direction.values().toMutableList()
                unexploredDirections[droid.coords]!!.remove(getDirection(droid.coords, droid.previousCoords()))
            }
        }
    }

    return map
}

fun stepsToFloodFill(map: Map<Coordinates, LocationInfo>): Int {

    val oxygenated: MutableMap<Coordinates, Boolean> = map.mapValues { false }.toMutableMap()
    val oxygen = map.filter { it.value.isOxygen }.keys.take(1).first()
    oxygenated[oxygen] = true

    var minute = 0
    while (oxygenated.any { !it.value }) {

        val filled = oxygenated.filter { it.value }.keys
        val neighbors = oxygenated.keys.filter {
            filled.contains(Coordinates(it.x, it.y + 1)) ||
                    filled.contains(Coordinates(it.x, it.y - 1)) ||
                    filled.contains(Coordinates(it.x - 1, it.y)) ||
                    filled.contains(Coordinates(it.x + 1, it.y))
        }
        neighbors.forEach { oxygenated[it] = true }

        minute++
    }

    return minute
}

fun main() {
    val program: List<BigInteger> = shared.listFromDelimitedFile("data/day_15.txt").map { BigInteger(it) }
    val map = buildMap(program)

    val oxygen = map.filter { it.value.isOxygen }.keys.take(1).first()
    println("Distance to oxygen: ${map[oxygen]?.distance ?: "Not Found"}")
    println("Minutes to fill: ${stepsToFloodFill(map)}")
}