package com.github.nathanieloliveira.kolorful.ui.jvm

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.nathanieloliveira.kolorful.ui.KolorfulApp

fun main() {
    application {
        val windowState = rememberWindowState(
            size = DpSize(1400.dp, 900.dp)
        )
        Window(
            ::exitApplication,
            state = windowState,
            title = "Kolorful"
        ) {
            KolorfulApp()
        }
    }
}