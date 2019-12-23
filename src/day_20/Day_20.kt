package day_20

import day_15.Coordinates
import java.util.*

data class Vec3(val x: Int, val y: Int, val z: Int) {
    fun xy(): Coordinates = Coordinates(this.x, this.y)
}

data class Portal(val name: String, var outer: Coordinates? = null, var inner: Coordinates? = null) {
    fun addLocation(location: Coordinates, isOuter: Boolean) {
        if (isOuter) {
            this.outer = location
        } else {
            this.inner = location
        }
    }

    fun merge(other: Portal): Portal {
        if (this.outer == null) this.outer = other.outer
        if (this.inner == null) this.inner = other.inner
        return this
    }
}

fun readMaze(): List<String> {
    val rows = shared.listFromFile("data/day_20.txt")
    val maxLineLength = rows.maxBy { it.length }!!.length
    return rows.map {
        it + List(maxLineLength - it.length) { ' ' }.joinToString("")
    }
}

fun extractPortals(row: String, y: Int): Set<Portal> {
    val portals = mutableSetOf<Portal>()
    val portalPattern = Regex("""([A-Z][A-Z]\.)|(\.[A-Z][A-Z])""")
    val matches = portalPattern.findAll(row)
    for (match in matches) {
        val x = match.range.first + match.groupValues.first().indexOf(".")
        val portalName = match.groupValues.first().replace(".", "")
        val isOuter = (x == 2) || (x == row.length - 3)
        if (portals.find { it.name == portalName } != null) {
            portals.find { it.name == portalName }!!.addLocation(Coordinates(x, y), isOuter)
        } else {
            val portal = Portal(portalName)
            portal.addLocation(Coordinates(x, y), isOuter)
            portals.add(portal)
        }
    }
    return portals
}

fun List<String>.transpose(): List<String> {
    if (this.isEmpty()) return listOf()
    return List(this[0].length) { row ->
        List(this.size) { col ->
            this[col][row]
        }.joinToString("")
    }
}

fun Coordinates.transpose(): Coordinates {
    return Coordinates(this.y, this.x)
}

fun getPortalLocations(maze: List<String>): Set<Portal> {
    val portals: MutableMap<String, Portal> = mutableMapOf()
    maze.forEachIndexed { index, row ->
        extractPortals(row, index)
            .forEach { portal ->
                if (portals.containsKey(portal.name)) {
                    portals[portal.name]!!.merge(portal)
                } else {
                    portals[portal.name] = portal
                }
            }
    }
    maze.transpose().forEachIndexed { index, row ->
        extractPortals(row, index)
            .map { portal ->
                val inner = portal.inner?.transpose()
                val outer = portal.outer?.transpose()
                Portal(portal.name, outer, inner)
            }
            .forEach { portal ->
                if (portals.containsKey(portal.name)) {
                    portals[portal.name]!!.merge(portal)
                } else {
                    portals[portal.name] = portal
                }
            }
    }
    return portals.values.toSet()
}

fun getDestinations(maze: List<String>, from: Coordinates, portals: Set<Portal>): Set<Coordinates> {
    val destinations: MutableSet<Coordinates> = mutableSetOf()

    if (from.y > 0 && maze[from.y - 1][from.x] == '.') destinations.add(Coordinates(from.x, from.y - 1))
    if (from.y < maze.size - 1 && maze[from.y + 1][from.x] == '.') destinations.add(Coordinates(from.x, from.y + 1))
    if (from.x > 0 && maze[from.y][from.x - 1] == '.') destinations.add(Coordinates(from.x - 1, from.y))
    if (from.x < maze[from.y].length - 1 && maze[from.y][from.x + 1] == '.') destinations.add(Coordinates(from.x + 1, from.y))
    portals.filter { (it.inner == from || it.outer == from) && it.name != "AA" && it.name != "ZZ"}
        .forEach { destinations.add(if (it.inner == from) it.outer!! else it.inner!!) }

    return destinations
}

fun getDestinations2(maze: List<String>, from: Vec3, portals: Set<Portal>): Set<Vec3> {
    val destinations: MutableSet<Vec3> = mutableSetOf()

    if (from.y > 0 && maze[from.y - 1][from.x] == '.') destinations.add(Vec3(from.x, from.y - 1, from.z))
    if (from.y < maze.size - 1 && maze[from.y + 1][from.x] == '.') destinations.add(Vec3(from.x, from.y + 1, from.z))
    if (from.x > 0 && maze[from.y][from.x - 1] == '.') destinations.add(Vec3(from.x - 1, from.y, from.z))
    if (from.x < maze[from.y].length - 1 && maze[from.y][from.x + 1] == '.') destinations.add(Vec3(from.x + 1, from.y, from.z))
    portals.filter { (it.inner == from.xy() || it.outer == from.xy()) && it.name != "AA" && it.name != "ZZ"}
        .forEach {
            if (it.inner == from.xy()) {
                destinations.add(Vec3(it.outer!!.x, it.outer!!.y, from.z - 1))
            } else if (it.outer == from.xy() && from.z < 0) {
                destinations.add(Vec3(it.inner!!.x, it.inner!!.y, from.z + 1))
            }
        }

    return destinations
}

fun getMazeGraph(maze: List<String>): Map<Coordinates, Set<Coordinates>> {
    val portalLocations = getPortalLocations(maze)
    val graph: MutableMap<Coordinates, Set<Coordinates>> = mutableMapOf()

    for (row in maze.indices) {
        for (col in maze[row].indices) {
            if (maze[row][col] == '.') {
                graph[Coordinates(col, row)] = getDestinations(maze, Coordinates(col, row), portalLocations)
            }
        }
    }

    return graph
}

fun bfs(graph: Map<Coordinates, Set<Coordinates>>, start: Coordinates, end: Coordinates): List<Coordinates> {
    val parents: MutableMap<Coordinates, Coordinates> = mutableMapOf()
    val fringe: ArrayDeque<Coordinates> = ArrayDeque()

    fringe.add(start)
    while (fringe.isNotEmpty() && !parents.containsKey(end)) {
        val current = fringe.removeFirst()
        val children = graph.getOrDefault(current, setOf()).filterNot { parents.contains(it) }
        children.forEach { parents[it] = current }
        fringe.addAll(children)
    }

    var current = end
    val path = mutableListOf<Coordinates>()
    while (current != start) {
        path.add(0, current)
        current = parents[current]!!
    }

    return path
}

fun bfs2(maze: List<String>, portals: Set<Portal>, start: Vec3, end: Vec3): List<Vec3> {
    val parents: MutableMap<Vec3, Vec3> = mutableMapOf()
    val fringe: ArrayDeque<Vec3> = ArrayDeque()

    fringe.add(start)
    while (fringe.isNotEmpty() && !parents.containsKey(end)) {
        val current = fringe.removeFirst()
        val children = getDestinations2(maze, current, portals).filterNot { parents.containsKey(it) }
        children.forEach { parents[it] = current }
        fringe.addAll(children)
    }

    var current = end
    val path = mutableListOf<Vec3>()
    while (current != start) {
        path.add(0, current)
        current = parents[current]!!
    }

    return path
}

fun main() {
    val maze = readMaze()
    val graph = getMazeGraph(maze)
    val start = getPortalLocations(maze).find { it.name == "AA" }!!.outer!!
    val end = getPortalLocations(maze).find { it.name == "ZZ" }!!.outer!!
    val path = bfs(graph, start, end)
    println(path.size)

    val path2 = bfs2(maze, getPortalLocations(maze), Vec3(start.x, start.y, 0), Vec3(end.x, end.y, 0))
    println(path2.size)
}