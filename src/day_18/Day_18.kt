package day_18

import day_15.Coordinates
import java.util.*

data class Key(val name: Char, val location: Coordinates)
data class Door(val name: Char, val location: Coordinates)

data class SearchState(val location: Coordinates, val keys: Set<Key>)

class Dungeon(filename: String) {

    val keys: MutableSet<Key> = mutableSetOf()
    private val rows: MutableList<String> = shared.listFromFile(filename).toMutableList()
    private val doors: MutableSet<Door> = mutableSetOf()
    private var originalStart: Coordinates? = null

    init {
        val doorPattern = Regex("""[A-Z]""")
        val keyPattern = Regex("""[a-z]""")

        for (row in this.rows.indices) {
            for (col in this.rows[row].indices) {
                val spot = this.rows[row][col]
                when {
                    doorPattern.matches(spot.toString()) -> {
                        doors.add(Door(spot, Coordinates(col, row)))
                    }
                    keyPattern.matches(spot.toString()) -> {
                        keys.add(Key(spot, Coordinates(col, row)))
                    }
                    spot == '@' -> {
                        originalStart = Coordinates(col, row)
                    }
                }
            }
        }
    }

    fun getStart(): Coordinates {
        for (row in this.rows.indices) {
            for (col in this.rows[row].indices) {
                if (this.rows[row][col] == '@') return Coordinates(col, row)
            }
        }
        error("No start found (@)")
    }

    fun getSuccessors(from: SearchState): Set<Coordinates> {
        val destinations = mutableSetOf<Coordinates>()
        val loc = from.location

        val left = Coordinates(loc.x - 1, loc.y)
        val right = Coordinates(loc.x + 1, loc.y)
        val above = Coordinates(loc.x, loc.y - 1)
        val below = Coordinates(loc.x, loc.y + 1)

        if (this.isDestination(left, from.keys)) destinations.add(left)
        if (this.isDestination(right, from.keys)) destinations.add(right)
        if (this.isDestination(above, from.keys)) destinations.add(above)
        if (this.isDestination(below, from.keys)) destinations.add(below)

        return destinations
    }

    private fun isDestination(location: Coordinates, keys: Set<Key>): Boolean {
        return if (location.x < 0 || location.y < 0 || location.y >= this.rows.size || location.x >= this.rows[location.y].length) {
            false
        } else if (this.get(location) == '#') {
            false
        } else !(this.isDoor(location) && keys.none { it.name.equals(getDoorName(location), ignoreCase = true) })
    }

    fun get(location: Coordinates) = this.rows[location.y][location.x]
    fun isKey(location: Coordinates) = this.keys.any { it.location == location }
    fun getKey(location: Coordinates): Key = this.keys.find { it.location == location }!!

    private fun isDoor(location: Coordinates) = this.doors.any { it.location == location }
    private fun getDoorName(location: Coordinates): Char = this.doors.find { it.location == location }!!.name
}

// (BFS) Returns minimum number of steps to collect all keys
fun collectKeys(dungeon: Dungeon, start: SearchState, keys: Set<Key>): Int {

    val parents: MutableMap<SearchState, SearchState> = mutableMapOf()
    val fringe: ArrayDeque<SearchState> = ArrayDeque()

    var found = false

    fringe.add(start)
    while (!found) {
        val current = fringe.removeFirst()
        val destinations = dungeon.getSuccessors(current)
        val children = destinations.map { dest ->
            val newKeys = if (dungeon.isKey(dest)) {
                if (current.keys + dungeon.getKey(dest) == keys)
                    found = true
                current.keys + dungeon.getKey(dest)
            } else {
                current.keys
            }
            SearchState(dest, newKeys)
        }.filterNot { parents.contains(it) }
        children.forEach { parents[it] = current }
        fringe.addAll(children)
    }

    var current = parents.keys.find { it.keys.size == keys.size }!!
    val path = mutableListOf<Coordinates>()
    while (current != start) {
        path.add(0, current.location)
        current = parents[current]!!
    }

    return path.size
}

fun part1() {
    val dungeon = Dungeon("data/day_18.txt")
    val start = SearchState(dungeon.getStart(), setOf())

    val minSteps = collectKeys(dungeon, start, dungeon.keys.toSet())
    println(minSteps)
}

fun main() {
    part1()
}