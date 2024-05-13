@file:OptIn(ExperimentalStdlibApi::class)

package com.github.nathanieloliveira.kolorful

/**
 * Game Boy CPU opcodes.
 *
 * reference: https://gbdev.io/gb-opcodes//optables/
 */
enum class Opcode(
    val cycles: Int,
    val bytes: Int,
    val code: UByte,
    val mask: UByte,
) {
    PREFIX(1, 1, 0xCBu, 0xFFu),

    NOP(1, 1, 0b0000_0000u, 0b1111_1111u),
    HALT(Int.MAX_VALUE, 1, 0b0111_0110u, 0b1111_1111u), // SLEEP UNTIL INTERRUPT
    STOP(Int.MAX_VALUE, 2, 0b0001_0000u, 0b1111_1111u),

    EI(1, 1, 0b1111_1011u, 0b1111_1111u),
    DI(1, 1, 0b1111_0011u, 0b1111_1111u),

    ADC_A_HL(2, 1, 0b1000_1110u, 0b1111_1111u),
    ADC_A_N8(2, 2, 0b1100_1110u, 0b1111_1111u),
    ADC_A_R8(1, 1, 0b1000_1000u, 0b1111_1000u),

    ADD_A_HL(2, 1, 0b1000_0110u, 0b1111_1111u),
    ADD_A_R8(1, 1, 0b1000_0000u, 0b1111_1000u),
    ADD_A_N8(2, 2, 0b1100_0110u, 0b1111_1111u),
    ADD_HL_SP(2, 1, 0b0011_1001u, 0b1111_1111u),
    ADD_HL_R16(2, 1, 0b0000_1001u, 0b1100_1111u),
    ADD_SP_E8(4, 2, 0b1110_1000u, 0b1111_1111u),

    AND_A_HL(2, 1, 0b1010_0110u, 0b1111_1111u),
    AND_A_N8(2, 2, 0b1110_0110u, 0b1111_1111u),
    AND_A_R8(1, 1, 0b1010_0000u, 0b1111_1000u),

    CALL_N16(6, 3, 0b1100_1101u, 0b1111_1111u),
    CALL_CC_N16(6, 3, 0b1100_0100u, 0b1110_0111u), // TODO branch not taken costs 3 cycles

    CCF(1, 1, 0b0011_1111u, 0b1111_1111u),
    CP_A_HL(2, 1, 0b1011_1110u, 0b1111_1111u),
    CP_A_N8(2, 2, 0b1111_1110u, 0b1111_1111u),
    CPL(1, 1, 0b0010_1111u, 0b1111_1111u),
    CP_A_R8(1, 1, 0b1011_1000u, 0b1111_1000u),

    DAA(1, 1, 0b0010_0111u, 0b1111_1111u),
    DEC_HL(3, 1, 0b0011_0101u, 0b1111_1111u),
    DEC_SP(2, 1, 0b0011_1011u, 0b1111_1111u),
    DEC_R8(1, 1, 0b0000_0101u, 0b1100_0111u),
    DEC_R16(2, 1, 0b0000_1011u, 0b1100_1111u),

    INC_HL(3, 1, 0b0011_0100u, 0b1111_1111u),
    INC_SP(2, 1, 0b0011_0011u, 0b1111_1111u),
    INC_R8(1, 1, 0b0000_0100u, 0b1100_0111u),
    INC_R16(2, 1, 0b0000_0011u, 0b1100_1111u),

    JP_N16(4, 3, 0b1100_0011u, 0b1111_1111u),
    JP_HL(1, 1, 0b1110_1001u, 0b1111_1111u),
    JP_CC_N16(4, 3, 0b1100_0010u, 0b1110_0111u), // TODO branch not taken costs 3 cycles

    JR_E8(3, 2, 0b0001_1000u, 0b1111_1111u),
    JR_CC_E8(3, 2, 0b0010_0000u, 0b1110_0111u), // TODO branch not taken costs 2 cycles

    LDI_HL_A(2, 1, 0b0010_0010u, 0b1111_1111u),
    LDI_A_HL(2, 1, 0b0010_1010u, 0b1111_1111u),

    LDD_HL_A(2, 1, 0b0011_0010u, 0b1111_1111u),
    LDD_A_HL(2, 1, 0b0011_1010u, 0b1111_1111u),

    LD_HL_N8(3, 2, 0b0011_0110u, 0b1111_1111u),
    LD_R8_HL(2, 1, 0b0100_0110u, 0b1100_0111u),

    LD_SP_N16(3, 3, 0b0011_0001u, 0b1111_1111u),
    LD_HL_R8(2, 1, 0b0111_0000u, 0b1111_1000u),
    LD_R8_R8(1, 1, 0b0100_0000u, 0b1100_0000u),
    LD_R8_N8(2, 2, 0b0000_0110u, 0b1100_0111u),
    LD_R16_N16(3, 3, 0b0000_0001u, 0b1100_1111u),

    LD_R16_A(2, 1, 0b0000_0010u, 0b1100_1111u),
    LD_A_R16(2, 1, 0b0000_1010u, 0b1100_1111u),

    LD_A16_A(4, 3, 0b1110_1010u, 0b1111_1111u),
    LD_A_A16(4, 3, 0b1111_1010u, 0b1111_1111u),

    LD_N16_SP(5, 3, 0b0000_1000u, 0b1111_1111u),

    LD_HL_SP_E8(3, 2, 0b1111_1000u, 0b1111_1111u),
    LD_SP_HL(2, 1, 0b1111_1001u, 0b1111_1111u),

    LD_C_A(2, 1, 0b1110_0010u, 0b1111_1111u),
    LD_A_C(2, 1, 0b1111_0010u, 0b1111_1111u),

    LDH_N8_A(3, 2, 0b1110_0000u, 0b1111_1111u),
    LDH_A_N8(3, 2, 0b1111_0000u, 0b1111_1111u),

    OR_A_HL(2, 1, 0b1011_0110u, 0b1111_1111u),
    OR_A_N8(2, 2, 0b1111_0110u, 0b1111_1111u),
    OR_A_R8(1, 1, 0b1011_0000u, 0b1111_1000u),

    POP_R16(3, 1, 0b1100_0001u, 0b1100_1111u),

    PUSH_R16(4, 1, 0b1100_0101u, 0b1100_1111u),

    RET(4, 1, 0b1100_1001u, 0b1111_1111u),
    RET_CC(5, 1, 0b1100_0000u, 0b1110_0111u), // TODO branch not taken costs 2 cycles
    RETI(4, 1, 0b1101_1001u, 0b1111_1111u),

    RST_VEC(4, 1, 0b1100_0111u, 0b1100_0111u),

    SBC_A_HL(2, 1, 0b1001_1110u, 0b1111_1111u),
    SBC_A_N8(2, 2, 0b1101_1110u, 0b1111_1111u),
    SBC_A_R8(1, 1, 0b1001_1000u, 0b1111_1000u),

    SCF(1, 1, 0b0011_0111u, 0b1111_1111u),

    SUB_A_HL(2, 1, 0b1001_0110u, 0b1111_1111u),
    SUB_A_N8(2, 2, 0b1101_0110u, 0b1111_1111u),
    SUB_A_R8(1, 1, 0b1001_0000u, 0b1111_1000u),

    XOR_A_HL(2, 1, 0b1010_1110u, 0b1111_1111u),
    XOR_A_N8(2, 2, 0b1110_1110u, 0b1111_1111u),
    XOR_A_R8(1, 1, 0b1010_1000u, 0b1111_1000u),

    RLCA(1, 1, 0b0000_0111u, 0b1111_1111u),
    RRCA(1, 1, 0b0000_1111u, 0b1111_1111u),
    RLA(1, 1, 0b0001_0111u, 0b1111_1111u),
    RRA(1, 1, 0b0001_1111u, 0b1111_1111u),

    INVALID(Int.MAX_VALUE, Int.MAX_VALUE, 0x00u, 0x00u), // note: this will match anything
    ;

    companion object {
        fun decode(byte: Byte): Opcode {
            val b = byte.toUByte()
            return Opcode.entries.find { op ->
                (b and op.mask) == op.code
            }!!
        }

        fun decode(byte: UByte): Opcode {
            return decode(byte.toByte())
        }
    }
}

