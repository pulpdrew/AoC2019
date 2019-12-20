package day_6

import kotlin.math.min

fun main() {
    val orbitData: List<List<String>> = shared.listFromFile("data/day_6.txt").map { it.splitToSequence(")").toList() }
    val orbitMap = buildOrbitMap(orbitData)
    val parentMap = buildParentMap((orbitData))
    println("Total number of direct and indirect orbits: ${countOrbits(orbitMap, "COM")}")
    println("Distance From YOU to SAN: ${distanceBetween("YOU", "SAN", parentMap)} orbital transfers.")
}

fun distanceBetween(nodeA: String, nodeB: String, parentMap: Map<String, String>): Int {
    val pathToA = pathTo(nodeA, parentMap)
    val pathToB = pathTo(nodeB, parentMap)

    var countOfCommonNodes = 0
    for (i in 0 until min(pathToA.size, pathToB.size)) {
        if (pathToA[i] == pathToB[i]) countOfCommonNodes++
        else break
    }

    return pathToA.size + pathToB.size - countOfCommonNodes * 2
}

fun pathTo(nodeName: String, parentMap: Map<String, String>): MutableList<String> {
    return if (parentMap.containsKey(nodeName)) {
        val parent = parentMap[nodeName]!!
        val pathToParent = pathTo(parent, parentMap)
        pathToParent.add(parent)
        pathToParent
    } else {
        mutableListOf()
    }
}

fun countOrbits(orbitMap: Map<String, List<String>>, root: String, parentOrbitCount: Int = -1): Int {
    val orbitsOfThisPlanet = parentOrbitCount + 1
    var childrenOrbitCount = 0
    if (orbitMap.containsKey(root)) {
        childrenOrbitCount = orbitMap[root]!!.map {
            countOrbits(orbitMap, it, orbitsOfThisPlanet)
        }.sum()
    }
    return orbitsOfThisPlanet + childrenOrbitCount
}

fun buildOrbitMap(orbits: List<List<String>>): Map<String, List<String>> {
    val orbitMap = mutableMapOf<String, MutableList<String>>()
    orbits.forEach { orbit ->
        if (orbitMap.containsKey(orbit[0])) {
            orbitMap[orbit[0]]!!.add(orbit[1])
        } else {
            orbitMap[orbit[0]] = mutableListOf(orbit[1])
        }
    }
    return orbitMap
}

fun buildParentMap(orbits: List<List<String>>): Map<String, String> {
    val parents = mutableMapOf<String, String>()
    orbits.forEach { orbit ->
        parents[orbit[1]] = orbit[0]
    }
    return parents
}