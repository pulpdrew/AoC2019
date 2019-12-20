package day_8

import java.io.File

fun main() {
    part1()
    part2()
}

fun part1() {
    val digits = File("data/day_8.txt").useLines { it.first() }.toCharArray().map { it.toInt() - '0'.toInt() }
    val image = buildImage(digits, 25, 6)
    val layerWithFewest0s = image.reduce { fewest, layer ->
        if (countDigits(layer, 0) < countDigits(fewest, 0)) layer else fewest
    }
    println("Part 1: ${countDigits(layerWithFewest0s, 1) * countDigits(layerWithFewest0s, 2)}")
}

fun part2() {
    val digits = File("data/day_8.txt").useLines { it.first() }.toCharArray().map { it.toInt() - '0'.toInt() }
    val image = buildImage(digits, 25, 6)
    printImage(render(image))
}

fun render(image: List<List<List<Int>>>): List<List<Int>> {
    return image.reduceRight { layer, canvas ->
        (layer zip canvas).map { rows ->
            (rows.first zip rows.second).map { digits ->
                if (digits.first != 2) digits.first else digits.second
            }
        }
    }
}

fun printImage(layer: List<List<Int>>) {
    layer.forEach {
        println(it.map { digit -> if (digit == 1) '1' else ' ' })
    }
}

fun countDigits(layer: List<List<Int>>, digit: Int): Int = layer.flatten().count { it == digit }

fun buildImage(digits: List<Int>, width: Int, height: Int): List<List<List<Int>>> {
    val numLayers = digits.size / width / height
    val image = mutableListOf<List<List<Int>>>()
    for (layer in 0 until numLayers) {
        val layerDigits = mutableListOf<List<Int>>()
        for (row in 0 until height) {
            val start = layer * (width * height) + row * width
            val end = start + width
            layerDigits.add(digits.subList(start, end))
        }
        image.add(layerDigits.toList())
    }
    return image.toList()
}