@file:OptIn(ExperimentalStdlibApi::class)

package com.github.nathanieloliveira.kolorful.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.github.nathanieloliveira.kolorful.Cartridge
import com.github.nathanieloliveira.kolorful.Console
import com.github.nathanieloliveira.kolorful.Cpu
import com.github.nathanieloliveira.kolorful.Cpu.InstructionWithAddress
import com.github.nathanieloliveira.kolorful.CpuState
import com.github.nathanieloliveira.kolorful.Register
import com.github.nathanieloliveira.kolorful.VRam
import com.github.nathanieloliveira.kolorful.affectedRegisters
import kolorful.ui.generated.resources.JetBrainsMono_Regular
import kolorful.ui.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Data
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import org.jetbrains.skia.Image as SkiaImage

val LocalMonospaceFont = staticCompositionLocalOf<FontFamily?> { null }

@Composable
fun HexState(
    label: String,
    state: String,
    highlightColor: Color,
    modifier: Modifier = Modifier,
) {
    val textColor = contentColorFor(highlightColor)
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label)
        Text(
            modifier = Modifier.border(1.dp, Color.Black).background(highlightColor).padding(4.dp),
            text = state,
            color = textColor,
            fontFamily = LocalMonospaceFont.current
        )
    }
}

@Composable
fun FlagState(
    label: String,
    state: Boolean,
    highlight: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label)
        Checkbox(state, {}, enabled = highlight)
    }
}


@Composable
fun Section(
    title: String,
    content: @Composable RowScope.() -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(title)
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                content()
            }
        }
    }
}

fun UShort.toHex(): String {
    return toHexString(HexFormat.UpperCase)
}

fun UByte.toHex(): String {
    return toHexString(HexFormat.UpperCase)
}

fun ByteArray.toHex(): String {
    return toHexString(HexFormat.UpperCase)
}

@Composable
fun CpuStateView(
    modifier: Modifier,
    state: CpuState,
    lastState: CpuState,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Section("Next") {
            Text(state.next.toString())
        }

        val affected = (state.next as? Cpu.Normal)?.instruction?.affectedRegisters()

        @Composable
        fun modifiedOrAffected(register: Register, check: () -> Boolean): Color {
            if (check()) {
                return MaterialTheme.colors.primary
            }
            val read = affected?.read == register
            val write = affected?.write == register
            if (read || write) {
                return MaterialTheme.colors.secondary
            }
            return MaterialTheme.colors.surface
        }

        Section("R16") {
            HexState(
                "AF",
                state.af.toHex(),
                modifiedOrAffected(Register.AF) {
                    lastState.af != state.af
                }
            )
            HexState(
                "BC",
                state.bc.toHex(),
                modifiedOrAffected(Register.BC) { lastState.bc != state.bc }
            )
            HexState(
                "DE",
                state.de.toHex(),
                modifiedOrAffected(Register.DE) { lastState.de != state.de }
            )
            HexState(
                "HL",
                state.hl.toHex(),
                modifiedOrAffected(Register.HL) { lastState.hl != state.hl }
            )
        }
        Section(
            "R8"
        ) {
            HexState("A", state.a.toHex(),
                modifiedOrAffected(Register.A) { lastState.a != state.a }
            )
            HexState("B", state.b.toHex(),
                modifiedOrAffected(Register.B) { lastState.b != state.b }
            )
            HexState("C", state.c.toHex(),
                modifiedOrAffected(Register.C) { lastState.c != state.c }
            )
            HexState("D", state.d.toHex(),
                modifiedOrAffected(Register.D) { lastState.d != state.d }
            )
            HexState("E", state.e.toHex(),
                modifiedOrAffected(Register.E) { lastState.e != state.e }
            )
            HexState("H", state.h.toHex(),
                modifiedOrAffected(Register.H) { lastState.h != state.h }
            )
            HexState("L", state.l.toHex(),
                modifiedOrAffected(Register.L) { lastState.l != state.l }
            )
        }
        Section(
            "Address"
        ) {
            HexState("PC", state.pc.toHex(), modifiedOrAffected(Register.PC) { lastState.pc != state.pc })
            HexState("SP", state.sp.toHex(), modifiedOrAffected(Register.SP) { lastState.sp != state.sp })
        }
        Section(
            "Flags"
        ) {
            FlagState("ZERO", state.z, lastState.z != state.z)
            FlagState("NEGATIVE", state.n, lastState.n != state.n)
            FlagState("HALF", state.half, lastState.half != state.half)
            FlagState("CARRY", state.carry, lastState.carry != state.carry)
        }
        Section("Interrupts") {
            FlagState("IME", state.ime, lastState.ime)
            HexState("IE", state.interruptEnable.toHex(), Color.White)
            HexState("IF", state.interruptEnable.toHex(), Color.White)
        }
        Section(
            "Stack"
        ) {
            Text(state.stack.toHex())
        }
    }
}