enum class PrefixedOpcode(
    val cycles: Int,
    val bytes: Int,
    val code: UByte,
    val mask: UByte,
) {
    // PREFIXED
    RLC_HL(4, 2, 0b0000_0110u, 0b1111_1111u),
    RLC_R8(2, 2, 0b0000_0000u, 0b1111_1000u),

    RRC_HL(4, 2, 0b0000_1110u, 0b1111_1111u),
    RRC_R8(2, 2, 0b0000_1000u, 0b1111_1000u),

    RL_HL(4, 2, 0b0001_0110u, 0b1111_1111u),
    RL_R8(2, 2, 0b0001_0000u, 0b1111_1000u),

    RR_HL(4, 2, 0b0001_1110u, 0b1111_1111u),
    RR_R8(2, 2, 0b0001_1000u, 0b1111_1000u),

    SLA_HL(4, 2, 0b0010_0110u, 0b1111_1111u),
    SLA_R8(2, 2, 0b0010_0000u, 0b1111_1000u),

    SRA_HL(4, 2, 0b0010_1110u, 0b1111_1111u),
    SRA_R8(2, 2, 0b0010_1000u, 0b1111_1000u),

    SWAP_HL(4, 2, 0b0011_0110u, 0b1111_1111u),
    SWAP_R8(2, 2, 0b0011_0000u, 0b1111_1000u),

    SRL_HL(3, 2, 0b0011_1110u, 0b1111_1111u),
    SRL_R8(2, 2, 0b0011_1000u, 0b1111_1000u),

    BIT_U3_HL(4, 2, 0b0100_0110u, 0b1100_0111u),
    BIT_U3_R8(2, 2, 0b0100_0000u, 0b1100_0000u),

    RES_U3_HL(4, 2, 0b1000_0110u, 0b1100_0111u),
    RES_U3_R8(2, 2, 0b1000_0000u, 0b1100_0000u),

    SET_U3_HL(4, 2, 0b1100_0110u, 0b1100_0111u),
    SET_U3_R8(2, 2, 0b1100_0000u, 0b1100_0000u),
    INVALID(Int.MAX_VALUE, Int.MAX_VALUE, 0x00u, 0x00u),
    ;

    companion object {
        fun decode(byte: Byte): PrefixedOpcode {
            val b = byte.toUByte()
            return PrefixedOpcode.entries.find { op ->
                (b and op.mask) == op.code
            }!!
        }

        fun decode(byte: UByte): PrefixedOpcode {
            return decode(byte.toByte())
        }
    }
}

