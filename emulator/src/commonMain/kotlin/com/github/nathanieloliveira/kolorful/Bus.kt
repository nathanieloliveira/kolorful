package com.github.nathanieloliveira.kolorful

@OptIn(ExperimentalStdlibApi::class)
class Bus(
    val devices: Array<Device>
) {

    fun read(address: UShort): UByte {
        for (d in devices) {
            if (d.range.any { address.toInt() in it }) {
                return d.read(address)
            }
        }
        error("Bus READ error. No memory mapped device at address $address.")
    }

    fun write(address: UShort, b: UByte) {
        for (d in devices) {
            if (d.range.any { address.toInt() in it }) {
                d.write(address, b)
                return
            }
        }
        error("Bus write error. No memory mapped device at address 0x${address.toHexString(HexFormat.UpperCase)}. Write 0x${b.toHexString()}")
    }

}