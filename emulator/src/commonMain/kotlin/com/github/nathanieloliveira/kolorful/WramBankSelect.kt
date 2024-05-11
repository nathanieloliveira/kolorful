package com.github.nathanieloliveira.kolorful

class WramBankSelect(val bankedMemory: BankedMemory): Device {
    override val range: Array<ClosedRange<Int>> = arrayOf(0xFF70..0xFF70)

    override fun read(address: UShort): UByte {
        return bankedMemory.selectedBank.toUByte()
    }

    override fun write(address: UShort, value: UByte) {
        require(value.toInt() < bankedMemory.banks.size) {
            "Tried to set an invalid WRAM Bank. $value"
        }
        bankedMemory.selectedBank = value.toInt()
    }
}