@Composable
fun TileDataDebugView(
    palette: Array<Color>,
    data: ByteArray,
    maxTilesHorizontal: Int,
    version: Int,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    val numTiles = data.size / 16
    val tileSize = 8
    val tilesHorizontal = maxTilesHorizontal.coerceAtMost(numTiles)
    val width = tilesHorizontal * tileSize
    val height = (numTiles / tilesHorizontal) * tileSize
    // ARGB format
    val pixels = remember {
        ByteArray(numTiles * tileSize * tileSize * Int.SIZE_BYTES).also {
            for (i in 3 until it.size step 4) {
                it[i] = 0xFF.toByte()
            }
        }
    }

    val imageInfo = ImageInfo(
        width,
        height,
        ColorType.RGBA_8888,
        ColorAlphaType.UNPREMUL
    )

    fun writeColor(array: ByteArray, color: Color, offset: Int) {
        array[offset + 0] = (color.red * 255f).roundToInt().toByte()
        array[offset + 1] = (color.green * 255f).roundToInt().toByte()
        array[offset + 2] = (color.blue * 255f).roundToInt().toByte()
        array[offset + 3] = (color.alpha * 255f).roundToInt().toByte()
    }

    Canvas(modifier) {
        val tileData = ByteArray(16)
        val drawScope = this

        for (tile in 0 until numTiles) {
            val startIndex = tile * 16
            data.copyInto(tileData, destinationOffset = 0, startIndex = startIndex, endIndex = startIndex + 16)

            val tileX = tile % maxTilesHorizontal
            val tileY = tile / maxTilesHorizontal

            val tileMemOffset = (tileY * width + tileX) * tileSize * Int.SIZE_BYTES

            for (line in 0 until 8) {
                val least = tileData[line * 2].toInt()
                val most = tileData[line * 2 + 1].toInt()

                for (pixel in 0 until 8) {
                    val mask = 0x80 shr pixel
                    val mostMasked = most and mask
                    val leastMasked = least and mask
                    val orEd = (mostMasked shl 1) or (leastMasked)
                    val pixelColorIndex = orEd shr (7 - pixel)
                    val pixelColor = palette[pixelColorIndex]

                    val offset = tileMemOffset + (line * width + pixel) * Int.SIZE_BYTES
                    writeColor(pixels, pixelColor, offset)
                }
            }
        }

        val pixmap = Pixmap.make(
            imageInfo,
            Data.makeFromBytes(pixels),
            width * Int.SIZE_BYTES
        )
        SkiaImage.makeFromPixmap(pixmap).use { image ->
            drawScope.drawIntoCanvas {
                scale(scale, pivot = Offset.Zero) {
                    it.nativeCanvas.drawImage(image, 0f, 0f)
                }
            }
        }
    }
}

@Composable
fun DisassemblyView(
    disassembly: List<InstructionWithAddress>,
    currentAddress: UShort,
    before: Int,
    after: Int,
    block: Int,
    breakpoints: Set<UShort>,
    onRowClicked: (UShort) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentInstruction = disassembly.indexOfFirst { it.address == currentAddress }
    val toShow = if (disassembly.isEmpty() || currentInstruction < 0) {
        emptyList()
    } else {
        val firstIndex = run {
            val a = ((currentInstruction - before) / block) * block
            a.coerceAtLeast(0)
        }
        val lastIndex = run {
            val a = ((currentInstruction + after) / block) * block
            a.coerceAtMost(disassembly.lastIndex)
        }
        disassembly.subList(firstIndex, lastIndex)
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(2.dp),
    ) {
        items(
            toShow,
            key = { it.address },
            contentType = { 1 },
        ) { instr ->
            val color = when (instr.address) {
                currentAddress -> MaterialTheme.colors.secondary
                in breakpoints -> MaterialTheme.colors.error
                else -> Color.Unspecified
            }
            val textColor = contentColorFor(color)
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (instr.address in breakpoints) {
                    Image(Icons.Default.AddCircle, null, modifier = Modifier.size(20.dp))
                }
                Text(
                    modifier = Modifier.fillMaxWidth().background(color).clickable {
                        onRowClicked(instr.address)
                    },
                    text = "${instr.address.toHex()}: ${instr.instruction}",
                    color = textColor,
                    style = MaterialTheme.typography.body1,
                    fontFamily = LocalMonospaceFont.current
                )
            }
        }
    }
}

val debugTiles = intArrayOf(
    0x7C, 0x7C, 0x00, 0xC6, 0xC6, 0x00, 0x00, 0xFE, 0xC6, 0xC6, 0x00, 0xC6, 0xC6, 0x00, 0x00, 0x00,
    0x3C, 0x7E, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x7E, 0x5E, 0x7E, 0x0A, 0x7C, 0x56, 0x38, 0x7C,
    0xFF, 0x00, 0x7E, 0xFF, 0x85, 0x81, 0x89, 0x83, 0x93, 0x85, 0xA5, 0x8B, 0xC9, 0x97, 0x7E, 0xFF,
    0xFF, 0x00, 0x7E, 0xFF, 0x85, 0x81, 0x89, 0x83, 0x93, 0x85, 0xA5, 0x8B, 0xC9, 0x97, 0x7E, 0xFF,
).let { orig ->
    ByteArray(orig.size) { i ->
        orig[i].toByte()
    }
}

