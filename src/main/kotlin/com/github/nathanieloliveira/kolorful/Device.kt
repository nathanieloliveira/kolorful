package com.github.nathanieloliveira.kolorful

interface Device {
    val range: Array<ClosedRange<Int>>
    fun read(address: UShort): UByte
    fun write(address: UShort, value: UByte)
}