enum class Register(val mnemonic: String) {
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    H("H"),
    L("L"),
    HL_ADDR("[HL]"),
    A("A"),
    BC("BC"),
    DE("DE"),
    HL("HL"),
    SP("SP"),
    PC("PC"),
    AF("AF"),
    ;

    companion object {

        fun decodeR8(byte: UByte, shift: Int = 3): Register {
            val shifted = byte.toInt() shr shift and 0x07
            return Register.entries[shifted]
        }

        fun decodeR16(byte: UByte): Register {
            val shifted = byte.toInt() shr 4 and 0x03
            return Register.entries[8 + shifted]
        }

    }
}

enum class Condition(val cond: UByte, val mnemonic: String) {
    NOT_ZERO(0x00u, "NZ"),
    ZERO(0x01u, "Z"),
    NOT_CARRY(0x10u, "NC"),
    CARRY(0x11u, "C"),
    ;

    companion object {
        fun decode(byte: UByte): Condition {
            val shifted = byte.toInt() shr 3 and 0x03
            return Condition.entries[shifted]
        }
    }
}

val hexFormat = HexFormat {
    upperCase = true
    number.prefix = "0x"
}

fun UByte.hex(): String {
    return toHexString(hexFormat)
}

fun UShort.hex(): String {
    return toHexString(hexFormat)
}

sealed class Instruction(val opcode: Opcode)

