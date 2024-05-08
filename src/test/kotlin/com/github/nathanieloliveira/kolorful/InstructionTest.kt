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
        Bus(emptyArray()),
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

    fun Cpu.checkState(state: CpuState): Cpu {
        val cpu = this
        var error = false
        val errorString = buildString {
            append("\nerrors:\n")
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
    )

    @Test
    fun testInstructionExecution() {
        val tests = listOf(
            CpuInstructionExecutionTest(
                Cpu.Normal(AddAN8(42u)),
                CpuState(a = 20u),
                CpuState(a = 62u),
            ),
            CpuInstructionExecutionTest(
                Cpu.Normal(AddAN8(240u)),
                CpuState(a = 16u, carry = false, z = false),
                CpuState(a = 0u, carry = true, z = true),
            ),
        )
        for (t in tests) {
            createCpu().setState(t.before).executeInstruction(t.instruction).checkState(t.after)
        }
    }
}