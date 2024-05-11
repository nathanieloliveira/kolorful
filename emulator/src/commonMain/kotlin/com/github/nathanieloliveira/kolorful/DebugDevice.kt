package com.github.nathanieloliveira.kolorful

@OptIn(ExperimentalStdlibApi::class)
class DebugDevice(override val range: Array<ClosedRange<Int>>) : Device {

    override fun read(address: UShort): UByte {
        println("DebugDevice: Reading ${address.toHexString()}")
        return 0u
    }

    override fun write(address: UShort, value: UByte) {
        println("DebugDevice: writing ${address.toHexString()} value ${value.toHexString()}")
    }
}