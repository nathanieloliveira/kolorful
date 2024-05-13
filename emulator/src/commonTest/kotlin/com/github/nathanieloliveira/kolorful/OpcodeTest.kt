@file:OptIn(ExperimentalStdlibApi::class)

package com.github.nathanieloliveira.kolorful

import kotlin.test.Test

/**
 * reference: https://gbdev.io/gb-opcodes//optables/
 */
@OptIn(ExperimentalStdlibApi::class)
class OpcodeTest {

    private fun assertOpcode(byte: Int, expected: Opcode) {
        val decoded = Opcode.decode(byte.toByte())
        assert(decoded == expected) {
            "byte: ${byte.toHexString()}, expected: ${expected.name}, got: ${decoded.name}"
        }
    }

    @Test
    fun testOpcodes() {
        assertOpcode(0x00, Opcode.NOP)
        assertOpcode(0x10, Opcode.STOP)
        assertOpcode(0x20, Opcode.JR_CC_E8)
        assertOpcode(0x30, Opcode.JR_CC_E8)

        assertOpcode(0x01, Opcode.LD_R16_N16)
        assertOpcode(0x11, Opcode.LD_R16_N16)
        assertOpcode(0x21, Opcode.LD_R16_N16)
        assertOpcode(0x31, Opcode.LD_SP_N16)

        assertOpcode(0x02, Opcode.LD_R16_A)
        assertOpcode(0x12, Opcode.LD_R16_A)
        assertOpcode(0x22, Opcode.LDI_HL_A)
        assertOpcode(0x32, Opcode.LDD_HL_A)

        assertOpcode(0x03, Opcode.INC_R16)
        assertOpcode(0x13, Opcode.INC_R16)
        assertOpcode(0x23, Opcode.INC_R16)
        assertOpcode(0x33, Opcode.INC_SP)

        assertOpcode(0x04, Opcode.INC_R8)
        assertOpcode(0x14, Opcode.INC_R8)
        assertOpcode(0x24, Opcode.INC_R8)
        assertOpcode(0x34, Opcode.INC_HL)

        assertOpcode(0x05, Opcode.DEC_R8)
        assertOpcode(0x15, Opcode.DEC_R8)
        assertOpcode(0x25, Opcode.DEC_R8)
        assertOpcode(0x35, Opcode.DEC_HL)

        assertOpcode(0x06, Opcode.LD_R8_N8)
        assertOpcode(0x16, Opcode.LD_R8_N8)
        assertOpcode(0x26, Opcode.LD_R8_N8)
        assertOpcode(0x36, Opcode.LD_HL_N8)

        assertOpcode(0x07, Opcode.RLCA)
        assertOpcode(0x17, Opcode.RLA)
        assertOpcode(0x27, Opcode.DAA)
        assertOpcode(0x37, Opcode.SCF)

        assertOpcode(0x08, Opcode.LD_N16_SP)
        assertOpcode(0x18, Opcode.JR_E8)
        assertOpcode(0x28, Opcode.JR_CC_E8)
        assertOpcode(0x38, Opcode.JR_CC_E8)

        assertOpcode(0x09, Opcode.ADD_HL_R16)
        assertOpcode(0x19, Opcode.ADD_HL_R16)
        assertOpcode(0x29, Opcode.ADD_HL_R16)
        assertOpcode(0x39, Opcode.ADD_HL_SP)

        assertOpcode(0x0A, Opcode.LD_A_R16)
        assertOpcode(0x1A, Opcode.LD_A_R16)
        assertOpcode(0x2A, Opcode.LDI_A_HL)
        assertOpcode(0x3A, Opcode.LDD_A_HL)

        assertOpcode(0x0B, Opcode.DEC_R16)
        assertOpcode(0x1B, Opcode.DEC_R16)
        assertOpcode(0x2B, Opcode.DEC_R16)
        assertOpcode(0x3B, Opcode.DEC_SP)

        assertOpcode(0x0C, Opcode.INC_R8)
        assertOpcode(0x1C, Opcode.INC_R8)
        assertOpcode(0x2C, Opcode.INC_R8)
        assertOpcode(0x3C, Opcode.INC_R8)

        assertOpcode(0x0D, Opcode.DEC_R8)
        assertOpcode(0x1D, Opcode.DEC_R8)
        assertOpcode(0x2D, Opcode.DEC_R8)
        assertOpcode(0x3D, Opcode.DEC_R8)

        assertOpcode(0x0F, Opcode.RRCA)
        assertOpcode(0x1F, Opcode.RRA)
        assertOpcode(0x2F, Opcode.CPL)
        assertOpcode(0x3F, Opcode.CCF)

        assertOpcode(0x40, Opcode.LD_R8_R8)
        assertOpcode(0x50, Opcode.LD_R8_R8)
        assertOpcode(0x60, Opcode.LD_R8_R8)
        assertOpcode(0x70, Opcode.LD_HL_R8)

        assertOpcode(0x41, Opcode.LD_R8_R8)
        assertOpcode(0x51, Opcode.LD_R8_R8)
        assertOpcode(0x61, Opcode.LD_R8_R8)
        assertOpcode(0x71, Opcode.LD_HL_R8)

        assertOpcode(0x42, Opcode.LD_R8_R8)
        assertOpcode(0x52, Opcode.LD_R8_R8)
        assertOpcode(0x62, Opcode.LD_R8_R8)
        assertOpcode(0x72, Opcode.LD_HL_R8)

        assertOpcode(0x43, Opcode.LD_R8_R8)
        assertOpcode(0x53, Opcode.LD_R8_R8)
        assertOpcode(0x63, Opcode.LD_R8_R8)
        assertOpcode(0x73, Opcode.LD_HL_R8)

        assertOpcode(0x44, Opcode.LD_R8_R8)
        assertOpcode(0x54, Opcode.LD_R8_R8)
        assertOpcode(0x64, Opcode.LD_R8_R8)
        assertOpcode(0x74, Opcode.LD_HL_R8)

        assertOpcode(0x45, Opcode.LD_R8_R8)
        assertOpcode(0x55, Opcode.LD_R8_R8)
        assertOpcode(0x65, Opcode.LD_R8_R8)
        assertOpcode(0x75, Opcode.LD_HL_R8)

        assertOpcode(0x46, Opcode.LD_R8_HL)
        assertOpcode(0x56, Opcode.LD_R8_HL)
        assertOpcode(0x66, Opcode.LD_R8_HL)
        assertOpcode(0x76, Opcode.HALT)

        assertOpcode(0x47, Opcode.LD_R8_R8)
        assertOpcode(0x57, Opcode.LD_R8_R8)
        assertOpcode(0x67, Opcode.LD_R8_R8)
        assertOpcode(0x77, Opcode.LD_HL_R8)

        assertOpcode(0x48, Opcode.LD_R8_R8)
        assertOpcode(0x58, Opcode.LD_R8_R8)
        assertOpcode(0x68, Opcode.LD_R8_R8)
        assertOpcode(0x78, Opcode.LD_R8_R8)

        assertOpcode(0x49, Opcode.LD_R8_R8)
        assertOpcode(0x59, Opcode.LD_R8_R8)
        assertOpcode(0x69, Opcode.LD_R8_R8)
        assertOpcode(0x79, Opcode.LD_R8_R8)

        assertOpcode(0x4A, Opcode.LD_R8_R8)
        assertOpcode(0x5A, Opcode.LD_R8_R8)
        assertOpcode(0x6A, Opcode.LD_R8_R8)
        assertOpcode(0x7A, Opcode.LD_R8_R8)

        assertOpcode(0x4B, Opcode.LD_R8_R8)
        assertOpcode(0x5B, Opcode.LD_R8_R8)
        assertOpcode(0x6B, Opcode.LD_R8_R8)
        assertOpcode(0x7B, Opcode.LD_R8_R8)

        assertOpcode(0x4C, Opcode.LD_R8_R8)
        assertOpcode(0x5C, Opcode.LD_R8_R8)
        assertOpcode(0x6C, Opcode.LD_R8_R8)
        assertOpcode(0x7C, Opcode.LD_R8_R8)

        assertOpcode(0x4D, Opcode.LD_R8_R8)
        assertOpcode(0x5D, Opcode.LD_R8_R8)
        assertOpcode(0x6D, Opcode.LD_R8_R8)
        assertOpcode(0x7D, Opcode.LD_R8_R8)

        assertOpcode(0x4E, Opcode.LD_R8_HL)
        assertOpcode(0x5E, Opcode.LD_R8_HL)
        assertOpcode(0x6E, Opcode.LD_R8_HL)
        assertOpcode(0x7E, Opcode.LD_R8_HL)

        assertOpcode(0x4F, Opcode.LD_R8_R8)
        assertOpcode(0x5F, Opcode.LD_R8_R8)
        assertOpcode(0x6F, Opcode.LD_R8_R8)
        assertOpcode(0x7F, Opcode.LD_R8_R8)

        assertOpcode(0x80, Opcode.ADD_A_R8)
        assertOpcode(0x90, Opcode.SUB_A_R8)
        assertOpcode(0xA0, Opcode.AND_A_R8)
        assertOpcode(0xB0, Opcode.OR_A_R8)

        assertOpcode(0x81, Opcode.ADD_A_R8)
        assertOpcode(0x91, Opcode.SUB_A_R8)
        assertOpcode(0xA1, Opcode.AND_A_R8)
        assertOpcode(0xB1, Opcode.OR_A_R8)

        assertOpcode(0x82, Opcode.ADD_A_R8)
        assertOpcode(0x92, Opcode.SUB_A_R8)
        assertOpcode(0xA2, Opcode.AND_A_R8)
        assertOpcode(0xB2, Opcode.OR_A_R8)

        assertOpcode(0x83, Opcode.ADD_A_R8)
        assertOpcode(0x93, Opcode.SUB_A_R8)
        assertOpcode(0xA3, Opcode.AND_A_R8)
        assertOpcode(0xB3, Opcode.OR_A_R8)

        assertOpcode(0x84, Opcode.ADD_A_R8)
        assertOpcode(0x94, Opcode.SUB_A_R8)
        assertOpcode(0xA4, Opcode.AND_A_R8)
        assertOpcode(0xB4, Opcode.OR_A_R8)

        assertOpcode(0x85, Opcode.ADD_A_R8)
        assertOpcode(0x95, Opcode.SUB_A_R8)
        assertOpcode(0xA5, Opcode.AND_A_R8)
        assertOpcode(0xB5, Opcode.OR_A_R8)

        assertOpcode(0x86, Opcode.ADD_A_HL)
        assertOpcode(0x96, Opcode.SUB_A_HL)
        assertOpcode(0xA6, Opcode.AND_A_HL)
        assertOpcode(0xB6, Opcode.OR_A_HL)

        assertOpcode(0x87, Opcode.ADD_A_R8)
        assertOpcode(0x97, Opcode.SUB_A_R8)
        assertOpcode(0xA7, Opcode.AND_A_R8)
        assertOpcode(0xB7, Opcode.OR_A_R8)

        assertOpcode(0x88, Opcode.ADC_A_R8)
        assertOpcode(0x98, Opcode.SBC_A_R8)
        assertOpcode(0xA8, Opcode.XOR_A_R8)
        assertOpcode(0xB8, Opcode.CP_A_R8)

        assertOpcode(0x89, Opcode.ADC_A_R8)
        assertOpcode(0x99, Opcode.SBC_A_R8)
        assertOpcode(0xA9, Opcode.XOR_A_R8)
        assertOpcode(0xB9, Opcode.CP_A_R8)

        assertOpcode(0x8A, Opcode.ADC_A_R8)
        assertOpcode(0x9A, Opcode.SBC_A_R8)
        assertOpcode(0xAA, Opcode.XOR_A_R8)
        assertOpcode(0xBA, Opcode.CP_A_R8)

        assertOpcode(0x8B, Opcode.ADC_A_R8)
        assertOpcode(0x9B, Opcode.SBC_A_R8)
        assertOpcode(0xAB, Opcode.XOR_A_R8)
        assertOpcode(0xBB, Opcode.CP_A_R8)

        assertOpcode(0x8C, Opcode.ADC_A_R8)
        assertOpcode(0x9C, Opcode.SBC_A_R8)
        assertOpcode(0xAC, Opcode.XOR_A_R8)
        assertOpcode(0xBC, Opcode.CP_A_R8)

        assertOpcode(0x8D, Opcode.ADC_A_R8)
        assertOpcode(0x9D, Opcode.SBC_A_R8)
        assertOpcode(0xAD, Opcode.XOR_A_R8)
        assertOpcode(0xBD, Opcode.CP_A_R8)

        assertOpcode(0x8E, Opcode.ADC_A_HL)
        assertOpcode(0x9E, Opcode.SBC_A_HL)
        assertOpcode(0xAE, Opcode.XOR_A_HL)
        assertOpcode(0xBE, Opcode.CP_A_HL)

        assertOpcode(0x8F, Opcode.ADC_A_R8)
        assertOpcode(0x9F, Opcode.SBC_A_R8)
        assertOpcode(0xAF, Opcode.XOR_A_R8)
        assertOpcode(0xBF, Opcode.CP_A_R8)

        // div
        assertOpcode(0xC0, Opcode.RET_CC)
        assertOpcode(0xD0, Opcode.RET_CC)
        assertOpcode(0xE0, Opcode.LDH_N8_A)
        assertOpcode(0xF0, Opcode.LDH_A_N8)

        assertOpcode(0xC1, Opcode.POP_R16)
        assertOpcode(0xD1, Opcode.POP_R16)
        assertOpcode(0xE1, Opcode.POP_R16)
        assertOpcode(0xF1, Opcode.POP_R16)

        assertOpcode(0xC2, Opcode.JP_CC_N16)
        assertOpcode(0xD2, Opcode.JP_CC_N16)
        assertOpcode(0xE2, Opcode.LD_C_A)
        assertOpcode(0xF2, Opcode.LD_A_C)

        assertOpcode(0xC3, Opcode.JP_N16)
        assertOpcode(0xD3, Opcode.INVALID)
        assertOpcode(0xE3, Opcode.INVALID)
        assertOpcode(0xF3, Opcode.DI)

        assertOpcode(0xC4, Opcode.CALL_CC_N16)
        assertOpcode(0xD4, Opcode.CALL_CC_N16)
        assertOpcode(0xE4, Opcode.INVALID)
        assertOpcode(0xF4, Opcode.INVALID)

        assertOpcode(0xC5, Opcode.PUSH_R16)
        assertOpcode(0xD5, Opcode.PUSH_R16)
        assertOpcode(0xE5, Opcode.PUSH_R16)
        assertOpcode(0xF5, Opcode.PUSH_R16)

        assertOpcode(0xC6, Opcode.ADD_A_N8)
        assertOpcode(0xD6, Opcode.SUB_A_N8)
        assertOpcode(0xE6, Opcode.AND_A_N8)
        assertOpcode(0xF6, Opcode.OR_A_N8)

        assertOpcode(0xC7, Opcode.RST_VEC)
        assertOpcode(0xD7, Opcode.RST_VEC)
        assertOpcode(0xE7, Opcode.RST_VEC)
        assertOpcode(0xF7, Opcode.RST_VEC)

        assertOpcode(0xC8, Opcode.RET_CC)
        assertOpcode(0xD8, Opcode.RET_CC)
        assertOpcode(0xE8, Opcode.ADD_SP_E8)
        assertOpcode(0xF8, Opcode.LD_HL_SP_E8)

        assertOpcode(0xC9, Opcode.RET)
        assertOpcode(0xD9, Opcode.RETI)
        assertOpcode(0xE9, Opcode.JP_HL)
        assertOpcode(0xF9, Opcode.LD_SP_HL)

        assertOpcode(0xCA, Opcode.JP_CC_N16)
        assertOpcode(0xDA, Opcode.JP_CC_N16)
        assertOpcode(0xEA, Opcode.LD_A16_A)
        assertOpcode(0xFA, Opcode.LD_A_A16)

        assertOpcode(0xCB, Opcode.PREFIX)
        assertOpcode(0xDB, Opcode.INVALID)
        assertOpcode(0xEB, Opcode.INVALID)
        assertOpcode(0xFB, Opcode.EI)

        assertOpcode(0xCC, Opcode.CALL_CC_N16)
        assertOpcode(0xDC, Opcode.CALL_CC_N16)
        assertOpcode(0xEC, Opcode.INVALID)
        assertOpcode(0xFC, Opcode.INVALID)

        assertOpcode(0xCD, Opcode.CALL_N16)
        assertOpcode(0xDD, Opcode.INVALID)
        assertOpcode(0xED, Opcode.INVALID)
        assertOpcode(0xFD, Opcode.INVALID)

        assertOpcode(0xCE, Opcode.ADC_A_N8)
        assertOpcode(0xDE, Opcode.SBC_A_N8)
        assertOpcode(0xEE, Opcode.XOR_A_N8)
        assertOpcode(0xFE, Opcode.CP_A_N8)

        assertOpcode(0xCF, Opcode.RST_VEC)
        assertOpcode(0xDF, Opcode.RST_VEC)
        assertOpcode(0xEF, Opcode.RST_VEC)
        assertOpcode(0xFF, Opcode.RST_VEC)
    }