@Composable
fun KolorfulApp() {

    val emulator = remember {
        val cart = Cartridge(
            Console::class.java.getResourceAsStream("/individual/01-special.gb")!!.readAllBytes()!!
        )
        Console(cartridge = cart).also {
            it.cpu.isBoot = false
        }
    }

    var runningEmulator by remember { mutableStateOf(false) }
    var currentCpuState by remember { mutableStateOf(emulator.cpu.getState()) }
    var lastCpuState by remember { mutableStateOf(emulator.cpu.getState()) }
    var cartridge by remember { mutableStateOf(false) }

    val disassembly = remember(cartridge) { emulator.cpu.getDisassembly() }

    var stop by remember { mutableStateOf(false) }
    var delay by remember { mutableStateOf(10f) }
    var tick by remember { mutableLongStateOf(0L) }
    var runUntil by remember { mutableLongStateOf(0L) }

    var version by remember { mutableStateOf(0) }
    val tileData = remember { ByteArray(VRam.VRAM_END - VRam.VRAM_START + 1) }
    var palette by remember {
        mutableStateOf(
            arrayOf(
                Color.White,
                Color.Gray,
                Color.DarkGray,
                Color.Black,
            )
        )
    }

    suspend fun runCpu(maxTicks: Int) {
        runCatching {
            runningEmulator = true
            for (i in 0 until maxTicks) {
                lastCpuState = emulator.cpu.getState()
                emulator.tick()
                currentCpuState = emulator.cpu.getState()
                emulator.getTileData(tileData)
                if (stop) {
                    stop = false
                    break
                }
                if (maxTicks > 1 && i % 100 == 0) {
                    version += 1
                }

                cartridge = !emulator.cpu.isBoot
                tick += 1
                delay(delay.roundToInt().milliseconds)

                if (emulator.cpu.pc in emulator.cpu.breakpoints.value) {
                    break
                }
            }
            version += 1
            runningEmulator = false
        }.onFailure {
            println("crashed on tick $tick")
            it.printStackTrace()
            runningEmulator = false

            currentCpuState = emulator.cpu.getState()
            println(emulator.cpu.getState())
        }
    }

    val scope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalMonospaceFont provides FontFamily(Font(Res.font.JetBrainsMono_Regular))
    ) {
        MaterialTheme {
            Surface(color = MaterialTheme.colors.background) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        CpuStateView(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            currentCpuState,
                            lastCpuState,
                        )
                        Row {
                            Text(tick.toString())
                            TextButton({
                                scope.launch {
                                    runCpu(1)
                                }
                            }, enabled = !runningEmulator) {
                                Text("Tick")
                            }

                            TextButton({
                                scope.launch(Dispatchers.Default) {
                                    runCpu(Int.MAX_VALUE)
                                }
                            }, enabled = !runningEmulator) {
                                Text("Run")
                            }

                            TextButton({
                                stop = true
                            }, enabled = runningEmulator) {
                                Text("Stop")
                            }

                            TextButton({
                                emulator.reset()
                                currentCpuState = emulator.cpu.getState()
                                lastCpuState = currentCpuState
                                tick = 0
                            }, enabled = !runningEmulator) {
                                Text("Reset")
                            }

                            TextField(runUntil.toString(), onValueChange = {
                                val longs = it.toLongOrNull() ?: return@TextField
                                runUntil = longs
                            })

                            TextButton({
                                val ticks = runUntil
                                runUntil = 0
                                scope.launch(Dispatchers.Default) {
                                    runCpu(ticks.toInt())
                                }
                            }, enabled = !runningEmulator) {
                                Text("Run until")
                            }
                        }
                        Row {
                            Slider(
                                modifier = Modifier.width(320.dp),
                                value = delay,
                                onValueChange = {
                                    delay = it
                                },
                                valueRange = 0f..100f,
                                steps = 101,
                            )
                            Text(
                                text = delay.roundToInt().toString()
                            )
                        }
                    }

                    val breakpoints by emulator.cpu.breakpoints.collectAsState()

                    DisassemblyView(
                        disassembly,
                        currentCpuState.pc,
                        64,
                        after = 128,
                        block = 16,
                        breakpoints = breakpoints,
                        onRowClicked = { addr ->
                            if (addr in breakpoints) {
                                emulator.cpu.removeBreakpoint(addr)
                            } else {
                                emulator.cpu.addBreakpoint(addr)
                            }
                        },
                        modifier = Modifier.width(400.dp).fillMaxHeight()
                    )

                    val maxTilesHorizontal = 8
                    val tilesPx = 8
                    val scale = 4f
                    TileDataDebugView(
                        modifier = Modifier.width((maxTilesHorizontal * tilesPx * scale).dp).fillMaxHeight(),
                        palette = palette,
                        data = debugTiles,
                        maxTilesHorizontal = maxTilesHorizontal,
                        version = version,
                        scale = scale
                    )
                }
            }
        }
    }
}