data object Nop: Instruction(Opcode.NOP) {
    override fun toString() = "NOP"
}
data object Halt: Instruction(Opcode.HALT) {
    override fun toString() = "HALT"
}
data class Stop(val immediate: UByte): Instruction(Opcode.STOP) {
    override fun toString() = "STOP ${immediate.hex()}"
}
data object Ei: Instruction(Opcode.EI) {
    override fun toString() = "EI"
}
data object Di: Instruction(Opcode.DI) {
    override fun toString() = "DI"
}
data object AdcAHl: Instruction(Opcode.ADC_A_HL) {
    override fun toString() = "ADC A, [HL]"
}
data object AddAHl: Instruction(Opcode.ADD_A_HL) {
    override fun toString() = "ADD A, [HL]"
}
data object AddHlSp: Instruction(Opcode.ADD_HL_SP) {
    override fun toString() = "ADD HL, SP"
}
data object AndAHl: Instruction(Opcode.AND_A_HL) {
    override fun toString() = "AND A, [HL]"
}
data object Ccf: Instruction(Opcode.CCF) {
    override fun toString() = "CCF"
}
data object CpAHl: Instruction(Opcode.CP_A_HL) {
    override fun toString() = "CP A, [HL]"
}
data object Cpl: Instruction(Opcode.CPL) {
    override fun toString() = "CPL"
}
data object Daa: Instruction(Opcode.DAA) {
    override fun toString() = "DAA"
}
data object DecHl: Instruction(Opcode.DEC_HL) {
    override fun toString() = "DEC [HL]"
}
data object DecSp: Instruction(Opcode.DEC_SP) {
    override fun toString() = "DEC SP"
}
data object IncHl: Instruction(Opcode.INC_HL) {
    override fun toString() = "INC [HL]"
}
data object IncSp: Instruction(Opcode.INC_SP) {
    override fun toString() = "INC SP"
}
data object JpHl: Instruction(Opcode.JP_HL) {
    override fun toString() = "JP HL"
}
data object LdiHlA: Instruction(Opcode.LDI_HL_A) {
    override fun toString() = "LD [HL+], A"
}
data object LdiAHl: Instruction(Opcode.LDI_A_HL) {
    override fun toString() = "LD A, [HL+]"
}
data object LddHlA: Instruction(Opcode.LDD_HL_A) {
    override fun toString() = "LD [HL-], A"
}
data object LddAHl: Instruction(Opcode.LDD_A_HL) {
    override fun toString() = "LD A, [HL-]"
}
data object LdSpHl: Instruction(Opcode.LD_SP_HL) {
    override fun toString() = "LD SP, HL"
}
data object LdCA: Instruction(Opcode.LD_C_A) {
    override fun toString() = "LD [C], A"
}
data object LdAC: Instruction(Opcode.LD_A_C) {
    override fun toString() = "LD A, [C]"
}
data object OrAHl: Instruction(Opcode.OR_A_HL) {
    override fun toString() = "OR A, [HL]"
}
data object Ret: Instruction(Opcode.RET) {
    override fun toString() = "RET"
}
data object RetI: Instruction(Opcode.RETI) {
    override fun toString() = "RETI"
}
data object SbcAHl: Instruction(Opcode.SBC_A_HL) {
    override fun toString() = "SBC A, [HL]"
}
data object Scf: Instruction(Opcode.SCF) {
    override fun toString() = "SCF"
}
data object SubAHl: Instruction(Opcode.SUB_A_HL) {
    override fun toString() = "SUB A, [HL]"
}
data object XorAHl: Instruction(Opcode.XOR_A_HL) {
    override fun toString() = "XOR A, [HL]"
}
data object RlcA: Instruction(Opcode.RLCA) {
    override fun toString() = "RLCA"
}
data object RrcA: Instruction(Opcode.RRCA) {
    override fun toString() = "RRCA"
}
data object RlA: Instruction(Opcode.RLA) {
    override fun toString() = "RLA"
}
data object RrA: Instruction(Opcode.RRA) {
    override fun toString() = "RRA"
}

