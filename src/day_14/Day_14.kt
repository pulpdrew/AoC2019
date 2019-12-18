package day_14

import java.lang.Integer.parseInt
import kotlin.math.ceil

class ChemicalGraph(private val edges: Set<Edge>, nodes: Map<String, Int>) {

    data class Edge(val from: String, val to: String, val weight: Int)

    // Maps a chemical name to the quantity of that chemical that is produced during a reaction
    private val products: Map<String, Int> = nodes

    // Just using binary search
    fun maxFuelForOre(oreCount: Long): Long {
        var lowerBound: Long = 1
        var upperBound: Long = 100000000

        while (lowerBound < upperBound - 1) {
            val midpoint = (lowerBound + upperBound) / 2
            if (oreForFuel(midpoint) > oreCount) {
                upperBound = midpoint
            } else {
                lowerBound = midpoint
            }
        }

        return lowerBound
    }

    fun oreForFuel(fuelCount: Long = 1): Long {

        // Maps chemical to the amount of it that is needed
        val needs: MutableMap<String, Long> = mutableMapOf()
        needs["FUEL"] = fuelCount

        this.topologicalSort().reversed().forEach { product ->

            // Figure out how many times the reaction must run to produce as much as is needed
            val quantityNeeded = needs[product]!!
            val quantityPerReaction = this.products[product] ?: error("Product $product not found in this graph.")
            val reactionCount = ceil(quantityNeeded.toDouble() / quantityPerReaction.toDouble()).toLong()

            // Find the ingredients needed for this product, add them to needs
            this.edges.filter { edge -> edge.to == product }.forEach { edge ->
                needs[edge.from] = (reactionCount * edge.weight) + needs.getOrDefault(edge.from, 0)
            }
        }

        return needs["ORE"]!!
    }

    private fun topologicalSort(): List<String> {
        val edgeSet: MutableSet<Edge> = edges.toMutableSet()
        val productsSet: MutableSet<String> = products.keys.toMutableSet()
        val sort: MutableList<String> = mutableListOf()

        while(productsSet.isNotEmpty()) {

            // Find any sinks
            val sinks = productsSet.filter { product ->
                edgeSet.none { edge ->
                    edge.from == product
                }
            }

            // remove the sinks from the products and remove any edges to the sinks
            productsSet.removeAll(sinks)
            edgeSet.removeIf { edge -> sinks.contains(edge.to) }
            sort.addAll(sinks)
        }

        return sort.reversed()
    }

    override fun toString(): String {
        return this.products.map { product ->
            val ingredients =
                this.edges.filter { it.to == product.key }.joinToString(" + ") { "${it.weight} ${it.from}" }
            "$ingredients -> ${product.value} ${product.key}"
        }.joinToString("\n")
    }

    companion object {
        fun fromFile(filename: String): ChemicalGraph {

            val edges: MutableSet<Edge> = mutableSetOf()
            val nodes: MutableMap<String, Int> = mutableMapOf()

            // The quantity and name of a chemical
            val chemRegex = Regex("""((\d+) ([A-Z]+))""")

            // Build a graph from each of the reactions in the input file
            val lines = shared.listFromFile(filename)
            lines.forEach { reactionDescription ->

                // Read each chemical from the line
                val chemicalDescriptions = chemRegex.findAll(reactionDescription).toList()

                // Get the result and its quantity and store in nodes
                val (_, resultQuantity, resultName) = chemicalDescriptions.last().destructured
                nodes[resultName] = parseInt(resultQuantity)

                // Draw an edge from each ingredient to the result
                for (i in 0 until chemicalDescriptions.size - 1) {
                    val (_, ingredientQuantity, ingredientName) = chemicalDescriptions[i].destructured
                    edges.add(Edge(ingredientName, resultName, parseInt(ingredientQuantity)))
                }
            }

            return ChemicalGraph(edges, nodes)
        }
    }
}

fun main() {
    val graph = ChemicalGraph.fromFile("data/day_14.txt")
    println("Ore needed for 1 Fuel: ${graph.oreForFuel()}")
    println("Ore needed for 460664 Fuel: ${graph.maxFuelForOre(1000000000000)}")
}