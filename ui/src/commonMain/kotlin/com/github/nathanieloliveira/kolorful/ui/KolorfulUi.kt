package com.github.nathanieloliveira.kolorful.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun KolorfulApp() {
    MaterialTheme {
        Surface(color = MaterialTheme.colors.background) {
            Text("Hello world")
        }
    }
}