data class AdcAR8(val operand: Register): Instruction(Opcode.ADC_A_R8) {
    override fun toString() = "ADC A, ${operand.mnemonic}"
}
data class AddAR8(val operand: Register): Instruction(Opcode.ADD_A_R8) {
    override fun toString() = "ADD A, ${operand.mnemonic}"
}
data class AddHlR16(val operand: Register): Instruction(Opcode.ADD_HL_R16) {
    override fun toString() = "ADC HL, ${operand.mnemonic}"
}
data class AndAR8(val operand: Register): Instruction(Opcode.AND_A_R8) {
    override fun toString() = "AND A, ${operand.mnemonic}"
}
data class CpAR8(val operand: Register): Instruction(Opcode.CP_A_R8) {
    override fun toString() = "CP A, ${operand.mnemonic}"
}
data class DecR8(val operand: Register): Instruction(Opcode.DEC_R8) {
    override fun toString() = "DEC ${operand.mnemonic}"
}
data class DecR16(val operand: Register): Instruction(Opcode.DEC_R16) {
    override fun toString() = "DEC ${operand.mnemonic}"
}
data class IncR8(val operand: Register): Instruction(Opcode.INC_R8) {
    override fun toString() = "INC ${operand.mnemonic}"
}
data class IncR16(val operand: Register): Instruction(Opcode.INC_R16) {
    override fun toString() = "INC ${operand.mnemonic}"
}
data class LdR8Hl(val destination: Register): Instruction(Opcode.LD_R8_HL) {
    override fun toString() = "LD ${destination.mnemonic}, [HL]"
}
data class LdHlR8(val source: Register): Instruction(Opcode.LD_HL_R8) {
    override fun toString() = "LD [HL], ${source.mnemonic}"
}

data class LdR8R8(val source: Register, val destination: Register): Instruction(Opcode.LD_R8_R8) {
    override fun toString() = "LD ${destination.mnemonic}, ${source.mnemonic}"
}
data class LdR16A(val destination: Register): Instruction(Opcode.LD_R16_A) {
    override fun toString() = "LD [${destination.mnemonic}], A"
}
data class LdAR16(val source: Register): Instruction(Opcode.LD_A_R16) {
    override fun toString() = "LD A, [${source.mnemonic}]"
}
data class LdA16A(val destAddress: UShort): Instruction(Opcode.LD_A16_A) {
    override fun toString() = "LD [${destAddress.hex()}], A"
}
data class LdAA16(val sourceAddress: UShort): Instruction(Opcode.LD_A_A16) {
    override fun toString() = "LD A, [${sourceAddress.hex()}]"
}
data class OrAR8(val operand: Register): Instruction(Opcode.OR_A_R8) {
    override fun toString() = "OR A, ${operand.mnemonic}"
}
data class PopR16(val operand: Register): Instruction(Opcode.POP_R16) {
    override fun toString() = "POP ${operand.mnemonic}"
}
data class PushR16(val operand: Register): Instruction(Opcode.PUSH_R16) {
    override fun toString() = "PUSH ${operand.mnemonic}"
}
data class RstVec(val target: UByte): Instruction(Opcode.RST_VEC) {
    override fun toString() = "RST \$$target"
}
data class SbcAR8(val operand: Register): Instruction(Opcode.SBC_A_R8) {
    override fun toString() = "SBC A, ${operand.mnemonic}"
}
data class SubAR8(val operand: Register): Instruction(Opcode.SUB_A_R8) {
    override fun toString() = "SUB A, ${operand.mnemonic}"
}
data class XorAR8(val operand: Register): Instruction(Opcode.XOR_A_R8) {
    override fun toString() = "XOR A, ${operand.mnemonic}"
}

data class RetCc(val condition: Condition): Instruction(Opcode.RET_CC) {
    override fun toString() = "RET ${condition.mnemonic}"
}

