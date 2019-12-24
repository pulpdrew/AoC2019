package day_23

import day_9.IntCodeVM
import java.math.BigInteger

data class Packet(val address: Int, val x: BigInteger, val y: BigInteger)

class NetworkedIntcodeVM(
    address: Int,
    program: List<BigInteger>,
    private val network: List<NetworkedIntcodeVM>,
    private val nat: NAT
) {
    var idle = false

    private val vm = IntCodeVM(program)
    private val incoming: MutableList<Packet> = mutableListOf()
    private var readingPacket = false

    init {
        vm.inputBuffer.add(address.toBigInteger())
    }

    fun execute() {

        // Make sure there is some sort of input
        when {
            readingPacket -> {
                this.vm.inputBuffer.add(this.incoming.first().y)
                this.incoming.removeAt(0)
                this.readingPacket = false
                this.idle = false
            }
            this.incoming.isNotEmpty() -> {
                this.readingPacket = true
                this.vm.inputBuffer.add(this.incoming.first().x)
                this.idle = false
            }
            else -> {
                this.vm.inputBuffer.add((-1).toBigInteger())
                this.idle = true
            }
        }

        // Allow the vm to run until it requests input
        this.vm.execute()

        // Send any produced packets
        while (this.vm.outputBuffer.size >= 3) {
            sendPacket()
            this.idle = false
        }
    }

    fun addPacket(packet: Packet) {
        this.incoming.add(packet)
    }

    private fun sendPacket() {

        val packet = Packet(
            this.vm.outputBuffer.removeAt(0).toInt(),
            this.vm.outputBuffer.removeAt(0),
            this.vm.outputBuffer.removeAt(0)
        )

        if (packet.address in network.indices) {
            network[packet.address].addPacket(packet)
        } else if (packet.address == 255) {
            nat.addPacket(packet)
        }
    }
}

class NAT(private val network: List<NetworkedIntcodeVM>) {
    var packet: Packet? = null

    fun addPacket(packet: Packet) {
        this.packet = packet
    }

    fun preventIdle(): Packet? {
        if (this.network.all { it.idle }) {
            this.network.first().addPacket(this.packet!!)
            return this.packet
        }
        return null
    }
}

fun part1() {
    val program = shared.listFromDelimitedFile("data/day_23.txt").map { BigInteger(it) }

    val network = mutableListOf<NetworkedIntcodeVM>()
    val nat = NAT(network)
    for (address in 0..49) {
        network.add(NetworkedIntcodeVM(address, program, network, nat))
    }

    while(nat.packet == null) {
        for (vm in network) {
            vm.execute()
        }
    }

    println("Part 1: ${nat.packet}")
}

fun part2() {
    val program = shared.listFromDelimitedFile("data/day_23.txt").map { BigInteger(it) }

    val network = mutableListOf<NetworkedIntcodeVM>()
    val nat = NAT(network)
    for (address in 0..49) {
        network.add(NetworkedIntcodeVM(address, program, network, nat))
    }

    var previousNatPacket: Packet? = null

    while(true) {
        for (vm in network) {
            vm.execute()
        }

        val natPacket = nat.preventIdle()
        if (natPacket != null) {
            if (previousNatPacket != null && previousNatPacket.y == natPacket.y) {
                println("Part 2: $natPacket")
                break
            } else {
                previousNatPacket = natPacket
            }
        }
    }
}

fun main() {
    part1()
    part2()
}