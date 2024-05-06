package com.github.nathanieloliveira.kolorful

interface Device {
    val range: ClosedRange<Int>
    fun read(address: UShort): UByte
    fun write(address: UShort, value: UByte)
}