data class AdcAN8(val immediate: UByte): Instruction(Opcode.ADC_A_R8) {
    override fun toString() = "ADC A, ${immediate.hex()}"
}
data class AddAN8(val immediate: UByte): Instruction(Opcode.ADD_A_N8) {
    override fun toString() = "ADD A, ${immediate.hex()}"
}
data class AddSpE8(val immediate: Byte): Instruction(Opcode.ADD_SP_E8) {
    override fun toString() = "ADD SP, $immediate"
}
data class AndAN8(val immediate: UByte): Instruction(Opcode.AND_A_N8) {
    override fun toString() = "AND A, ${immediate.hex()}"
}
data class CpAN8(val immediate: UByte): Instruction(Opcode.CP_A_N8) {
    override fun toString() = "CP A, ${immediate.hex()}"
}
data class JrE8(val immediate: Byte): Instruction(Opcode.JR_E8) {
    override fun toString() = "JR $immediate"
}
data class JrCcE8(val condition: Condition, val immediate: Byte): Instruction(Opcode.JR_CC_E8) {
    override fun toString() = "JR ${condition.mnemonic}, $immediate"
}
data class LdHlN8(val immediate: UByte): Instruction(Opcode.LD_HL_N8) {
    override fun toString() = "Ld [Hl], ${immediate.hex()}"
}
data class LdR8N8(val destination: Register, val immediate: UByte): Instruction(Opcode.LD_R8_N8) {
    override fun toString() = "LD ${destination.mnemonic}, ${immediate.hex()}"
}
data class LdHlSpE8(val immediate: Byte): Instruction(Opcode.LD_HL_SP_E8) {
    override fun toString() = "LD HL, SP + $immediate"
}
data class LdhN8A(val immediate: UByte): Instruction(Opcode.LDH_N8_A) {
    override fun toString() = "LD [\$FF${immediate.toHexString(HexFormat.UpperCase)}], A"
}
data class LdhAN8(val immediate: UByte): Instruction(Opcode.LDH_A_N8) {
    override fun toString() = "LD A, [\$FF${immediate.toHexString(HexFormat.UpperCase)}]"
}
data class OrAN8(val immediate: UByte): Instruction(Opcode.OR_A_N8) {
    override fun toString() = "OR A, ${immediate.hex()}"
}
data class SbcAN8(val immediate: UByte): Instruction(Opcode.SBC_A_N8) {
    override fun toString() = "SBC A, ${immediate.hex()}"
}
data class SubAN8(val immediate: UByte): Instruction(Opcode.SUB_A_N8) {
    override fun toString() = "SUB A, ${immediate.hex()}"
}
data class XorAN8(val immediate: UByte): Instruction(Opcode.XOR_A_N8) {
    override fun toString() = "XOR A, ${immediate.hex()}"
}

data class CallN16(val immediate: UShort): Instruction(Opcode.CALL_N16) {
    override fun toString(): String = "CALL ${immediate.hex()}"
}
data class CallCcN16(val condition: Condition, val immediate: UShort): Instruction(Opcode.CALL_CC_N16) {
    override fun toString() = "CALL ${condition.mnemonic} ${immediate.hex()}"
}
data class JpN16(val immediate: UShort): Instruction(Opcode.JP_N16) {
    override fun toString() = "JP ${immediate.hex()}"
}
data class JpCcN16(val condition: Condition, val immediate: UShort): Instruction(Opcode.JP_CC_N16) {
    override fun toString() = "JP ${condition.mnemonic} ${immediate.hex()}"
}
data class LdSpN16(val immediate: UShort): Instruction(Opcode.LD_SP_N16) {
    override fun toString() = "LD SP, ${immediate.hex()}"
}
data class LdR16N16(val destination: Register, val immediate: UShort): Instruction(Opcode.LD_R16_N16) {
    override fun toString() = "LD ${destination.mnemonic}, ${immediate.hex()}"
}
data class LdN16Sp(val immediate: UShort): Instruction(Opcode.LD_N16_SP) {
    override fun toString(): String = "LD [${immediate.hex()}], SP"
}

sealed class PrefixedInstruction(val opcode: PrefixedOpcode)

