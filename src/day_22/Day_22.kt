package day_22

import java.math.BigInteger

fun extractInteger(str: String) = Regex("""(-?\d+)""").find(str)!!.value.toInt().toBigInteger()

fun reverse(start: BigInteger, deckSize: BigInteger) = deckSize - start - 1.toBigInteger()
fun shift(start: BigInteger, shift: BigInteger, deckSize: BigInteger): BigInteger {
    var end = (start - shift) % deckSize
    while (end < 0.toBigInteger()) end += deckSize
    return end
}
fun mod(start: BigInteger, interval: BigInteger, deckSize: BigInteger) = (start * interval) % deckSize

fun applyShuffles(shuffles: List<String>, start: BigInteger, deckSize: BigInteger): BigInteger {
    return shuffles.fold(start) { index, shuffle ->
        when {
            shuffle.contains("cut") -> {
                val shift = extractInteger(shuffle)
                shift(index, shift, deckSize)
            }
            shuffle.contains("deal with increment") -> {
                val interval = extractInteger(shuffle)
                mod(index, interval, deckSize)
            }
            else -> {
                reverse(index, deckSize)
            }
        }
    }
}

fun part1() {
    val shuffles = shared.listFromFile("data/day_22.txt")
    val result = applyShuffles(shuffles, 2019.toBigInteger(), 10007.toBigInteger())
    println(result)
}

fun main() {
   part1()
}