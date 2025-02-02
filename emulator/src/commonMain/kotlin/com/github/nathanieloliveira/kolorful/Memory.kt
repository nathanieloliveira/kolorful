package com.github.nathanieloliveira.kolorful

class Memory(override val range: Array<ClosedRange<Int>>, val memory: ByteArray) : Device {

    init {
        assert(range.size == 1) { "only one range can be mapped" }
    }

    override fun read(address: UShort): UByte {
        val start = range.first().start.toUShort()
        val realAddr = address - start
        return memory[realAddr.toInt()].toUByte()
    }

    override fun write(address: UShort, value: UByte) {
        val start = range.first().start.toUShort()
        val realAddr = address - start
        memory[realAddr.toInt()] = value.toByte()
    }
}