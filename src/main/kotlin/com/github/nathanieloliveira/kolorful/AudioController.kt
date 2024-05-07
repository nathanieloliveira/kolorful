package com.github.nathanieloliveira.kolorful

@OptIn(ExperimentalStdlibApi::class)
class AudioController: Device {
    override val range: Array<ClosedRange<Int>> = arrayOf(0xFF10..0xFF26)

    override fun read(address: UShort): UByte {
        println("AudioController read ${address.toHexString()}")
        return 0u
    }

    override fun write(address: UShort, value: UByte) {
        println("AudioController write: ${address.toHexString()} = ${value.toHexString()}")
    }
}