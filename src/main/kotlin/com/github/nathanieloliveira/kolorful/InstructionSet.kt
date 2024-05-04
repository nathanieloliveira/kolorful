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

    ADC_A_R8(1, 1, 0b1000_1000u, 0b1111_1000u),
    ADC_A_HL(2, 1, 0b1000_1110u, 0b1111_1111u),
    ADC_A_N8(2, 2, 0b1100_1110u, 0b1111_1111u),

    ADD_A_R8(1, 1, 0b1000_0000u, 0b1111_1000u),
    ADD_A_HL(2, 1, 0b1000_0110u, 0b1111_1111u),
    ADD_A_N8(2, 2, 0b1100_0110u, 0b1111_1111u),
    ADD_HL_R16(2, 1, 0b0000_1001u, 0b0011_0000u),
    ADD_HL_SP(2, 1, 0b0011_1001u, 0b1111_1111u),
    ADD_SP_E8(4, 2, 0b1110_1000u, 0b1111_1111u),

    AND_A_R8(1, 1, 0b1010_0000u, 0b1111_1000u),
    AND_A_HL(2, 1, 0b1010_0110u, 0b1111_1111u),
    AND_A_N8(2, 2, 0b1110_0110u, 0b1111_1111u),

    CALL_N16(6, 3, 0b1100_1101u, 0b1111_1111u),
    CALL_CC_N16(6, 3, 0b1100_0100u, 0b1110_0111u), // TODO branch not taken costs 3 cycles

    CCF(1, 1, 0b0011_1111u, 0b1111_1111u),
    CP_A_R8(1, 1, 0b1011_1000u, 0b1111_1000u),
    CP_A_HL(2, 1, 0b1011_1110u, 0b1111_1111u),
    CP_A_N8(2, 2, 0b1111_1110u, 0b1111_1111u),
    CPL(1, 1, 0b0010_1111u, 0b1111_1111u),

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

    LD_R8_R8(1, 1, 0b0100_0000u, 0b1100_0000u),
    LD_R8_N8(2, 2, 0b0000_0110u, 0b1100_0111u),
    LD_R16_N16(3, 3, 0b0000_0001u, 0b0011_0000u),
    LD_R16_A(2, 1, 0b0000_0010u, 0b0011_0000u),
    LD_N16_A(4, 3, 0b1110_1010u, 0b1111_1111u),

    LD_A_R16(2, 1, 0b0000_1010u, 0b0011_0000u),
    LD_A_N16(4, 3, 0b1111_1010u, 0b1111_1111u),

    LD_SP_N16(3, 3, 0b0011_0001u, 0b1111_1111u),
    LD_N16_SP(5, 3, 0b0000_1000u, 0b1111_1111u),

    LD_HL_R8(2, 1, 0b0111_0000u, 0b1111_1000u),
    LD_HL_N8(3, 2, 0b0011_0110u, 0b1111_1111u),
    LD_R8_HL(2, 1, 0b0100_0110u, 0b1100_0111u),

    LD_HL_SP_E8(3, 2, 0b1111_1000u, 0b1111_1111u),
    LD_SP_HL(2, 1, 0b1111_1001u, 0b1111_1111u),

    LDH_N8_A(3, 2, 0b1110_0000u, 0b1111_1111u),
    LDH_C_A(2, 1, 0b1110_0010u, 0b1111_1111u),

    LDH_A_N8(3, 2, 0b1111_0010u, 0b1111_1111u),
    LDH_A_C(2, 1, 0b1111_0010u, 0b1111_1111u),

    LDI_HL_A(2, 1, 0b0010_1010u, 0b1111_1111u),
    LDI_A_HL(2, 1, 0b0010_0010u, 0b1111_1111u),

    LDD_HL_A(2, 1, 0b0011_1010u, 0b1111_1111u),
    LDD_A_HL(2, 1, 0b0011_0010u, 0b1111_1111u),

    OR_A_R8(1, 1, 0b1011_0000u, 0b1111_1000u),
    OR_A_HL(2, 1, 0b1011_0110u, 0b1111_1111u),
    OR_A_N8(2, 2, 0b1111_0110u, 0b1111_1111u),

    POP_R16(3, 1, 0b1100_0001u, 0b1100_1111u),

    PUSH_R16(4, 1, 0b1100_0101u, 0b1100_1111u),

    RET(4, 1, 0b1100_1001u, 0b1111_1111u),
    RET_CC(5, 1, 0b1100_0000u, 0b1110_0111u), // TODO branch not taken costs 2 cycles
    RETI(4, 1, 0b1101_1001u, 0b1111_1111u),

    RST_VEC(4, 1, 0b1100_0111u, 0b1100_0111u),

    SBC_A_R8(1, 1, 0b1001_1000u, 0b1111_1000u),
    SBC_A_HL(2, 1, 0b1001_1110u, 0b1111_1111u),
    SBC_A_N8(2, 2, 0b1101_1110u, 0b1111_1111u),

    SCF(1, 1, 0b0011_0111u, 0b1111_1111u),

    SUB_A_R8(1, 1, 0b1001_0000u, 0b1111_1000u),
    SUB_A_HL(2, 1, 0b1001_0110u, 0b1111_1111u),
    SUB_A_N8(2, 2, 0b1101_0110u, 0b1111_1111u),

    XOR_A_R8(1, 1, 0b1010_1000u, 0b1111_1000u),
    XOR_A_HL(2, 1, 0b1010_1110u, 0b1111_1111u),
    XOR_A_N8(2, 2, 0b1110_1110u, 0b1111_1111u),

    RLCA(1, 1, 0b0000_0111u, 0b1111_1111u),
    RRCA(1, 1, 0b0000_1111u, 0b1111_1111u),
    RLA(1, 1, 0b0001_0111u, 0b1111_1111u),
    RRA(1, 1, 0b0001_1111u, 0b1111_1111u),

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

    INVALID(Int.MAX_VALUE, Int.MAX_VALUE, 0x00u, 0x00u), // note: this will match anything
    ;

    companion object {
        fun decode(byte: Byte): Opcode {
            val b = byte.toUByte()
            return Opcode.entries.find { op ->
                (b and op.mask) == op.code
            }!!
        }
    }
}

enum class Register {
    A,
    B,
    C,
    D,
    E,
    H,
    L,
    SP,
    PC,
    AF,
    BC,
    DE,
    HL,
}

sealed class Instruction(val opcode: Opcode)

data object NOP : Instruction(Opcode.NOP)

data class ADC_A_R8(val src: Register) : Instruction(Opcode.ADC_A_R8)
data object ADC_A_HL : Instruction(Opcode.ADC_A_HL)
data class ADC_A_N8(val operand: UByte) : Instruction(Opcode.ADC_A_N8)



