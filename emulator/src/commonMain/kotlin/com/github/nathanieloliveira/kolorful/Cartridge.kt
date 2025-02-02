package com.github.nathanieloliveira.kolorful

class Cartridge(
    val rom: ByteArray
): Device {

    companion object {
        val CARTRIDGE_RANGE = 0x0000..0x7FFF
        val EXTERNAL_RAM_RANGE = 0xA000..0xBFFF
    }

    override val range: Array<ClosedRange<Int>> = arrayOf(
        CARTRIDGE_RANGE,
        EXTERNAL_RAM_RANGE,
    )

    val externalRam = ByteArray(EXTERNAL_RAM_RANGE.endInclusive - EXTERNAL_RAM_RANGE.start + 1)

    override fun read(address: UShort): UByte {
        return when (address.toInt()) {
            in CARTRIDGE_RANGE -> {
                val realAddr = address - CARTRIDGE_RANGE.start.toUShort()
                rom[realAddr.toInt()]
            }
            in EXTERNAL_RAM_RANGE -> {
                val realAddr = address - EXTERNAL_RAM_RANGE.start.toUShort()
                externalRam[realAddr.toInt()]
            }
            else -> error("no")
        }.toUByte()
    }

    override fun write(address: UShort, value: UByte) {
        when (address.toInt()) {
            in CARTRIDGE_RANGE -> {
                error("trying to write to ROM -> address=$address value=$value")
            }
            in EXTERNAL_RAM_RANGE -> {
                val realAddr = address - EXTERNAL_RAM_RANGE.start.toUShort()
                externalRam[realAddr.toInt()] = value.toByte()
            }
            else -> error("no")
        }
    }
}