data object RlcHl: PrefixedInstruction(PrefixedOpcode.RLC_HL) {
    override fun toString() = "RLC [HL]"
}
data object RlHl: PrefixedInstruction(PrefixedOpcode.RL_HL) {
    override fun toString() = "RL [HL]"
}
data object RrcHl: PrefixedInstruction(PrefixedOpcode.RRC_HL) {
    override fun toString() = "RRC [HL]"
}
data object RrHl: PrefixedInstruction(PrefixedOpcode.RRC_HL) {
    override fun toString() = "RR [HL]"
}
data object SlaHl: PrefixedInstruction(PrefixedOpcode.SLA_HL) {
    override fun toString() = "SLA [HL]"
}
data object SraHl: PrefixedInstruction(PrefixedOpcode.SRA_HL) {
    override fun toString() = "SRA [HL]"
}
data object SwapHl: PrefixedInstruction(PrefixedOpcode.SWAP_HL) {
    override fun toString() = "SWAP [HL]"
}
data object SrlHl: PrefixedInstruction(PrefixedOpcode.SRL_HL) {
    override fun toString() = "SRL [HL]"
}
data class RlcR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.RLC_HL) {
    override fun toString() = "RLC ${operand.mnemonic}"
}
data class RlR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.RL_HL) {
    override fun toString() = "RL ${operand.mnemonic}"
}
data class RrcR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.RRC_R8) {
    override fun toString() = "RRC ${operand.mnemonic}"
}
data class RrR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.RR_R8) {
    override fun toString() = "RR ${operand.mnemonic}"
}
data class SlaR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.SLA_R8) {
    override fun toString() = "SLA ${operand.mnemonic}"
}
data class SraR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.SRA_R8) {
    override fun toString() = "SRA ${operand.mnemonic}"
}
data class SwapR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.SWAP_R8) {
    override fun toString() = "SWAP ${operand.mnemonic}"
}
data class SrlR8(val operand: Register): PrefixedInstruction(PrefixedOpcode.SRL_R8) {
    override fun toString() = "SRL ${operand.mnemonic}"
}

data class BitU3Hl(val bit: UByte): PrefixedInstruction(PrefixedOpcode.BIT_U3_HL) {
    override fun toString() = "BIT $bit, [HL]"
}
data class ResU3Hl(val bit: UByte): PrefixedInstruction(PrefixedOpcode.RES_U3_HL) {
    override fun toString() = "RES $bit, [HL]"
}
data class SetU3Hl(val bit: UByte): PrefixedInstruction(PrefixedOpcode.SET_U3_HL) {
    override fun toString() = "SET $bit, [HL]"
}
data class BitU3R8(val bit: UByte, val operand: Register): PrefixedInstruction(PrefixedOpcode.BIT_U3_R8) {
    override fun toString() = "BIT $bit, ${operand.mnemonic}"
}
data class ResU3R8(val bit: UByte, val operand: Register): PrefixedInstruction(PrefixedOpcode.RES_U3_R8) {
    override fun toString() = "RES $bit, ${operand.mnemonic}"
}
data class SetU3R8(val bit: UByte, val operand: Register): PrefixedInstruction(PrefixedOpcode.SET_U3_R8) {
    override fun toString() = "SET $bit, ${operand.mnemonic}"
}

data class RegisterAffected(
    val write: Register? = null,
    val read: Register? = null,
)

