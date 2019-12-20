package day_10

import kotlin.math.*

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(b: Vec2): Vec2 = Vec2(this.x + b.x, this.y + b.y)
    operator fun minus(b: Vec2): Vec2 = Vec2(this.x - b.x, this.y - b.y)
    operator fun times(b: Int): Vec2 = Vec2(this.x * b, this.y * b)
    fun angleTo(b: Vec2): Double {
        val difference = (b - this).reduce()
        var angle = -Math.toDegrees(atan2(-difference.y.toDouble(), difference.x.toDouble())) + 90
        while (angle < 0) angle += 360
        return angle
    }
    fun reduce(): Vec2 {
        when {
            abs(this.x) == abs(this.y) -> return Vec2(1 * this.x.sign, 1 * this.y.sign)
            this.x == 0 -> return Vec2(0, this.y.sign)
            this.y == 0 -> return Vec2(this.x.sign, 0)

            // Find gcf using euclid's algorithm
            else -> {
                var a = abs(this.x)
                var b = abs(this.y)
                if (a < b) a = b.also { b = a }

                while (a % b != 0) {
                    val r = a % b
                    a = b
                    b = r
                }

                return Vec2(this.x / b, this.y / b)
            }
        }

    }
}

fun main() {
    part1()
    part2()
}

fun part1() {
    val asteroids = readMap("data/day_10.txt")
    val bestStation: Vec2 = asteroids.maxBy { countVisibleAsteroidsFrom(it, asteroids) } ?: Vec2(0, 0)
    println("Best Station: $bestStation. ${countVisibleAsteroidsFrom(bestStation, asteroids)} visible asteroids")
}

fun part2() {

    val asteroids = readMap("data/day_10.txt").toMutableSet()
    val bestStation: Vec2 = asteroids.maxBy { countVisibleAsteroidsFrom(it, asteroids) } ?: Vec2(0, 0)
    asteroids -= bestStation

    val destroyed: MutableList<Vec2> = mutableListOf()
    while (destroyed.size < 200) {
        val asteroidsByAngle: MutableList<Vec2> = asteroids.toList().sortedBy { bestStation.angleTo(it) }.toMutableList()
        asteroidsByAngle.forEach {
            if (getAsteroidsBetween(bestStation, it, asteroids).isEmpty()) {
                destroyed.add(it)
                println(it)
            }
        }
        asteroids -= destroyed
    }

    println("200th destroyed asteroid: ${destroyed[199]}")
}

fun countVisibleAsteroidsFrom(station: Vec2, asteroids: Set<Vec2>): Int {
    return asteroids.filter {
        it != station && getAsteroidsBetween(station, it, asteroids).isEmpty()
    }.size
}

fun getAsteroidsBetween(a: Vec2, b: Vec2, asteroids: Set<Vec2>): Set<Vec2> {
    val vectorBetween = (a - b).reduce()
    var tempVec = b + vectorBetween
    val asteroidsBetween: MutableSet<Vec2> = mutableSetOf()
    while (tempVec != a) {
        if (asteroids.contains(tempVec)) asteroidsBetween.add(tempVec)
        tempVec += vectorBetween
    }
    return asteroidsBetween.toSet()
}

fun readMap(filename: String): Set<Vec2> {
    return shared.listFromFile(filename).mapIndexed { row, line ->
        line.mapIndexed { col, char ->
            if (char == '#') Vec2(col, row) else null
        }.filterNotNull()
    }.flatten().toSet()
}