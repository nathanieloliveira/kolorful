package com.github.nathanieloliveira.kolorful

class Cpu {

    companion object {
        const val FLAG_ZERO: UShort = 0x0080u
        const val FLAG_SUB: UShort = 0x0040u
        const val FLAG_HALF_CARRY: UShort = 0x0020u
        const val FLAG_CARRY: UShort = 0x0010u
    }

    var af: UShort = 0u
    var bc: UShort = 0u
    var de: UShort = 0u
    var hl: UShort = 0u

    var sp: UShort = 0u
    var pc: UShort = 0u

    var ime: Boolean = false

    var z: Boolean
        get() = af and FLAG_ZERO == FLAG_ZERO
        set(value) {
            af = setFlag(value, FLAG_ZERO)
        }

    var n: Boolean
        get() = af and FLAG_SUB == FLAG_SUB
        set(value) {
            af = setFlag(value, FLAG_SUB)
        }

    var h: Boolean
        get() = af and FLAG_HALF_CARRY == FLAG_HALF_CARRY
        set(value) {
            af = setFlag(value, FLAG_HALF_CARRY)
        }

    var c: Boolean
        get() = af and FLAG_CARRY == FLAG_CARRY
        set(value) {
            af = setFlag(value, FLAG_CARRY)
        }


    private fun setFlag(value: Boolean, flag: UShort) = if (value) {
        af or flag
    } else {
        af and flag.inv()
    }
}