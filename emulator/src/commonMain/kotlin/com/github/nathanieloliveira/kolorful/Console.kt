package com.github.nathanieloliveira.kolorful

class Console {

    fun run(program: ByteArray) {
        val bankedMemory = BankedMemory(0xC000..0xDFFF)
        val devices: Array<Device> = arrayOf(
            VRam(),
            bankedMemory,
            WramBankSelect(bankedMemory),
            AudioController(),
            DebugDevice(arrayOf(0x0000..0xFFFF)),
        )
        val bus = Bus(devices)
        val cpu = Cpu(this::class.java.getResourceAsStream("/cgb_boot.bin")!!.readAllBytes()!!, bus)

        try {
            cpu.run()
        } catch (e: Exception) {
            e.printStackTrace()
            println("------- cpu state -------")
            println(cpu.dumpState())
        }
    }

}