package day_12

import java.lang.Integer.parseInt
import kotlin.math.abs
import kotlin.math.sign

data class Vec3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(b: Vec3) = Vec3(this.x + b.x, this.y + b.y, this.z + b.z)
    operator fun minus(b: Vec3) = Vec3(this.x - b.x, this.y - b.y, this.z - b.z)
    operator fun times(b: Int) = Vec3(this.x * b, this.y * b, this.z * b)
    operator fun Int.times(b: Vec3): Vec3 = b * this
    fun sign() = Vec3(this.x.sign, this.y.sign, this.z.sign)
    fun componentSum(): Int = abs(this.x) + abs(this.y) + abs(this.z)
    override fun toString(): String = "<x= $x, x= $y, x= $z>"
}

class Moon(initialPosition: Vec3) {
    var position: Vec3 = initialPosition
    var velocity: Vec3 = Vec3(0, 0, 0)
    fun totalEnergy(): Int = position.componentSum() * velocity.componentSum()
}

fun deltaV(otherMoonPositions: List<Vec3>, position: Vec3): Vec3 {
    var result = Vec3(0, 0, 0)
    otherMoonPositions.forEach { result += (it - position).sign() }
    return result
}

fun step(moons: List<Moon>) {
    moons.forEach { moon ->
        moon.velocity += deltaV((moons - moon).map { it.position }, moon.position)
    }
    moons.forEach { moon ->
        moon.position += moon.velocity
    }
}

fun part1() {
    val vec3Regex = Regex("""<x=(-?\d+),\s*y=(-?\d+),\s*z=(-?\d+)>""")
    val moons: List<Moon> = shared.listFromFile("data/day_12.txt").map { line ->
        val (x, y, z) = vec3Regex.find(line)!!.destructured
        Moon(Vec3(parseInt(x), parseInt(y), parseInt(z)))
    }

    for (step in 1..1000) {
        step(moons)
    }

    val totalEnergy = moons.sumBy { it.totalEnergy() }
    println("The total energy of the system is $totalEnergy")
}

fun part2() {
    val vec3Regex = Regex("""<x=(-?\d+),\s*y=(-?\d+),\s*z=(-?\d+)>""")
    val moons: List<Moon> = shared.listFromFile("data/day_12.txt").map { line ->
        val (x, y, z) = vec3Regex.find(line)!!.destructured
        Moon(Vec3(parseInt(x), parseInt(y), parseInt(z)))
    }
    val originalMoonPositions: List<Vec3> = shared.listFromFile("data/day_12.txt").map { line ->
        val (x, y, z) = vec3Regex.find(line)!!.destructured
        Vec3(parseInt(x), parseInt(y), parseInt(z))
    }

    // The period (in steps) of the cycles for each dimension
    var xCycle: Int = -1
    var yCycle: Int = -1
    var zCycle: Int = -1

    // keep stepping until the periods are all found
    var step = 0
    while (xCycle < 0 || yCycle < 0 || zCycle < 0) {
        step(moons)
        step++
        if (xCycle < 0 && moons.map {it.position.x} == originalMoonPositions.map { it.x } && moons.map {it.velocity.x}.all { it == 0 })
            xCycle = step
        if (yCycle < 0 && moons.map {it.position.y} == originalMoonPositions.map { it.y } && moons.map {it.velocity.y}.all { it == 0 })
            yCycle = step
        if (zCycle < 0 && moons.map {it.position.z} == originalMoonPositions.map { it.z } && moons.map {it.velocity.z}.all { it == 0 })
            zCycle = step
    }

    println("Period is the LCM of: $xCycle $yCycle $zCycle")
    println("(Use https://www.calculatorsoup.com/calculators/math/lcm.php)")
}

fun main() {
    part1()
    part2()
}