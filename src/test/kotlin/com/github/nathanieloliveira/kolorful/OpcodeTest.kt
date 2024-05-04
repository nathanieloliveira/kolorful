package com.github.nathanieloliveira.kolorful

import kotlin.test.Test

/**
 * reference: https://gbdev.io/gb-opcodes//optables/
 */
class OpcodeTest {

    private fun assertOpcode(byte: Int, expected: Opcode) {
        val decoded = Opcode.decode(byte.toByte())
        assert(decoded == expected) {
            "expected: ${expected.name}, got: ${decoded.name}"
        }
    }

    @Test
    fun testSimpleOpcodes() {
        assertOpcode(0x00, Opcode.NOP)
        assertOpcode(0x76, Opcode.HALT)
        assertOpcode(0xF3, Opcode.DI)
        assertOpcode(0xFB, Opcode.EI)
        assertOpcode(0xCB, Opcode.PREFIX)
        assertOpcode(0x10, Opcode.STOP)

        assertOpcode(0x07, Opcode.RLCA)
        assertOpcode(0x17, Opcode.RLA)
        assertOpcode(0x0F, Opcode.RRCA)
        assertOpcode(0x1F, Opcode.RRA)

        assertOpcode(0x27, Opcode.DAA)
        assertOpcode(0x37, Opcode.SCF)
        assertOpcode(0x2F, Opcode.CPL)
        assertOpcode(0x3F, Opcode.CCF)

        assertOpcode(0xC9, Opcode.RET)
        assertOpcode(0xD9, Opcode.RETI)
        assertOpcode(0xE9, Opcode.JP_HL)

        assertOpcode(0xC1, Opcode.POP_R16)
        assertOpcode(0xD1, Opcode.POP_R16)
        assertOpcode(0xE1, Opcode.POP_R16)
        assertOpcode(0xF1, Opcode.POP_R16)

        assertOpcode(0xC5, Opcode.PUSH_R16)
        assertOpcode(0xD5, Opcode.PUSH_R16)
        assertOpcode(0xE5, Opcode.PUSH_R16)
        assertOpcode(0xF5, Opcode.PUSH_R16)

        assertOpcode(0xCD, Opcode.CALL_N16)

        assertOpcode(0x18, Opcode.JR_E8)
        assertOpcode(0x20, Opcode.JR_CC_E8)
        assertOpcode(0x30, Opcode.JR_CC_E8)
        assertOpcode(0x28, Opcode.JR_CC_E8)
        assertOpcode(0x38, Opcode.JR_CC_E8)

        assertOpcode(0xCC, Opcode.CALL_CC_N16)
        assertOpcode(0xDC, Opcode.CALL_CC_N16)
        assertOpcode(0xC4, Opcode.CALL_CC_N16)
        assertOpcode(0xD4, Opcode.CALL_CC_N16)

        assertOpcode(0xC0, Opcode.RET_CC)
        assertOpcode(0xD0, Opcode.RET_CC)
        assertOpcode(0xC8, Opcode.RET_CC)
        assertOpcode(0xD8, Opcode.RET_CC)

        assertOpcode(0xC3, Opcode.JP_N16)
        assertOpcode(0xE9, Opcode.JP_HL)
        assertOpcode(0xCA, Opcode.JP_CC_N16)
        assertOpcode(0xDA, Opcode.JP_CC_N16)
        assertOpcode(0xC2, Opcode.JP_CC_N16)
        assertOpcode(0xD2, Opcode.JP_CC_N16)

        assertOpcode(0x03, Opcode.INC_R16)
        assertOpcode(0x13, Opcode.INC_R16)
        assertOpcode(0x23, Opcode.INC_R16)
        assertOpcode(0x33, Opcode.INC_SP)

        assertOpcode(0x0B, Opcode.DEC_R16)
        assertOpcode(0x1B, Opcode.DEC_R16)
        assertOpcode(0x2B, Opcode.DEC_R16)
        assertOpcode(0x3B, Opcode.DEC_SP)

        assertOpcode(0x04, Opcode.INC_R8)
        assertOpcode(0x14, Opcode.INC_R8)
        assertOpcode(0x24, Opcode.INC_R8)
        assertOpcode(0x34, Opcode.INC_HL)
        assertOpcode(0x0C, Opcode.INC_R8)
        assertOpcode(0x1C, Opcode.INC_R8)
        assertOpcode(0x2C, Opcode.INC_R8)
        assertOpcode(0x3C, Opcode.INC_R8)

        assertOpcode(0x05, Opcode.DEC_R8)
        assertOpcode(0x15, Opcode.DEC_R8)
        assertOpcode(0x25, Opcode.DEC_R8)
        assertOpcode(0x35, Opcode.DEC_HL)
        assertOpcode(0x0D, Opcode.DEC_R8)
        assertOpcode(0x1D, Opcode.DEC_R8)
        assertOpcode(0x2D, Opcode.DEC_R8)
        assertOpcode(0x3D, Opcode.DEC_R8)

    }

}