package com.github.nathanieloliveira.kolorful

class Memory(override val range: ClosedRange<Int>) : Device {

    val memory = ByteArray(range.endInclusive - range.start + 1)

    override fun read(address: UShort): UByte {
        return memory[address.toInt()].toUByte()
    }

    override fun write(address: UShort, value: UByte) {
        memory[address.toInt()] = value.toByte()
    }

}