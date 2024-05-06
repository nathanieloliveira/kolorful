package com.github.nathanieloliveira.kolorful

class Bus(
    val devices: Array<Device>
) {



    fun read(address: UShort): UByte {
        for (d in devices) {
            if (address.toInt() in d.range) {
                d.read(address)
            }
        }
        error("not found!")
    }

    fun write(address: UShort, b: UByte) {
        for (d in devices) {
            if (address.toInt() in d.range) {
                d.write(address, b)
            }
        }
        error("not found!")
    }

}