    private fun assertPrefixedOpcode(byte: Int, expected: PrefixedOpcode) {
        val decoded = PrefixedOpcode.decode(byte.toByte())
        assert(decoded == expected) {
            "byte: ${byte.toHexString()}, expected: ${expected.name}, got: ${decoded.name}"
        }
    }

    @Test
    fun testPrefixedOpcodes() {
        // line 0x0X
        for (byte in 0x00..0x05) {
            assertPrefixedOpcode(byte, PrefixedOpcode.RLC_R8)
        }
        assertPrefixedOpcode(0x06, PrefixedOpcode.RLC_HL)
        assertPrefixedOpcode(0x07, PrefixedOpcode.RLC_R8)

        for (byte in 0x08..0x0D) {
            assertPrefixedOpcode(byte, PrefixedOpcode.RRC_R8)
        }
        assertPrefixedOpcode(0x0E, PrefixedOpcode.RRC_HL)
        assertPrefixedOpcode(0x0F, PrefixedOpcode.RRC_R8)

        // line 0x1X
        for (byte in 0x10..0x15) {
            assertPrefixedOpcode(byte, PrefixedOpcode.RL_R8)
        }
        assertPrefixedOpcode(0x16, PrefixedOpcode.RL_HL)
        assertPrefixedOpcode(0x17, PrefixedOpcode.RL_R8)

        for (byte in 0x18..0x1D) {
            assertPrefixedOpcode(byte, PrefixedOpcode.RR_R8)
        }
        assertPrefixedOpcode(0x1E, PrefixedOpcode.RR_HL)
        assertPrefixedOpcode(0x1F, PrefixedOpcode.RR_R8)

        // line 0x2X
        for (byte in 0x20..0x25) {
            assertPrefixedOpcode(byte, PrefixedOpcode.SLA_R8)
        }
        assertPrefixedOpcode(0x26, PrefixedOpcode.SLA_HL)
        assertPrefixedOpcode(0x27, PrefixedOpcode.SLA_R8)

        for (byte in 0x28..0x2D) {
            assertPrefixedOpcode(byte, PrefixedOpcode.SRA_R8)
        }
        assertPrefixedOpcode(0x2E, PrefixedOpcode.SRA_HL)
        assertPrefixedOpcode(0x2F, PrefixedOpcode.SRA_R8)

        // line 0x3X
        for (byte in 0x30..0x35) {
            assertPrefixedOpcode(byte, PrefixedOpcode.SWAP_R8)
        }
        assertPrefixedOpcode(0x36, PrefixedOpcode.SWAP_HL)
        assertPrefixedOpcode(0x37, PrefixedOpcode.SWAP_R8)

        for (byte in 0x38..0x3D) {
            assertPrefixedOpcode(byte, PrefixedOpcode.SRL_R8)
        }
        assertPrefixedOpcode(0x3E, PrefixedOpcode.SRL_HL)
        assertPrefixedOpcode(0x3F, PrefixedOpcode.SRL_R8)

        // BITS
        for (byte in 0x40..0x7F) {
            if ((byte and 0x06 == 0x06) && byte and 0x07 != 0x07) {
                assertPrefixedOpcode(byte, PrefixedOpcode.BIT_U3_HL)
            } else {
                assertPrefixedOpcode(byte, PrefixedOpcode.BIT_U3_R8)
            }
        }

        // RES
        for (byte in 0x80..0xBF) {
            if ((byte and 0x06 == 0x06) && byte and 0x07 != 0x07) {
                assertPrefixedOpcode(byte, PrefixedOpcode.RES_U3_HL)
            } else {
                assertPrefixedOpcode(byte, PrefixedOpcode.RES_U3_R8)
            }
        }

        // SET
        for (byte in 0xC0..0xFF) {
            if ((byte and 0x06 == 0x06) && byte and 0x07 != 0x07) {
                assertPrefixedOpcode(byte, PrefixedOpcode.SET_U3_HL)
            } else {
                assertPrefixedOpcode(byte, PrefixedOpcode.SET_U3_R8)
            }
        }
    }

    @Test
    fun dump1ByteOps() {
        for (opcode in Opcode.entries) {
            if (opcode.bytes == 3) {
                println(opcode)
            }
        }
    }

}