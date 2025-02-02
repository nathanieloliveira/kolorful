package com.github.nathanieloliveira.kolorful

import kotlin.Char

class Serial : Device {

    override val range: Array<ClosedRange<Int>> = arrayOf(
        0xFF01..0xFF02
    )

    var sb: UByte = 0x00u
    var sc: UByte = 0x00u

    override fun read(address: UShort): UByte {
        return when (address.toUInt()) {
            0xFF01u -> sb
            0xFF02u -> sc
            else -> error("Not valid")
        }
    }

    override fun write(address: UShort, value: UByte) {
        when (address.toUInt()) {
            0xFF01u -> {
                sb = value
            }
            0xFF02u -> {
                sc = value
                val and = sc.toUInt() and 0x81u
                if (and == 0x81u) {
                    val char = Char(sb.toUShort())
                    print(char)
                }
            }
            else -> error("Not valid")
        }
    }
}