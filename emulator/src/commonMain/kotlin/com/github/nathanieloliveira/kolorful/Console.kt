package com.github.nathanieloliveira.kolorful

class Console(
    val trace: Boolean = false,
    val cartridge: Cartridge?
) {

    val bankedMemory = BankedMemory(0xC000..0xDFFF)

    val devices = buildList<Device> {
        if (cartridge != null) {
            add(cartridge)
        }
        add(VRam())
        add(bankedMemory)
        add(WramBankSelect(bankedMemory))
        add(Memory(arrayOf(0xE000..0xFDFF), bankedMemory.bank0)) // Echo RAM
        add(AudioController())
        add(Serial())
        add(DebugDevice(arrayOf(0x0000..0xFFFF)))
    }.toTypedArray()

    val bus = Bus(devices)
    val cpu = Cpu(bus, trace, this::class.java.getResourceAsStream("/cgb_boot.bin")!!.readAllBytes()!!)

    fun run(program: ByteArray) {
        try {
            cpu.run()
        } catch (e: Exception) {
            e.printStackTrace()
            println("------- cpu state -------")
            println(cpu.dumpState())
        }
    }

    fun tick() {
        cpu.tick()
    }

    fun reset() {
        cpu.reset()
    }

    fun getColorPalette(): Array<UInt> {
        return arrayOf(
            0xFFFFFFFFu,
            0xFF555555u,
            0xFF111111u,
            0xFF000000u,
        )
    }

    fun getTileData(data: ByteArray) {
        for(i in 0 until (VRam.VRAM_END - VRam.VRAM_START + 1)) {
            val addr = (i + VRam.VRAM_START).toUShort()
            data[i] = bus.read(addr).toByte()
        }
    }

}