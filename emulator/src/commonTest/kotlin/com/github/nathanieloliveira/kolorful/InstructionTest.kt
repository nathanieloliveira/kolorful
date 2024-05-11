package com.github.nathanieloliveira.kolorful

import kotlin.test.Test

class InstructionTest {

    data class CpuState(
        val af: UShort? = null,
        val bc: UShort? = null,
        val de: UShort? = null,
        val hl: UShort? = null,
        val sp: UShort? = null,
        val pc: UShort? = null,
        val a: UByte? = null,
        val b: UByte? = null,
        val c: UByte? = null,
        val d: UByte? = null,
        val e: UByte? = null,
        val h: UByte? = null,
        val l: UByte? = null,
        val z: Boolean? = null,
        val n: Boolean? = null,
        val half: Boolean? = null,
        val carry: Boolean? = null,
    )

    fun createCpu() = Cpu(
        byteArrayOf(),
        Bus(
            arrayOf(

            )
        ),
        false,
    )

    fun Cpu.executeInstruction(instruction: Cpu.RealInstruction): Cpu {
        this.execute(instruction)
        return this
    }

    fun Cpu.setState(state: CpuState): Cpu {
        if (state.af != null) {
            this.af = state.af
        }
        if (state.bc != null) {
            this.bc = state.bc
        }
        if (state.de != null) {
            this.de = state.de
        }
        if (state.hl != null) {
            this.hl = state.hl
        }
        if (state.sp != null) {
            this.sp = state.sp
        }
        if (state.pc != null) {
            this.pc = state.pc
        }
        if (state.a != null) {
            this.a = state.a
        }
        if (state.b != null) {
            this.b = state.b
        }
        if (state.c != null) {
            this.c = state.c
        }
        if (state.d != null) {
            this.d = state.d
        }
        if (state.e != null) {
            this.e = state.e
        }
        if (state.h != null) {
            this.h = state.h
        }
        if (state.l != null) {
            this.l = state.l
        }
        if (state.z != null) {
            this.z = state.z
        }
        if (state.n != null) {
            this.n = state.n
        }
        if (state.half != null) {
            this.half = state.half
        }
        if (state.carry != null) {
            this.carry = state.carry
        }
        return this
    }

    fun Cpu.checkState(instruction: Cpu.RealInstruction, state: CpuState): Cpu {
        val cpu = this
        var error = false
        val errorString = buildString {
            append("\nError running instruction $instruction:\n")
            append("errors:\n")
            if (state.af != null) {
                val afError = cpu.af != state.af
                if (afError) {
                    append("afError: expected: ${state.af}, actual: ${cpu.af}\n")
                }
                error = error || afError
            }
            if (state.bc != null) {
                val bcError = cpu.bc != state.bc
                if (bcError) {
                    append("bcError: expected: ${state.bc}, actual: ${cpu.bc}\n")
                }
                error = error || bcError
            }
            if (state.de != null) {
                val deError = cpu.de != state.de
                if (deError) {
                    append("deError: expected: ${state.de}, actual: ${cpu.de}\n")
                }
                error = error || deError
            }
            if (state.hl != null) {
                val hlError = cpu.hl != state.hl
                if (hlError) {
                    append("hlError: expected: ${state.hl}, actual: ${cpu.hl}\n")
                }
                error = error || hlError
            }
            if (state.sp != null) {
                val spError = cpu.sp != state.sp
                if (spError) {
                    append("spError: expected: ${state.sp}, actual: ${cpu.sp}\n")
                }
                error = error || spError
            }
            if (state.pc != null) {
                val pcError = cpu.pc != state.pc
                if (pcError) {
                    append("pcError: expected: ${state.pc}, actual: ${cpu.pc}\n")
                }
                error = error || pcError
            }
            if (state.a != null) {
                val aError = cpu.a != state.a
                if (aError) {
                    append("aError: expected: ${state.a}, actual: ${cpu.a}\n")
                }
                error = error || aError
            }
            if (state.b != null) {
                val bError = cpu.b != state.b
                if (bError) {
                    append("bError: expected: ${state.b}, actual: ${cpu.b}\n")
                }
                error = error || bError
            }
            if (state.c != null) {
                val cError = cpu.c != state.c
                if (cError) {
                    append("cError: expected: ${state.c}, actual: ${cpu.c}\n")
                }
                error = error || cError
            }
            if (state.d != null) {
                val dError = cpu.d != state.d
                if (dError) {
                    append("dError: expected: ${state.d}, actual: ${cpu.d}\n")
                }
                error = error || dError
            }
            if (state.e != null) {
                val eError = cpu.e != state.e
                if (eError) {
                    append("eError: expected: ${state.e}, actual: ${cpu.e}\n")
                }
                error = error || eError
            }
            if (state.h != null) {
                val hError = cpu.h != state.h
                if (hError) {
                    append("hError: expected: ${state.h}, actual: ${cpu.h}\n")
                }
                error = error || hError
            }
            if (state.l != null) {
                val lError = cpu.l != state.l
                if (lError) {
                    append("lError: expected: ${state.l}, actual: ${cpu.l}\n")
                }
                error = error || lError
            }
            if (state.z != null) {
                val zError = cpu.z != state.z
                if (zError) {
                    append("zError: expected: ${state.z}, actual: ${cpu.z}\n")
                }
                error = error || zError
            }
            if (state.n != null) {
                val nError = cpu.n != state.n
                if (nError) {
                    append("nError: expected: ${state.n}, actual: ${cpu.n}\n")
                }
                error = error || nError
            }
            if (state.half != null) {
                val halfError = cpu.half != state.half
                if (halfError) {
                    append("halfError: expected: ${state.half}, actual: ${cpu.half}\n")
                }
                error = error || halfError
            }
            if (state.carry != null) {
                val carryError = cpu.carry != state.carry
                if (carryError) {
                    append("carryError: expected: ${state.carry}, actual: ${cpu.carry}\n")
                }
                error = error || carryError
            }
        }
        assert(!error) {
            errorString
        }
        return this
    }