fun Instruction.affectedRegisters(): RegisterAffected {
    return when (this) {
        AdcAHl -> RegisterAffected(Register.A, Register.HL)
        is AdcAN8 -> RegisterAffected(Register.A, Register.HL)
        is AdcAR8 -> RegisterAffected(Register.A, this.operand)
        AddAHl -> RegisterAffected(Register.A, Register.HL)
        is AddAN8 -> RegisterAffected(Register.A)
        is AddAR8 -> RegisterAffected(Register.A, this.operand)
        is AddHlR16 -> RegisterAffected(Register.HL, this.operand)
        AddHlSp -> RegisterAffected(Register.HL, Register.SP)
        is AddSpE8 -> RegisterAffected(Register.SP)
        AndAHl -> RegisterAffected(Register.A, Register.HL)
        is AndAN8 -> RegisterAffected(Register.A)
        is AndAR8 -> RegisterAffected(Register.A, this.operand)
        is CallCcN16 -> RegisterAffected(Register.PC)
        is CallN16 -> RegisterAffected(Register.PC)
        Ccf -> RegisterAffected()
        CpAHl -> RegisterAffected(Register.A, Register.HL)
        is CpAN8 -> RegisterAffected(Register.A)
        is CpAR8 -> RegisterAffected(Register.A, this.operand)
        Cpl -> RegisterAffected(Register.A)
        Daa -> RegisterAffected(Register.A)
        DecHl -> RegisterAffected(Register.HL)
        is DecR16 -> RegisterAffected(this.operand)
        is DecR8 -> RegisterAffected(this.operand)
        DecSp -> RegisterAffected(Register.SP)
        Di -> RegisterAffected()
        Ei -> RegisterAffected()
        Halt -> RegisterAffected()
        IncHl -> RegisterAffected(Register.HL)
        is IncR16 -> RegisterAffected(this.operand)
        is IncR8 -> RegisterAffected(this.operand)
        IncSp -> RegisterAffected(Register.SP)
        is JpCcN16 -> RegisterAffected(Register.PC)
        JpHl -> RegisterAffected(Register.PC, Register.HL)
        is JpN16 -> RegisterAffected(Register.PC)
        is JrCcE8 -> RegisterAffected(Register.PC)
        is JrE8 -> RegisterAffected(Register.PC)
        is LdA16A -> RegisterAffected(Register.A)
        is LdAA16 -> RegisterAffected(Register.A)
        LdAC -> RegisterAffected(Register.A, Register.C)
        is LdAR16 -> RegisterAffected(Register.A, this.source)
        LdCA -> RegisterAffected(Register.A, Register.C)
        is LdHlN8 -> RegisterAffected(Register.HL)
        is LdHlR8 -> RegisterAffected(Register.HL, this.source)
        is LdHlSpE8 -> RegisterAffected(Register.HL, Register.SP)
        is LdN16Sp -> RegisterAffected(write = null, Register.SP)
        is LdR16A -> RegisterAffected(write = null, Register.A)
        is LdR16N16 -> RegisterAffected(this.destination)
        is LdR8Hl -> RegisterAffected(this.destination, Register.HL)
        is LdR8N8 -> RegisterAffected(this.destination)
        is LdR8R8 -> RegisterAffected(this.destination)
        LdSpHl -> RegisterAffected(Register.SP, Register.HL)
        is LdSpN16 -> RegisterAffected(Register.SP)
        LddAHl -> RegisterAffected(Register.A, Register.HL)
        LddHlA -> RegisterAffected(Register.HL, Register.A)
        is LdhAN8 -> RegisterAffected(Register.A)
        is LdhN8A -> RegisterAffected(read = Register.A)
        LdiAHl -> RegisterAffected(Register.A, Register.HL)
        LdiHlA -> RegisterAffected(Register.HL, Register.A)
        Nop -> RegisterAffected()
        OrAHl -> RegisterAffected(Register.A, Register.HL)
        is OrAN8 -> RegisterAffected(Register.A)
        is OrAR8 -> RegisterAffected(Register.A, this.operand)
        is PopR16 -> RegisterAffected(Register.SP)
        is PushR16 -> RegisterAffected(Register.SP)
        Ret -> RegisterAffected(Register.PC)
        is RetCc -> RegisterAffected(Register.PC)
        RetI -> RegisterAffected(Register.PC)
        RlA -> RegisterAffected(Register.A)
        RlcA -> RegisterAffected(Register.A)
        RrA -> RegisterAffected(Register.A)
        RrcA -> RegisterAffected(Register.A)
        is RstVec -> RegisterAffected(Register.PC)
        SbcAHl -> RegisterAffected(Register.A, Register.HL)
        is SbcAN8 -> RegisterAffected(Register.A)
        is SbcAR8 -> RegisterAffected(Register.A, this.operand)
        Scf -> RegisterAffected(Register.A)
        is Stop -> RegisterAffected()
        SubAHl -> RegisterAffected(Register.A, Register.HL)
        is SubAN8 -> RegisterAffected(Register.A)
        is SubAR8 -> RegisterAffected(Register.A, this.operand)
        XorAHl -> RegisterAffected(Register.A, Register.HL)
        is XorAN8 -> RegisterAffected(Register.A)
        is XorAR8 -> RegisterAffected(Register.A, this.operand)
    }
}