package day_24

fun calculateBiodiversityScore(bugs: List<List<Boolean>>): Int {
    var score = 0
    for (row in bugs.indices) {
        for (col in bugs[row].indices) {
            if (bugs[row][col]) score += 1.shl(row * bugs[row].size + col)
        }
    }
    return score
}

fun step(bugs: List<List<Boolean>>): List<List<Boolean>> {
    return bugs.mapIndexed { row, bugList ->
        bugList.mapIndexed { col, bug ->
            val left = (col > 0 && bugs[row][col - 1])
            val right = (col < bugs[row].size - 1 && bugs[row][col + 1])
            val top = (row > 0 && bugs[row - 1][col])
            val bottom = (row < bugs.size - 1 && bugs[row + 1][col])

            if (bug) {
                listOf(left, right, top, bottom).count { it } == 1
            } else {
                listOf(left, right, top, bottom).count { it } in 1..2
            }
        }
    }
}

fun printBugs(bugs: List<List<Boolean>>) {
    println(bugs.joinToString("\n") { line -> line.joinToString("") { if (it) "#" else "." } })
    println()
}

fun main() {
    var bugs = shared.listFromFile("data/day_24.txt").map { line -> line.map { it == '#' }}

    val bdRatings = mutableSetOf(calculateBiodiversityScore(bugs))
    while (true) {
        bugs = step(bugs)
        val rating = calculateBiodiversityScore(bugs)
        if (bdRatings.contains(rating)) break
        else bdRatings.add(rating)
    }

    println(calculateBiodiversityScore(bugs))
}