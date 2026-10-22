package dev.cybercore.cyberssh.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberDark = darkColorScheme(
    primary = Color(0xFF35DCE6),
    onPrimary = Color(0xFF061014),
    secondary = Color(0xFF83F7A6),
    tertiary = Color(0xFFEF58B5),
    background = Color(0xFF080B11),
    surface = Color(0xFF111821),
    onSurface = Color(0xFFF1F5F8),
)

@Composable
fun CyberSSHTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CyberDark, content = content)
}
