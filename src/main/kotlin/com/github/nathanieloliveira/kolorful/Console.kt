package com.github.nathanieloliveira.kolorful

class Console {

    fun run(program: ByteArray) {
        val devices: Array<Device> = arrayOf(
            Memory(0xC000..0xCFFF), // WRAM
            Memory(0xFF80..0xFFFE), // HRAM
        )
        val bus = Bus(devices)
        val cpu = Cpu(this::class.java.getResourceAsStream("/cgb_boot.bin")!!.readAllBytes()!!, bus)

        cpu.run()
    }

}