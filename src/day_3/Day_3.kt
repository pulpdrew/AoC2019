package day_3

import java.lang.Integer.parseInt
import kotlin.math.abs

/**
 * Structure to hold location data for a single 2D point. Steps is the number
 * of steps taken to reach the point, and is not used for equality or hashcode.
 */
data class Point(val x: Int, val y: Int, val steps: Int) {
    override fun equals(other: Any?): Boolean {
        return if (other is Point) (this.x == other.x && this.y == other.y) else false
    }

    override fun hashCode() = this.x * this.y
}

/**
 * Structure to hold direction and distance data for a single wire segment.
 */
data class Segment(val direction: Char, val distance: Int)

fun String.toSegment(): Segment {
    val direction = this[0]
    val distance = parseInt(this.substring(1 until this.length))
    return Segment(direction, distance)
}

fun main() {
    val wires: List<List<Segment>> = shared.listFromFile("data/day_3.txt").map { wireSegments ->
        wireSegments.splitToSequence(",").toList().map { it.toSegment() }
    }
    val intersections = findIntersections(wires)
    val part1Distance = intersections.map { abs(it.x) + abs(it.y) }.min()
    val part2Distance = intersections.map { it.steps }.min()

    println("Part 1 Smallest Distance: $part1Distance")
    println("Part 2 Smallest Distance: $part2Distance")
}

/**
 * Finds all the points at which the given wires intersect. The steps are equal to sum
 * of the minimum number of steps each wire takes to get to the intersection.
 */
fun findIntersections(wires: List<List<Segment>>): Set<Point> {
    val allWirePoints: List<Set<Point>> = wires.map { plotWire(it) }

    val intersections: MutableSet<Point> = (allWirePoints[0] intersect allWirePoints[1]).map {
        val firstPointSteps: Int = allWirePoints[0].find { p -> p == it }?.steps ?: 0
        val secondPointSteps: Int = allWirePoints[1].find { p -> p == it }?.steps ?: 0
        Point(it.x, it.y, firstPointSteps + secondPointSteps)
    }.toMutableSet()

    intersections.removeIf { it.x == 0 && it.y == 0 }
    return intersections.toSet()
}

/**
 * Calculates the set of all points occupied by the given wire
 * starting at (0,0,steps=0). Each point is paired with the minimum
 * number of steps the wire takes to reach that point.
 */
fun plotWire(wire: List<Segment>): Set<Point> {
    var segmentStart = Point(0, 0, 0)
    val wirePoints = mutableSetOf<Point>()
    for (segment in wire) {
        val segmentPoints = plotSegment(segment, segmentStart)
        wirePoints.addAll(segmentPoints)
        segmentStart = segmentPoints.last()
    }
    return wirePoints
}

/**
 * Calculates a list of all the points occupied by the given segment
 * starting at the given point.
 */
fun plotSegment(segment: Segment, start: Point): List<Point> {
    return when (segment.direction) {
        'U' -> (0..segment.distance).map { Point(start.x, start.y + it, start.steps + it) }
        'D' -> (0..segment.distance).map { Point(start.x, start.y - it, start.steps + it) }
        'L' -> (0..segment.distance).map { Point(start.x - it, start.y, start.steps + it) }
        'R' -> (0..segment.distance).map { Point(start.x + it, start.y, start.steps + it) }
        else -> listOf()
    }
}