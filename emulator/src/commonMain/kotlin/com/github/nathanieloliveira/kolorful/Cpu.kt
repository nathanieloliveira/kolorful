package com.github.nathanieloliveira.kolorful

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.min

@OptIn(ExperimentalStdlibApi::class)
class Cpu(
    val bus: Bus,
    val trace: Boolean = true,
    var bootRom: ByteArray? = null,
) {

    companion object {
        const val FLAG_ZERO: UShort = 0x0080u
        const val FLAG_SUB: UShort = 0x0040u
        const val FLAG_HALF_CARRY: UShort = 0x0020u
        const val FLAG_CARRY: UShort = 0x0010u

        const val HRAM_OFFSET: UShort = 0xFF80u
        const val HRAM_END: UShort = 0xFFFEu

        const val BOOT_ROM_DISABLE = 0xFF50u

        const val INTERRUPT_ENABLE_ADDR = 0xFFFFu
        const val INTERRUPT_FLAG_ADDR = 0xFF0Fu

        const val INTERRUPT_JOYPAD = 0x10u
        const val INTERRUPT_SERIAL = 0x08u
        const val INTERRUPT_TIMER = 0x04u
        const val INTERRUPT_LCD = 0x02u
        const val INTERRUPT_VBLANK = 0x01u

        val allowedRstAddresses = setOf(
            0x0000u,
            0x0008u,
            0x0010u,
            0x0018u,
            0x0020u,
            0x0028u,
            0x0030u,
            0x0038u,
        )
    }

    enum class AluOp {
        ADD8,
        ADD16,
        SUB8,
        SUB16,
    }

    sealed interface RealInstruction
    data class Normal(val instruction: Instruction) : RealInstruction {
        override fun toString(): String {
            return instruction.toString()
        }
    }
    data class Prefixed(val instruction: PrefixedInstruction) : RealInstruction {
        override fun toString(): String {
            return instruction.toString()
        }
    }

    data class InstructionWithAddress(val instruction: RealInstruction?, val address: UShort)

    fun Instruction.toNormal() = Normal(this)
    fun PrefixedInstruction.toPrefixed() = Prefixed(this)

    var af: UShort = 0u
    var bc: UShort = 0u
    var de: UShort = 0u
    var hl: UShort = 0u

    var sp: UShort = 0u
    var pc: UShort = 0u

    /** Interrupt Master Enable */
    var ime: Boolean = false
    var interruptEnable: UInt = 0u
    var interruptFlag: UInt = 0u

    var a: UByte
        get() = (af.toInt() shr 8 and 0xFF).toUByte()
        set(value) {
            af = af and 0x00FFu or (value.toInt() shl 8).toUShort()
        }

    var b: UByte
        get() = (bc.toInt() shr 8 and 0xFF).toUByte()
        set(value) {
            bc = bc and 0x00FFu or (value.toInt() shl 8).toUShort()
        }

    var c: UByte
        get() = (bc.toInt() and 0xFF).toUByte()
        set(value) {
            bc = bc and 0xFF00u or (value.toInt()).toUShort()
        }

    var d: UByte
        get() = (de.toInt() shr 8 and 0xFF).toUByte()
        set(value) {
            de = de and 0x00FFu or (value.toInt() shl 8).toUShort()
        }

    var e: UByte
        get() = (de.toInt() and 0xFF).toUByte()
        set(value) {
            de = de and 0xFF00u or (value.toInt()).toUShort()
        }

    var h: UByte
        get() = (hl.toInt() shr 8 and 0xFF).toUByte()
        set(value) {
            hl = hl and 0x00FFu or (value.toInt() shl 8).toUShort()
        }

    var l: UByte
        get() = (hl.toInt() and 0xFF).toUByte()
        set(value) {
            hl = hl and 0xFF00u or (value.toInt()).toUShort()
        }

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

    var half: Boolean
        get() = af and FLAG_HALF_CARRY == FLAG_HALF_CARRY
        set(value) {
            af = setFlag(value, FLAG_HALF_CARRY)
        }

    var carry: Boolean
        get() = af and FLAG_CARRY == FLAG_CARRY
        set(value) {
            af = setFlag(value, FLAG_CARRY)
        }


    val cb: UByte get() = if (carry) 0x01u else 0x00u

    var isBoot = true

    val breakpoints = MutableStateFlow<Set<UShort>>(emptySet())

    fun addBreakpoint(addr: UShort) {
        breakpoints.update {
            val new = it.toMutableSet()
            new.add(addr)
            new
        }
    }

    fun removeBreakpoint(addr: UShort) {
        breakpoints.update {
            val new = it.toMutableSet()
            new.remove(addr)
            new
        }
    }

    fun setFlags(result: UInt, aluOp: AluOp) {
        z = when (aluOp) {
            AluOp.ADD8, AluOp.SUB8 -> result and 0xFFu == 0u
            AluOp.ADD16, AluOp.SUB16 -> result and 0xFFFFu == 0u
        }
        n = aluOp == AluOp.SUB8 || aluOp == AluOp.SUB16
        half = when (aluOp) {
            AluOp.ADD8, AluOp.SUB8 -> result > 0x0Fu
            AluOp.ADD16, AluOp.SUB16 -> result > 0x7FFu
        }
        carry = when (aluOp) {
            AluOp.ADD8, AluOp.SUB8 -> result > 0xFFu
            AluOp.ADD16, AluOp.SUB16 -> result > 0xFFFFu
        }
    }

    val hram = ByteArray(0x7F)

    fun readByte(address: UShort): UByte {
        val bankZeroRange = address.toInt() < 0x3FFF
        return when {
            address in HRAM_OFFSET..HRAM_END -> {
                // in HRAM
                hram[(address - HRAM_OFFSET).toInt()].toUByte()
            }

            address.toUInt() == INTERRUPT_FLAG_ADDR -> {
                interruptFlag.toUByte()
            }

            address.toUInt() == INTERRUPT_ENABLE_ADDR -> {
                interruptEnable.toUByte()
            }

            isBoot && bankZeroRange -> {
                bootRom!![address].toUByte()
            }

            else -> {
                bus.read(address)
            }
        }
    }

    fun writeByte(address: UShort, byte: UByte) {
        val bankZeroRange = address.toInt() < 0x3FFF
        when {
            address in HRAM_OFFSET..HRAM_END -> {
                // in HRAM
                hram[(address - HRAM_OFFSET).toInt()] = byte.toByte()
            }

            address.toUInt() == BOOT_ROM_DISABLE -> {
                println("DISABLE BOOT ROM")
                isBoot = byte <= 0x00u
            }

            address.toUInt() == INTERRUPT_FLAG_ADDR -> {
                interruptFlag = byte.toUInt()
                println("write IF=$byte")
            }

            address.toUInt() == INTERRUPT_ENABLE_ADDR -> {
                interruptEnable = byte.toUInt()
                println("write IE=$byte")
            }

            isBoot && bankZeroRange -> {
                error("trying to write into Boot ROM address. address=${address.toHexString()} value=${byte.toHexString()}")
            }

            else -> {
                bus.write(address, byte)
            }
        }
    }

    fun fetchByte(): UByte {
        return readByte(pc++)
    }

    fun fetch(fetchByte: () -> UByte): RealInstruction? {
        fun fetchUShort(): UShort {
            val leastSignificant = fetchByte()
            val mostSignificant = fetchByte()
            val short = (mostSignificant.toUInt() shl 8) or (leastSignificant.toUInt())
            return short.toUShort()
        }

        fun decodePrefixed(): Prefixed? {
            val prefixed = fetchByte()
            val prefixedOp = PrefixedOpcode.decode(prefixed)
            val reg = Register.decodeR8(prefixed, shift = 0)
            val bitIndex = (prefixed.toInt() shr 3 and 0x03).toUByte()
            return when (prefixedOp) {
                PrefixedOpcode.RLC_HL -> RlcHl
                PrefixedOpcode.RLC_R8 -> RlcR8(reg)
                PrefixedOpcode.RRC_HL -> RrcHl
                PrefixedOpcode.RRC_R8 -> RrcR8(reg)
                PrefixedOpcode.RL_HL -> RlHl
                PrefixedOpcode.RL_R8 -> RlR8(reg)
                PrefixedOpcode.RR_HL -> RrHl
                PrefixedOpcode.RR_R8 -> RrR8(reg)
                PrefixedOpcode.SLA_HL -> SlaHl
                PrefixedOpcode.SLA_R8 -> SlaR8(reg)
                PrefixedOpcode.SRA_HL -> SraHl
                PrefixedOpcode.SRA_R8 -> SraR8(reg)
                PrefixedOpcode.SWAP_HL -> SwapHl
                PrefixedOpcode.SWAP_R8 -> SwapR8(reg)
                PrefixedOpcode.SRL_HL -> SrlHl
                PrefixedOpcode.SRL_R8 -> SrlR8(reg)
                PrefixedOpcode.BIT_U3_HL -> BitU3Hl(bitIndex)
                PrefixedOpcode.BIT_U3_R8 -> BitU3R8(bitIndex, reg)
                PrefixedOpcode.RES_U3_HL -> ResU3Hl(bitIndex)
                PrefixedOpcode.RES_U3_R8 -> ResU3R8(bitIndex, reg)
                PrefixedOpcode.SET_U3_HL -> SetU3Hl(bitIndex)
                PrefixedOpcode.SET_U3_R8 -> SetU3R8(bitIndex, reg)
                PrefixedOpcode.INVALID -> return null
            }.toPrefixed()
        }

        val op = fetchByte()
        val dec = Opcode.decode(op)
        val regR8 = Register.decodeR8(op, shift = 0)
        val regR16 = Register.decodeR16(op)
        val condition = Condition.decode(op)
        val inst = when (dec) {
            Opcode.NOP -> Nop
            Opcode.HALT -> Halt
            Opcode.STOP -> Stop(fetchByte())
            Opcode.EI -> Ei
            Opcode.DI -> Di
            Opcode.ADC_A_HL -> AdcAHl
            Opcode.ADC_A_N8 -> {
                val immediate = fetchByte()
                AdcAN8(immediate)
            }

            Opcode.ADC_A_R8 -> AdcAR8(regR8)
            Opcode.ADD_A_HL -> AddAHl
            Opcode.ADD_A_R8 -> AddAR8(regR8)
            Opcode.ADD_A_N8 -> {
                val immediate = fetchByte()
                AddAN8(immediate)
            }

            Opcode.ADD_HL_SP -> AddHlSp
            Opcode.ADD_HL_R16 -> AddHlR16(regR16)
            Opcode.ADD_SP_E8 -> {
                val immediate = fetchByte()
                AddSpE8(immediate.toByte())
            }

            Opcode.AND_A_HL -> AndAHl
            Opcode.AND_A_N8 -> {
                val immediate = fetchByte()
                AndAN8(immediate)
            }

            Opcode.AND_A_R8 -> AndAR8(regR8)
            Opcode.CALL_N16 -> {
                val immediate = fetchUShort()
                CallN16(immediate)
            }

            Opcode.CALL_CC_N16 -> {
                val immediate = fetchUShort()
                CallCcN16(condition, immediate)
            }

            Opcode.CCF -> Ccf
            Opcode.CP_A_HL -> CpAHl
            Opcode.CP_A_N8 -> {
                val immediate = fetchByte()
                CpAN8(immediate)
            }

            Opcode.CPL -> Cpl
            Opcode.CP_A_R8 -> CpAR8(regR8)
            Opcode.DAA -> Daa
            Opcode.DEC_HL -> DecHl
            Opcode.DEC_SP -> DecSp
            Opcode.DEC_R8 -> {
                val reg = Register.decodeR8(op, shift = 3)
                DecR8(reg)
            }

            Opcode.DEC_R16 -> DecR16(regR16)
            Opcode.INC_HL -> IncHl
            Opcode.INC_SP -> IncSp
            Opcode.INC_R8 -> {
                val reg = Register.decodeR8(op, shift = 3)
                IncR8(reg)
            }

            Opcode.INC_R16 -> IncR16(regR16)
            Opcode.JP_N16 -> {
                val addr = fetchUShort()
                JpN16(addr)
            }

            Opcode.JP_HL -> JpHl
            Opcode.JP_CC_N16 -> {
                val addr = fetchUShort()
                JpCcN16(condition, addr)
            }

            Opcode.JR_E8 -> {
                val immediate = fetchByte()
                JrE8(immediate.toByte())
            }

            Opcode.JR_CC_E8 -> {
                val immediate = fetchByte()
                JrCcE8(condition, immediate.toByte())
            }

            Opcode.LDI_HL_A -> LdiHlA
            Opcode.LDI_A_HL -> LdiAHl
            Opcode.LDD_HL_A -> LddHlA
            Opcode.LDD_A_HL -> LddAHl
            Opcode.LD_HL_N8 -> {
                val immediate = fetchByte()
                LdHlN8(immediate)
            }

            Opcode.LD_R8_HL -> {
                val reg = Register.decodeR8(op, shift = 3)
                LdR8Hl(reg)
            }

            Opcode.LD_SP_N16 -> {
                val immediate = fetchUShort()
                LdSpN16(immediate)
            }

            Opcode.LD_HL_R8 -> LdHlR8(regR8)
            Opcode.LD_R8_R8 -> {
                val destination = Register.decodeR8(op, shift = 3)
                LdR8R8(regR8, destination)
            }

            Opcode.LD_R8_N8 -> {
                val destination = Register.decodeR8(op, shift = 3)
                val immediate = fetchByte()
                LdR8N8(destination, immediate)
            }

            Opcode.LD_R16_N16 -> {
                val immediate = fetchUShort()
                LdR16N16(regR16, immediate)
            }

            Opcode.LD_R16_A -> LdR16A(regR16)
            Opcode.LD_A_R16 -> LdAR16(regR16)

            Opcode.LD_A16_A -> {
                val immediate = fetchUShort()
                LdA16A(immediate)
            }

            Opcode.LD_A_A16 -> {
                val immediate = fetchUShort()
                LdAA16(immediate)
            }

            Opcode.LD_N16_SP -> {
                val immediate = fetchUShort()
                LdN16Sp(immediate)
            }

            Opcode.LD_HL_SP_E8 -> {
                val immediate = fetchByte()
                LdHlSpE8(immediate.toByte())
            }

            Opcode.LD_SP_HL -> LdSpHl
            Opcode.LD_C_A -> LdCA
            Opcode.LD_A_C -> LdAC
            Opcode.LDH_N8_A -> {
                val immediate = fetchByte()
                LdhN8A(immediate)
            }

            Opcode.LDH_A_N8 -> {
                val immediate = fetchByte()
                LdhAN8(immediate)
            }

            Opcode.OR_A_HL -> OrAHl
            Opcode.OR_A_N8 -> {
                val immediate = fetchByte()
                OrAN8(immediate)
            }

            Opcode.OR_A_R8 -> OrAR8(regR8)
            Opcode.POP_R16 -> PopR16(regR16)
            Opcode.PUSH_R16 -> PushR16(regR16)
            Opcode.RET -> Ret
            Opcode.RET_CC -> RetCc(condition)
            Opcode.RETI -> RetI
            Opcode.RST_VEC -> {
                val target = fetchByte()
                RstVec(target)
            }

            Opcode.SBC_A_HL -> SbcAHl
            Opcode.SBC_A_N8 -> {
                val immediate = fetchByte()
                SbcAN8(immediate)
            }

            Opcode.SBC_A_R8 -> SbcAR8(regR8)
            Opcode.SCF -> Scf
            Opcode.SUB_A_HL -> SubAHl
            Opcode.SUB_A_N8 -> {
                val immediate = fetchByte()
                SubAN8(immediate)
            }

            Opcode.SUB_A_R8 -> SubAR8(regR8)
            Opcode.XOR_A_HL -> XorAHl
            Opcode.XOR_A_N8 -> {
                val immediate = fetchByte()
                XorAN8(immediate)
            }

            Opcode.XOR_A_R8 -> XorAR8(regR8)
            Opcode.RLCA -> RlcA
            Opcode.RRCA -> RrcA
            Opcode.RLA -> RlA
            Opcode.RRA -> RrA
            Opcode.INVALID -> return null
            Opcode.PREFIX -> null
        }?.toNormal() ?: decodePrefixed()
        return inst
    }

    private fun call(label: UShort) {
        val nextInst = pc
        val least = (nextInst and 0xFFu).toUByte()
        val most = (nextInst.toUInt() and 0xFF00u shr 8).toUByte()
        sp = (sp - 1u).toUShort()
        writeByte(sp, most)
        sp = (sp - 1u).toUShort()
        writeByte(sp, least)
        pc = label
    }

    fun execute(instruction: RealInstruction) {

        fun ret() {
            val least = readByte(sp)
            sp = (sp + 1u).toUShort()
            val most = readByte(sp)
            sp = (sp + 1u).toUShort()
            val value = (most.toUInt() shl 8) or least.toUInt()
            pc = value.toUShort()
        }

        fun execute(instruction: Instruction) {
            when (instruction) {
                AdcAHl -> {
                    // Add the byte pointed to by HL plus the carry flag to A.
                    val operand = (readByte(hl) + cb).toUByte()
                    val result = a + operand
                    a = result.toUByte()
                    setFlags(result, AluOp.ADD8)
                }

                is AdcAN8 -> {
                    val result = instruction.immediate + a + cb
                    a = result.toUByte()
                    setFlags(result, AluOp.ADD8)
                }

                is AdcAR8 -> {
                    val result = readR8(instruction.operand) + a + cb
                    a = result.toUByte()
                    setFlags(result, AluOp.ADD8)
                }

                AddAHl -> {
                    val result = readByte(hl) + a
                    a = result.toUByte()
                    setFlags(result, AluOp.ADD8)
                }

                is AddAN8 -> {
                    val result = instruction.immediate + a
                    a = result.toUByte()
                    setFlags(result, AluOp.ADD8)
                }

                is AddAR8 -> {
                    val result = readR8(instruction.operand) + a
                    a = result.toUByte()
                    setFlags(result, AluOp.ADD8)
                }

                is AddHlR16 -> {
                    val result = hl + readR16(instruction.operand)
                    hl = result.toUShort()
                    setFlags(result, AluOp.ADD16)
                }

                AddHlSp -> {
                    val result = hl + sp
                    hl = result.toUShort()
                    setFlags(result, AluOp.ADD16)
                }

                is AddSpE8 -> {
                    val result = (sp.toInt() + instruction.immediate)
                    sp = result.toUShort()
                    z = false
                    n = false
                    carry = result > 0x7F
                    half = result > 0x07
                }

                AndAHl -> {
                    val result = readByte(hl) and a
                    a = result
                    z = result == 0u.toUByte()
                    n = false
                    half = true
                    carry = false
                }

                is AndAN8 -> {
                    val result = instruction.immediate and a
                    a = result
                    z = result == 0u.toUByte()
                    n = false
                    half = true
                    carry = false
                }

                is AndAR8 -> {
                    val result = readR8(instruction.operand) and a
                    a = result
                    z = result.toUByte() == 0u.toUByte()
                    n = false
                    half = true
                    carry = false
                }

                is CallCcN16 -> {
                    if (checkCondition(instruction.condition)) {
                        call(instruction.immediate)
                    }
                }

                is CallN16 -> {
                    call(instruction.immediate)
                }

                Ccf -> {
                    n = false
                    half = false
                    carry = !carry
                }

                CpAHl -> {
                    val result = a - readByte(hl)
                    setFlags(result, AluOp.SUB8)
                }

                is CpAN8 -> {
                    val result = a - instruction.immediate
                    setFlags(result, AluOp.SUB8)
                }

                is CpAR8 -> {
                    val result = a - readR8(instruction.operand)
                    setFlags(result, AluOp.SUB8)
                }

                Cpl -> {
                    a = a.inv()
                    n = true
                    half = true
                }

                Daa -> {
                    /**
                     * When this instruction is executed, the A register is BCD corrected using the contents of the flags.
                     * The exact process is the following:
                     * if the least significant four bits of A contain a non-BCD digit (i. e. it is greater than 9) or the H flag is set,
                     * then $06 is added to the register. Then the four most significant bits are checked.
                     * If this more significant digit also happens to be greater than 9 or the C flag is set, then $60 is added.
                     */
                    if ((a and 0x0Fu) > 0x09u || half) {
                        a = (a + 0x06u).toUByte()
                    }
                    val most = (a.toInt() and 0xF0 shr 4).toUByte()
                    if (most > 0x09u || carry) {
                        a = (a + 0x60u).toUByte()
                    }
                }

                DecHl -> {
                    val result = readByte(hl) - 1u
                    writeByte(hl, result.toUByte())
                    setFlags(result, AluOp.SUB8)
                }

                is DecR16 -> {
                    val result = readR16(instruction.operand) - 1u
                    writeR16(instruction.operand, result.toUShort())
                    setFlags(result, AluOp.SUB8)
                }

                is DecR8 -> {
                    val result = readR8(instruction.operand) - 1u
                    writeR8(instruction.operand, result.toUByte())
                    setFlags(result, AluOp.SUB8)
                }

                DecSp -> {
                    sp = (sp - 1u).toUShort()
                }

                Di -> {
                    ime = false
                }

                Ei -> {
                    ime = true
                    // TODO: delay enabling by one instruction
                }

                Halt -> {
                    TODO("halt")
                }

                IncHl -> {
                    val result = readByte(hl) + 1u
                    writeByte(hl, result.toUByte())
                    setFlags(result, AluOp.ADD8)
                }

                is IncR16 -> {
                    val result = readR16(instruction.operand) + 1u
                    writeR16(instruction.operand, result.toUShort())
                    setFlags(result, AluOp.ADD16)
                }

                is IncR8 -> {
                    val result = readR8(instruction.operand) + 1u
                    writeR8(instruction.operand, result.toUByte())
                    setFlags(result, AluOp.SUB8)
                }

                IncSp -> {
                    sp = (sp + 1u).toUShort()
                }

                is JpCcN16 -> {
                    if (checkCondition(instruction.condition)) {
                        pc = instruction.immediate
                    }
                }

                JpHl -> {
                    pc = hl
                }

                is JpN16 -> {
                    pc = instruction.immediate
                }

                is JrCcE8 -> {
                    if (checkCondition(instruction.condition)) {
                        pc = (pc.toInt() + instruction.immediate).toUShort()
                    }
                }

                is JrE8 -> {
                    pc = (pc.toInt() + instruction.immediate).toUShort()
                }

                LdAC -> {
                    val addr = 0xFF00u + c.toUShort()
                    val read = readByte(addr.toUShort())
                    a = read
                }

                LdCA -> {
                    val addr = 0xFF00u + c.toUShort()
                    writeByte(addr.toUShort(), a)
                }

                is LdAR16 -> {
                    a = readByte(readR16(instruction.source))
                }

                is LdHlN8 -> {
                    writeByte(hl, instruction.immediate)
                }

                is LdHlR8 -> {
                    writeByte(hl, readR8(instruction.source))
                }

                is LdHlSpE8 -> {
                    val result = (sp.toInt() + instruction.immediate).toUInt()
                    hl = result.toUShort()
                    setFlags(result, AluOp.ADD8)
                }

                is LdN16Sp -> {
                    val least = (sp and 0xFFu).toUByte()
                    val most = (sp.toUInt() and 0xFF00u shr 8).toUByte()
                    writeByte(instruction.immediate, least)
                    writeByte((instruction.immediate + 1u).toUShort(), most)
                }

                is LdR16A -> {
                    writeByte(readR16(instruction.destination), a)
                }

                is LdA16A -> {
                    writeByte(instruction.destAddress, a)
                }
                is LdAA16 -> {
                    a = readByte(instruction.sourceAddress)
                }

                is LdR16N16 -> {
                    writeR16(instruction.destination, instruction.immediate)
                }

                is LdR8Hl -> {
                    writeR8(instruction.destination, readByte(hl))
                }

                is LdR8N8 -> {
                    writeR8(instruction.destination, instruction.immediate)
                }

                is LdR8R8 -> {
                    writeR8(instruction.destination, readR8(instruction.source))
                }

                LdSpHl -> {
                    writeR16(Register.SP, hl)
                }

                is LdSpN16 -> {
                    writeR16(Register.SP, instruction.immediate)
                }

                LddAHl -> {
                    a = readByte(hl)
                    hl = (hl - 1u).toUShort()
                }

                LddHlA -> {
                    writeByte(hl, a)
                    hl = (hl - 1u).toUShort()
                }

                is LdhAN8 -> {
                    val addr = (0xFF00u + instruction.immediate).toUShort()
                    a = readByte(addr)
                }

                is LdhN8A -> {
                    val addr = (0xFF00u + instruction.immediate).toUShort()
                    writeByte(addr, a)
                }

                LdiAHl -> {
                    a = readByte(hl)
                    hl = (hl + 1u).toUShort()
                }

                LdiHlA -> {
                    writeByte(hl, a)
                    hl = (hl + 1u).toUShort()
                }

                Nop -> {}
                OrAHl -> {
                    a = readByte(hl) or a
                }

                is OrAN8 -> {
                    a = a or instruction.immediate
                }

                is OrAR8 -> {
                    a = a or readR8(instruction.operand)
                }

                is PopR16 -> {
                    val least = readByte(sp)
                    sp = (sp + 1u).toUShort()
                    val most = readByte(sp)
                    sp = (sp + 1u).toUShort()
                    val value = (most.toUInt() shl 8) or least.toUInt()
                    writeR16(instruction.operand, value.toUShort())
                }

                is PushR16 -> {
                    val value = readR16(instruction.operand)
                    val most = value.toUInt() and 0xFF00u shr 8
                    val least = value.toUInt() and 0xFFu
                    sp = (sp - 1u).toUShort()
                    writeByte(sp, most.toUByte())
                    sp = (sp - 1u).toUShort()
                    writeByte(sp, least.toUByte())
                }

                Ret -> {
                    ret()
                }

                is RetCc -> {
                    val ret = checkCondition(instruction.condition)
                    if (ret) {
                        ret()
                    }
                }

                RetI -> {
                    ime = true
                    ret()
                }

                RlA -> {
                    val temp = cb
                    carry = (a and 0x80u).toUInt() == 0x80u
                    a = (a.toUInt() shl 1).toUByte() or temp
                    z = false
                    n = false
                    half = false
                }

                RlcA -> {
                    carry = (a and 0x80u).toUInt() == 0x80u
                    a = (a.toUInt() shl 1).toUByte() or cb
                    z = false
                    n = false
                    half = false
                }

                RrA -> {
                    val temp = cb
                    carry = (a and 0x01u).toUInt() == 0x01u
                    a = (a.toUInt() shr 1).toUByte() or (temp.toUInt() shl 7).toUByte()
                    z = false
                    n = false
                    half = false
                }

                RrcA -> {
                    carry = (a and 0x01u).toUInt() == 0x01u
                    a = (a.toUInt() shr 1).toUByte() or (cb.toUInt() shl 7).toUByte()
                    z = false
                    n = false
                    half = false
                }

                is RstVec -> {
                    val addr = instruction.target.toUInt()
                    assert(addr in allowedRstAddresses) { "tried to jump to forbidden rst addr: addr=$addr" }
                    call(addr.toUShort())
                }

                SbcAHl -> {
                    val result = (a - readByte(hl) - cb)
                    a = result.toUByte()
                    setFlags(result, AluOp.SUB8)
                }

                is SbcAN8 -> {
                    val result = (a - instruction.immediate - cb)
                    a = result.toUByte()
                    setFlags(result, AluOp.SUB8)
                }

                is SbcAR8 -> {
                    val result = (a - readR8(instruction.operand) - cb)
                    a = result.toUByte()
                    setFlags(result, AluOp.SUB8)
                }

                Scf -> {
                    n = false
                    half = false
                    carry = true
                }

                is Stop -> {
                    // TODO WHAT?
                }

                SubAHl -> {
                    val result = a - readByte(hl)
                    a = result.toUByte()
                    setFlags(result, AluOp.SUB8)
                }

                is SubAN8 -> {
                    val result = a - instruction.immediate
                    a = result.toUByte()
                    setFlags(result, AluOp.SUB8)
                }

                is SubAR8 -> {
                    val result = a - readR8(instruction.operand)
                    a = result.toUByte()
                    setFlags(result, AluOp.SUB8)
                }

                XorAHl -> {
                    val result = a xor readByte(hl)
                    a = result
                    z = result <= 0u
                    n = false
                    half = false
                    carry = false
                }

                is XorAN8 -> {
                    val result = a xor instruction.immediate
                    a = result
                    z = result <= 0u
                    n = false
                    half = false
                    carry = false
                }

                is XorAR8 -> {
                    val result = a xor readR8(instruction.operand)
                    a = result
                    z = result <= 0u
                    n = false
                    half = false
                    carry = false
                }
            }
        }

        fun execute(instruction: PrefixedInstruction) {
            when (instruction) {
                is BitU3Hl -> {
                    val mask = (0x01u shl instruction.bit.toInt()).toUByte()
                    z = (readByte(hl) and mask).toUInt() == 0x00u
                    n = false
                    half = true
                }

                is BitU3R8 -> {
                    val mask = (0x01u shl instruction.bit.toInt()).toUByte()
                    z = (readR8(instruction.operand) and mask).toUInt() == 0x00u
                    n = false
                    half = true
                }

                is ResU3Hl -> {
                    val mask = (0x01u shl instruction.bit.toInt()).toUByte()
                    val v = readByte(hl)
                    val new = v and mask.inv()
                    writeByte(hl, new)
                }

                is ResU3R8 -> {
                    val mask = (0x01u shl instruction.bit.toInt()).toUByte()
                    val v = readR8(instruction.operand)
                    val new = v and mask.inv()
                    writeR8(instruction.operand, new)
                }

                RlHl -> {
                    val temp = cb
                    val value = readByte(hl)
                    val newValue = ((value.toUInt() shl 1) or temp.toUInt()).toUByte()
                    writeByte(hl, newValue)
                    carry = (value and 0x80u).toUInt() == 0x80u
                    z = newValue.toUInt() == 0u
                }

                is RlR8 -> {
                    val temp = cb
                    val value = readR8(instruction.operand)
                    val newValue = ((value.toUInt() shl 1) or temp.toUInt()).toUByte()
                    writeR8(instruction.operand, newValue)
                    carry = (value and 0x80u).toUInt() == 0x80u
                    z = newValue.toUInt() == 0u
                }

                RlcHl -> {
                    val value = readByte(hl)
                    val b7 = ((value and 0x80u).toUInt() shr 7).toUByte()
                    val newValue = ((value.toUInt() shl 1) or b7.toUInt()).toUByte()
                    writeByte(hl, newValue)
                    carry = (value and 0x80u).toUInt() == 0x80u
                    z = newValue.toUInt() == 0u
                    n = false
                    half = false
                }

                is RlcR8 -> {
                    val value = readR8(instruction.operand)
                    val b7 = ((value and 0x80u).toUInt() shr 7).toUByte()
                    val newValue = ((value.toUInt() shl 1) or b7.toUInt()).toUByte()
                    writeR8(instruction.operand, newValue)
                    carry = (value and 0x80u).toUInt() == 0x80u
                    z = newValue.toUInt() == 0u
                    n = false
                    half = false
                }

                RrHl -> {
                    val temp = cb.toUInt() shl 7
                    val value = readByte(hl)
                    val newValue = ((value.toUInt() shr 1) or temp).toUByte()
                    writeByte(hl, newValue)
                    carry = (value and 0x01u).toUInt() == 0x01u
                    z = newValue.toUInt() == 0u
                }

                is RrR8 -> {
                    val temp = cb.toUInt() shl 7
                    val value = readR8(instruction.operand)
                    val newValue = ((value.toUInt() shr 1) or temp).toUByte()
                    writeR8(instruction.operand, newValue)
                    carry = (value and 0x01u).toUInt() == 0x01u
                    z = newValue.toUInt() == 0u
                }

                RrcHl -> {
                    val value = readByte(hl)
                    val b0 = (value and 0x01u).toUInt()
                    val newValue = ((value.toUInt() shr 1) or b0).toUByte()
                    writeByte(hl, newValue)
                    carry = (value and 0x01u).toUInt() == 0x01u
                    z = newValue.toUInt() == 0u
                    n = false
                    half = false
                }

                is RrcR8 -> {
                    val value = readR8(instruction.operand)
                    val b0 = (value and 0x01u).toUInt()
                    val newValue = ((value.toUInt() shr 1) or b0).toUByte()
                    writeR8(instruction.operand, newValue)
                    carry = (value and 0x01u).toUInt() == 0x01u
                    z = newValue.toUInt() == 0u
                    n = false
                    half = false
                }

                is SetU3Hl -> {
                    val mask = (0x01u shl instruction.bit.toInt()).toUByte()
                    val value = readByte(hl) or mask
                    writeByte(hl, value)
                }

                is SetU3R8 -> {
                    val mask = (0x01u shl instruction.bit.toInt()).toUByte()
                    val value = readR8(instruction.operand) or mask
                    writeR8(instruction.operand, value)
                }

                SlaHl -> {
                    val value = readByte(hl).toUInt()
                    val res = (value shl 1)
                    writeByte(hl, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = value and 0x80u == 0x80u
                }

                is SlaR8 -> {
                    val value = readR8(instruction.operand).toUInt()
                    val res = (value shl 1)
                    writeR8(instruction.operand, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = value and 0x80u == 0x80u
                }

                SraHl -> {
                    val value = readByte(hl).toUInt()
                    val res = (value shr 1) or (value and 0x80u)
                    writeByte(hl, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = value and 0x01u == 0x01u
                }

                is SraR8 -> {
                    val value = readR8(instruction.operand).toUInt()
                    val res = (value shr 1) or (value and 0x80u)
                    writeR8(instruction.operand, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = value and 0x01u == 0x01u
                }

                SrlHl -> {
                    val value = readByte(hl).toUInt()
                    val res = (value shr 1)
                    writeByte(hl, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = value and 0x01u == 0x01u
                }

                is SrlR8 -> {
                    val value = readR8(instruction.operand).toUInt()
                    val res = (value shr 1)
                    writeR8(instruction.operand, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = value and 0x01u == 0x01u
                }

                SwapHl -> {
                    val value = readByte(hl).toUInt()
                    val res = (value and 0xF0u shr 4) or (value and 0x0Fu shl 4)
                    writeByte(hl, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = false
                }

                is SwapR8 -> {
                    val value = readR8(instruction.operand).toUInt()
                    val res = (value and 0xF0u shr 4) or (value and 0x0Fu shl 4)
                    writeR8(instruction.operand, res.toUByte())
                    z = res == 0u
                    n = false
                    half = false
                    carry = false
                }
            }
        }

        if (trace) {
            println("execute(): inst=$instruction. Before:")
            println(dumpState())
        }
        when (instruction) {
            is Normal -> execute(instruction.instruction)
            is Prefixed -> execute(instruction.instruction)
        }
        if (trace) {
            println("After:")
            println(dumpState())
            println("###################")
        }
    }

    fun run() {
        while (true) {
            tick()
        }
    }

    var stoppedAtBreakpoint = false

    fun tick() {
        if (ime) {
            val isr = interruptEnable and interruptFlag
            if (isr > 0u) {
                TODO("check interrupt")
            }
            if (isr and INTERRUPT_VBLANK == INTERRUPT_VBLANK) {
                ime = false
                interruptFlag = interruptFlag and INTERRUPT_VBLANK.inv()
                call(0x40u)
            }
            if (isr and INTERRUPT_LCD == INTERRUPT_LCD) {
                ime = false
                interruptFlag = interruptFlag and INTERRUPT_LCD.inv()
                call(0x48u)
            }
            if (isr and INTERRUPT_TIMER == INTERRUPT_TIMER) {
                ime = false
                interruptFlag = interruptFlag and INTERRUPT_TIMER.inv()
                call(0x50u)
            }
            if (isr and INTERRUPT_SERIAL == INTERRUPT_SERIAL) {
                ime = false
                interruptFlag = interruptFlag and INTERRUPT_SERIAL.inv()
                call(0x58u)
            }
            if (isr and INTERRUPT_JOYPAD == INTERRUPT_JOYPAD) {
                ime = false
                interruptFlag = interruptFlag and INTERRUPT_JOYPAD.inv()
                call(0x60u)
            }
        }

        val instruction = fetch(::fetchByte)!!
        execute(instruction)
    }

    fun reset() {
        af = 0u
        bc = 0u
        de = 0u
        hl = 0u
        sp = 0u
        pc = 0u
        isBoot = true
    }

    fun peekNextInstruction(address: UShort = pc): RealInstruction {
        var offset = 0u
        return fetch {
            val addr = (address + offset).toUShort()
            offset += 1u
            readByte(addr)
        }!!
    }

    private fun setFlag(value: Boolean, flag: UShort) = if (value) {
        af or flag
    } else {
        af and flag.inv()
    }

    operator fun ByteArray.get(i: UShort): Byte {
        return this[i.toInt()]
    }

    fun readR8(register: Register): UByte {
        return when (register) {
            Register.B -> b
            Register.C -> c
            Register.D -> d
            Register.E -> e
            Register.H -> h
            Register.L -> l
            Register.HL_ADDR -> h
            Register.A -> a
            else -> error("not a valid r8 register. register: $register")
        }
    }

    fun readR16(register: Register): UShort {
        return when (register) {
            Register.BC -> bc
            Register.DE -> de
            Register.HL -> hl
            Register.SP -> sp
            Register.PC -> pc
            else -> error("not a valid r16 register. register: $register")
        }
    }

    fun writeR8(register: Register, value: UByte) {
        when (register) {
            Register.B -> {
                b = value
            }

            Register.C -> {
                c = value
            }

            Register.D -> {
                d = value
            }

            Register.E -> {
                e = value
            }

            Register.H -> {
                h = value
            }

            Register.L -> {
                l = value
            }

            Register.HL_ADDR -> {
                error("not a valid r8 register. register: $register")
            }

            Register.A -> {
                a = value
            }

            else -> error("not a valid r8 register. register: $register")
        }
    }

    fun writeR16(register: Register, value: UShort) {
        when (register) {
            Register.BC -> {
                bc = value
            }

            Register.DE -> {
                de = value
            }

            Register.HL -> {
                hl = value
            }

            Register.SP -> {
                sp = value
            }

            Register.PC -> {
                pc = value
            }

            else -> error("not a valid r16 register. register: $register")
        }
    }

    fun checkCondition(condition: Condition) = when (condition) {
        Condition.NOT_ZERO -> !z
        Condition.ZERO -> z
        Condition.NOT_CARRY -> !carry
        Condition.CARRY -> carry
    }

    fun getStack(): ByteArray {
        var spp = sp.toUInt()
        val stack = ByteArray(32)
        val min = min(stack.size.toUInt(), 0xFFFEu - spp)
        var c = 0
        for (addr in spp until (spp + min)) {
            stack[c] = readByte(addr.toUShort()).toByte()
            c += 1
        }
        return stack.copyOf(min.toInt())
    }

    fun getState(): CpuState {
        return CpuState(
            next = peekNextInstruction(),
            af = af,
            bc = bc,
            de = de,
            hl = hl,
            sp = sp,
            pc = pc,
            a = a,
            b = b,
            c = c,
            d = d,
            e = e,
            h = h,
            l = l,
            z = z,
            n = n,
            half = half,
            carry = carry,
            ime = ime,
            interruptEnable = interruptEnable.toUByte(),
            interruptFlag = interruptFlag.toUByte(),
            stack = getStack(),
        )
    }

    fun getDisassembly(): List<InstructionWithAddress> {
        val romSize = if (isBoot) {
            bootRom!!.size
        } else {
            32 * 1024
        }
        var pointer = 0
        val instructions = mutableListOf<InstructionWithAddress>()
        while (pointer < romSize) {
            val address = pointer
            val instr = fetch {
                val byte = readByte(pointer.toUShort())
                pointer += 1
                byte
            }
            instructions.add(InstructionWithAddress(instr, address.toUShort()))
        }
        return instructions
    }

    fun dumpState() = buildString {
        append("A=")
        append(a.toHexString())
        append("    BC=")
        append(bc.toHexString())
        append("\n")
        append("DE=")
        append(de.toHexString())
        append(" HL=")
        append(hl.toHexString())
        append("\n")
        append("PC=")
        append(pc.toHexString())
        append(" SP=")
        append(sp.toHexString())
        append("\nFLAGS: ")
        append("z=$z ")
        append("n=$n ")
        append("h=$half ")
        append("c=$carry")
        append("ime:$ime")
        append("ie:$interruptEnable")
        append("if:$interruptFlag")
        val spp = sp.toUInt()
        if (spp != 0u) {
            val stackSize = (0xFFFEu - spp).toInt()
            if (stackSize > 0) {
                append("\nStack: ")
                for (addr in 0 until stackSize) {
                    val hramAddr = hram.size - stackSize + addr
                    append(hram[hramAddr].toHexString())
                    append(" ")
                }
            }
        }
    }
}