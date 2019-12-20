package day_1

import java.lang.Integer.parseInt
import kotlin.math.floor

fun main() {
    val modules = shared.listFromFile("data/day_1.txt").map { parseInt(it) }
    val part1Fuel = modules.map { fuelForSingleMass(it) }.sum()
    val part2Fuel = modules.map { fuelForModuleAndFuel(it) }.sum()

    println("The total fuel required for part 1 is $part1Fuel.")
    println("The total fuel required for part 2 is $part2Fuel.")
}

fun fuelForModuleAndFuel(mass: Int): Int {
    val fuelForModule = fuelForSingleMass(mass)
    return fuelForModule + fuelForFuel(fuelForModule)
}

fun fuelForFuel(fuelMass: Int): Int {
    val fuelForFuel = fuelForSingleMass(fuelMass)
    return if (fuelForFuel > 0) (fuelForFuel + fuelForFuel(fuelForFuel)) else 0
}

fun fuelForSingleMass(mass: Int): Int = floor(mass.toDouble() / 3 - 2).toInt()