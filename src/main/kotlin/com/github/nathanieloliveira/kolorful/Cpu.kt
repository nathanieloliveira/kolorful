package com.github.nathanieloliveira.kolorful

class Cpu(
    val bootRom: ByteArray,
    val bus: Bus,
) {

    companion object {
        const val FLAG_ZERO: UShort = 0x0080u
        const val FLAG_SUB: UShort = 0x0040u
        const val FLAG_HALF_CARRY: UShort = 0x0020u
        const val FLAG_CARRY: UShort = 0x0010u
    }

    enum class AluOp {
        ADD8,
        ADD16,
        SUB8,
        SUB16,
    }

    sealed interface RealInstruction
    data class Normal(val instruction: Instruction): RealInstruction
    data class Prefixed(val instruction: PrefixedInstruction): RealInstruction

    fun Instruction.toNormal() = Normal(this)
    fun PrefixedInstruction.toPrefixed() = Prefixed(this)

    var af: UShort = 0u
    var bc: UShort = 0u
    var de: UShort = 0u
    var hl: UShort = 0u

    var sp: UShort = 0u
    var pc: UShort = 0u

    var ime: Boolean = false

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

    val cb: UByte = if (carry) 0x01u else 0x00u

    var isBoot = true

    fun setFlags(result: UInt, aluOp: AluOp) {
        z = result == 0u
        n = aluOp == AluOp.SUB8 || aluOp == AluOp.SUB16
        if (aluOp == AluOp.ADD8 || aluOp == AluOp.SUB8) {
            carry = result > 0xFFu
            half = result > 0x0Fu || result > UShort.MAX_VALUE
        }
        carry = carry or (result > UShort.MAX_VALUE)
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

    fun readByte(address: UShort): UByte {
        val inBootRange = address.toInt() < bootRom.size
        return if (isBoot && inBootRange) {
            bootRom[address].toUByte()
        } else {
            bus.read(address)
        }
    }

    fun writeByte(address: UShort, byte: UByte) {
        val inBootRange = address.toInt() < bootRom.size
        return if (isBoot && inBootRange) {
            error("trying to write into Boot ROM address")
        } else {
            bus.write(address, byte)
        }
    }

    fun fetchByte(): UByte {
        return readByte(pc++)
    }

    fun fetchUShort(): UShort {
        val leastSignificant = fetchByte()
        val mostSignificant = fetchByte()
        val short = mostSignificant.toInt() shl 8 and leastSignificant.toInt()
        return short.toUShort()
    }

    fun fetch(): RealInstruction {
        fun decodePrefixed(): Prefixed {
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
                PrefixedOpcode.INVALID -> error("Invalid prefixed opcode: $prefixedOp")
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
            Opcode.STOP -> Stop
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
            Opcode.LD_N16_A -> {
                val immediate = fetchUShort()
                LdN16A(immediate)
            }
            Opcode.LD_A_N16 -> {
                val immediate = fetchUShort()
                LdAN16(immediate)
            }
            Opcode.LD_N16_SP -> {
                val immediate = fetchUShort()
                LdN16Sp(immediate)
            }
            Opcode.LD_HL_SP_E8 -> {
                val immediate = fetchByte()
                LdHlSpE8(immediate)
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
            Opcode.INVALID -> error("Invalid opcode: $op")
            Opcode.PREFIX -> null
        }?.toNormal() ?: decodePrefixed()
        return inst
    }

    fun execute(instruction: RealInstruction) {
        fun call(label: UShort) {
            val least = readByte(pc)
            val most = readByte((pc + 1u).toUShort())
            writeByte(sp++, least)
            writeByte(sp++, most)
            pc = label
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
                    carry = result > 0xFF
                    half = result > 0x0F
                }
                AndAHl -> {
                    val result = readByte(hl) and a
                    a = result
                    z = result.toUByte() == 0u.toUByte()
                    n = false
                    half = true
                    carry = false
                }
                is AndAN8 -> {
                    val result = instruction.immediate and a
                    a = result
                    z = result.toUByte() == 0u.toUByte()
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
                    TODO("not capable to disable interrupt")
                }
                Ei -> {
                    TODO("not capable to disable interrupt")
                }
                Halt -> {
                    TODO()
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
                is LdAN16 -> {

                }
                is LdAR16 -> TODO()
                is LdHlN8 -> TODO()
                is LdHlR8 -> TODO()
                is LdHlSpE8 -> TODO()
                is LdN16A -> TODO()
                is LdN16Sp -> TODO()
                is LdR16A -> TODO()
                is LdR16N16 -> TODO()
                is LdR8Hl -> TODO()
                is LdR8N8 -> TODO()
                is LdR8R8 -> TODO()
                LdSpHl -> TODO()
                is LdSpN16 -> TODO()
                LddAHl -> TODO()
                LddHlA -> TODO()
                is LdhAN8 -> TODO()
                is LdhN8A -> TODO()
                LdiAHl -> TODO()
                LdiHlA -> TODO()
                Nop -> TODO()
                OrAHl -> TODO()
                is OrAN8 -> TODO()
                is OrAR8 -> TODO()
                is PopR16 -> TODO()
                is PushR16 -> TODO()
                Ret -> TODO()
                is RetCc -> TODO()
                RetI -> TODO()
                RlA -> TODO()
                RlcA -> TODO()
                RrA -> TODO()
                RrcA -> TODO()
                is RstVec -> TODO()
                SbcAHl -> TODO()
                is SbcAN8 -> TODO()
                is SbcAR8 -> TODO()
                Scf -> TODO()
                Stop -> TODO()
                SubAHl -> TODO()
                is SubAN8 -> TODO()
                is SubAR8 -> TODO()
                XorAHl -> TODO()
                is XorAN8 -> TODO()
                is XorAR8 -> TODO()
            }
        }

        fun execute(prefixedInstruction: PrefixedInstruction) {
            when (prefixedInstruction) {
                is BitU3Hl -> TODO()
                is BitU3R8 -> TODO()
                is ResU3Hl -> TODO()
                is ResU3R8 -> TODO()
                RlHl -> TODO()
                is RlR8 -> TODO()
                RlcHl -> TODO()
                is RlcR8 -> TODO()
                RrHl -> TODO()
                is RrR8 -> TODO()
                RrcHl -> TODO()
                is RrcR8 -> TODO()
                is SetU3Hl -> TODO()
                is SetU3R8 -> TODO()
                SlaHl -> TODO()
                is SlaR8 -> TODO()
                SraHl -> TODO()
                is SraR8 -> TODO()
                SrlHl -> TODO()
                is SrlR8 -> TODO()
                SwapHl -> TODO()
                is SwapR8 -> TODO()
            }
        }

        when (instruction) {
            is Normal -> execute(instruction.instruction)
            is Prefixed -> execute(instruction.instruction)
        }
    }

    fun run() {
        while (true) {
            val instruction = fetch()
            execute(instruction)
        }
    }

    private fun setFlag(value: Boolean, flag: UShort) = if (value) {
        af or flag
    } else {
        af and flag.inv()
    }
}