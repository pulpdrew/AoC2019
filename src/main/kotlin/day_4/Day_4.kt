package day_4

const val min = 145852
const val max = 616942

fun main() {
    val possiblePasswords = (min..max).filter { meetsCriteria(it) }
    println("Total possible passwords: ${possiblePasswords.size}")
}

fun meetsCriteria(num: Int): Boolean {
    val digits = digits(num)
    var hasDouble = false

    for (i in 1 until digits.size) {
        if (digits[i] < digits[i - 1])
            return false
        else if (digits[i] == digits[i - 1] && ((i < 2 || digits[i] != digits[i-2]) && (i > digits.size - 2 || digits[i] != digits[i+1])))
            hasDouble = true
    }
    return hasDouble
}

fun digits(num: Int): List<Int> {
    val digits = mutableListOf<Int>()
    var temp = num

    while (temp > 0) {
        digits.add(temp % 10)
        temp /= 10
    }

    return digits.toList().reversed()
}