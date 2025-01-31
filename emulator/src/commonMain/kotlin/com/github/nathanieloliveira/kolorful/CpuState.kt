package com.github.nathanieloliveira.kolorful

data class CpuState(
    val next: Cpu.RealInstruction,
    val af: UShort,
    val bc: UShort,
    val de: UShort,
    val hl: UShort,
    val sp: UShort,
    val pc: UShort,
    val a: UByte,
    val b: UByte,
    val c: UByte,
    val d: UByte,
    val e: UByte,
    val h: UByte,
    val l: UByte,
    val z: Boolean,
    val n: Boolean,
    val half: Boolean,
    val carry: Boolean,
    val ime: Boolean,
    val interruptEnable: UByte,
    val interruptFlag: UByte,
    val stack: ByteArray,
)
