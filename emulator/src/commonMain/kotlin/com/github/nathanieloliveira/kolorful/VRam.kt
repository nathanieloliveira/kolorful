package com.github.nathanieloliveira.kolorful

class VRam: Device {

    companion object {
        const val VRAM_START = 0x8000
        const val VRAM_END = 0x9FFF
    }

    var selectedBank = 0

    val banks = Array(2) {
        ByteArray(0x2000)
    }

    override val range: Array<ClosedRange<Int>> = arrayOf(
        VRAM_START..VRAM_END,
        0xFF4F..0xFF4F,
    )

    override fun read(address: UShort): UByte {
        if (address.toUInt() == 0xFF4Fu) {
            return (0xFEu or selectedBank.toUInt()).toUByte()
        }
        val readAddr = address.toInt() - 0x8000
        return banks[selectedBank][readAddr].toUByte()
    }

    override fun write(address: UShort, value: UByte) {
        if (address.toUInt() == 0xFF4Fu) {
            val bank = value and 0x01u
            selectedBank = bank.toInt()
            return
        }
        val writeAddr = address.toInt() - 0x8000
        banks[selectedBank][writeAddr] = value.toByte()
    }
}