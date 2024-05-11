package com.github.nathanieloliveira.kolorful

class VRam: Device {

    var selectedBank = 0

    val banks = Array(2) {
        ByteArray(0x2000)
    }

    override val range: Array<ClosedRange<Int>> = arrayOf(
        0x8000..0x9FFF,
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
            require(value <= 1u) {
                "Trying to write $value into VRAM Bank Register"
            }
            selectedBank = value.toInt()
            return
        }
        val writeAddr = address.toInt() - 0x8000
        banks[selectedBank][writeAddr] = value.toByte()
    }
}