    data class CpuInstructionExecutionTest(
        val instruction: Cpu.RealInstruction,
        val before: CpuState,
        val after: CpuState,
        val setup: (Cpu.() -> Unit)? = null,
        val extraChecks: (Cpu.() -> Unit)? = null,
    )

    fun MutableList<CpuInstructionExecutionTest>.testInst(
        instruction: Instruction,
        before: CpuState,
        after: CpuState,
        setup: (Cpu.() -> Unit)? = null,
        extraChecks: (Cpu.() -> Unit)? = null,
    ) {
        add(
            CpuInstructionExecutionTest(
                Cpu.Normal(instruction),
                before,
                after,
                setup,
                extraChecks,
            )
        )
    }

    fun MutableList<CpuInstructionExecutionTest>.testInst(
        instruction: PrefixedInstruction,
        before: CpuState,
        after: CpuState,
        setup: (Cpu.() -> Unit)? = null,
        extraChecks: (Cpu.() -> Unit)? = null,
    ) {
        add(
            CpuInstructionExecutionTest(
                Cpu.Prefixed(instruction),
                before,
                after,
                setup,
                extraChecks,
            )
        )
    }

    @Test
    fun testInstructionExecution() {
        val tests = buildList {
            // ADC tests
            testInst(
                AdcAR8(Register.D),
                CpuState(a = 0u, d = 20u, carry = false),
                CpuState(a = 20u, d = 20u, carry = false, z = false, n = false),
            )
            testInst(
                AdcAR8(Register.D),
                CpuState(a = 0u, d = 20u, carry = true),
                CpuState(a = 21u, d = 20u, carry = false),
            )
            testInst(
                AdcAR8(Register.D),
                CpuState(a = 15u, d = 15u, carry = true, half = false),
                CpuState(a = 31u, d = 15u, carry = false, half = true),
            )
            testInst(
                AdcAR8(Register.D),
                CpuState(a = 250u, d = 6u, carry = true, half = false),
                CpuState(a = 1u, d = 6u, carry = true, half = true, z = false),
            )
            testInst(
                AdcAR8(Register.D),
                CpuState(a = 250u, d = 6u, carry = false, half = false),
                CpuState(a = 0u, d = 6u, carry = true, half = true, z = true),
            )
            testInst(
                AdcAHl,
                CpuState(a = 125u, hl = Cpu.HRAM_OFFSET),
                CpuState(a = 128u),
                setup = {
                    writeByte(Cpu.HRAM_OFFSET, 3u)
                },
            )
            // ADD HL
            testInst(
                AddHlR16(Register.BC),
                CpuState(bc = 2048u, hl = 0u, half = false),
                CpuState(bc = 2048u, hl = 2048u, half = true),
            )
            testInst(
                AddHlSp,
                CpuState(hl = 0x7FFFu, sp = 0x8001u, half = false),
                CpuState(hl = 0u, sp = 0x8001u, half = true, carry = true, z = true),
            )
            // ADD SP,e8
            testInst(
                AddSpE8(-128),
                CpuState(sp = 256u, z = true, n = true, half = false, carry = false),
                CpuState(sp = 128u, z = false, n = false, half = true, carry = true),
            )
            // ADD tests
            testInst(
                AddAN8(42u),
                CpuState(a = 20u),
                CpuState(a = 62u),
            )
            testInst(
                AddAN8(240u),
                CpuState(a = 16u, carry = false, z = false),
                CpuState(a = 0u, carry = true, z = true),
            )
            // AND tests
            testInst(
                AndAN8(0xAAu),
                CpuState(a = 0x55u, z = false, n = true, half = false, carry = true),
                CpuState(a = 0u, z = true, n = false, half = true, carry = false),
            )
            testInst(
                AndAR8(Register.B),
                CpuState(a = 0x55u, b = 0xAAu, z = false, n = true, half = false, carry = true),
                CpuState(a = 0u, z = true, n = false, half = true, carry = false),
            )
            testInst(
                AndAHl,
                CpuState(a = 0x55u, hl = Cpu.HRAM_OFFSET, z = false, n = true, half = false, carry = true),
                CpuState(a = 0u, z = true, n = false, half = true, carry = false),
                setup = {
                    writeByte(Cpu.HRAM_OFFSET, 0xAAu)
                }
            )
            // BIT tests
            testInst(
                BitU3Hl(7u),
                CpuState(hl = Cpu.HRAM_OFFSET, z = false, n = true, half = false),
                CpuState(hl = Cpu.HRAM_OFFSET, z = false, n = false, half = true),
                setup = {
                    writeByte(Cpu.HRAM_OFFSET, 0xAAu)
                }
            )
            testInst(
                BitU3R8(6u, Register.A),
                CpuState(a = 0xAAu, z = false, n = true, half = false),
                CpuState(a = 0xAAu, z = true, n = false, half = true),
            )
            // CALL
            testInst(
                CallN16(0xFF00u),
                CpuState(pc = 0x00FFu, sp = Cpu.HRAM_END),
                CpuState(pc = 0xFF00u, sp = (Cpu.HRAM_END - 2u).toUShort()),
                extraChecks = {
                    val least = readByte((sp + 1u).toUShort())
                    val most = readByte((sp + 2u).toUShort())
                    assert(least.toUInt() == 0xFFu) {
                        "least significant is wrong: expected: 0xFFu, got $least"
                    }
                    assert(most.toUInt() == 0x00u) {
                        "most significant is wrong: expected: 0x00u, got $most"
                    }
                }
            )
            Condition.entries.forEachIndexed { i, cond ->
                val stateNotCall = CpuState(pc = 0x00FFu, sp = Cpu.HRAM_END, z = cond == Condition.ZERO, carry = cond == Condition.CARRY)
                val stateCall = CpuState(pc = 0xFF00u, sp = (Cpu.HRAM_END - 2u).toUShort())
                // always call
                testInst(
                    CallCcN16(cond, 0xFF00u),
                    stateNotCall,
                    stateCall,
                    extraChecks = {
                        val least = readByte((sp + 1u).toUShort())
                        val most = readByte((sp + 2u).toUShort())
                        assert(least.toUInt() == 0xFFu) {
                            "least significant is wrong: expected: 0xFFu, got $least"
                        }
                        assert(most.toUInt() == 0x00u) {
                            "most significant is wrong: expected: 0x00u, got $most"
                        }
                    }
                )
                // never call
                val notCond = Condition.entries[(i + 3) % Condition.entries.size]
                testInst(
                    CallCcN16(notCond, 0xFF00u),
                    stateNotCall,
                    stateNotCall,
                )
            }
            // CCF
            repeat(2) { i ->
                testInst(
                    Ccf,
                    CpuState(n = true, half = true, carry = i == 0),
                    CpuState(n = false, half = false, carry = i != 0),
                )
            }
            // CP A,r8
            testInst(
                CpAR8(Register.B),
                CpuState(a = 0xFFu, b = 0x0Fu, z = true,  n = false, half = false, carry = true),
                CpuState(a = 0xFFu, b = 0x0Fu, z = false, n = true, half = true, carry = false),
            )
            testInst(
                CpAR8(Register.B),
                CpuState(a = 0x0Fu, b = 0xFFu, z = true,  n = false, half = false, carry = false),
                CpuState(a = 0x0Fu, b = 0xFFu, z = false, n = true, half = true, carry = true),
            )
            testInst(
                CpAN8(0xFFu),
                CpuState(a = 0xFFu, z = false, n = false, half = false, carry = false),
                CpuState(a = 0xFFu, z = true, n = true, half = false, carry = false),
            )
            // CPL
            testInst(
                Cpl,
                CpuState(a = 0xFFu, z = true, n = false, half = false, carry = false),
                CpuState(a = 0x00u, z = true, n = true, half = true, carry = false),
            )
        }
        for (t in tests) {
            createCpu()
                .setState(t.before)
                .also {
                    t.setup?.invoke(it)
                }.executeInstruction(t.instruction)
                .checkState(t.instruction, t.after)
                .also {
                    t.extraChecks?.invoke(it)
                }
        }
    }
}