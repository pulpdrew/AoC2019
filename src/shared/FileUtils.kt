package shared

import java.io.File

fun listFrom(filename: String): List<String> = File(filename).useLines { it.toList() }
fun mutableListFrom(filename: String): MutableList<String> = listFrom(filename).toMutableList()