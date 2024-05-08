package com.github.nathanieloliveira.kolorful

class BankedMemory(val ran: ClosedRange<Int>) : Device {

    val bank0 = ByteArray(0x1000)
    val banks = Array(8) {
        ByteArray(0x1000)
    }

    var selectedBank = 1
        set(value) {
            require(value in 0..ran.endInclusive)
            field = value.coerceAtLeast(1)
        }

    override val range: Array<ClosedRange<Int>> = arrayOf(ran)

    override fun read(address: UShort): UByte {
        if (address.toUInt() in 0xC000u..0xCFFFu) {
            val readAddr = address - 0xC000u
            return bank0[readAddr.toInt()].toUByte()
        }
        val readAddr = address - 0xD000u
        return banks[selectedBank][readAddr.toInt()].toUByte()
    }

    override fun write(address: UShort, value: UByte) {
        if (address.toUInt() in 0xC000u..0xCFFFu) {
            val readAddr = address - 0xC000u
            bank0[readAddr.toInt()] = value.toByte()
            return
        }
        val readAddr = address - 0xD000u
        banks[selectedBank][readAddr.toInt()] = value.toByte()
    }

}