package shared

import java.io.File

fun listFromFile(filename: String): List<String> = File(filename).useLines { it.toList() }
fun listFromDelimitedFile(filename: String, delimiter: String = ","): List<String> {
    return File(filename).useLines { it.joinToString() }.splitToSequence(delimiter).toList()
}