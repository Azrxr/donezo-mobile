package com.jasawira.donezo.presentation.theme

import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Color Preset Configuration
 * Total 20 Presets: 10 Pastel, 5 Vibrant (High Contrast), 5 Deep/Dark
 * * Setiap preset memiliki:
 * - primaryColor: Untuk progress bar, checkbox, highlight
 * - backgroundColor: Background card
 * - textColor: Text judul, label
 * - accentColor: Icon, button, emphasis
 * - borderColor: Border card
 */

data class ColorPreset(
    val id: Int,
    val name: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val textColor: Color,
    val accentColor: Color,
    val borderColor: Color
)

object ColorPresets {
    // ==========================================
    // 1. PASTEL SERIES (Original)
    // ==========================================
    val pastelPink = ColorPreset(
        id = 0, name = "Pastel Pink",
        primaryColor = Color(0xFFFFB3D9), backgroundColor = Color(0xFFFFE5F0),
        textColor = Color(0xFFC2185B), accentColor = Color(0xFFFF69B4), borderColor = Color(0xFFFFB3D9)
    )
    val pastelBlue = ColorPreset(
        id = 1, name = "Pastel Blue",
        primaryColor = Color(0xFFB3E5FC), backgroundColor = Color(0xFFE1F5FE),
        textColor = Color(0xFF0277BD), accentColor = Color(0xFF0288D1), borderColor = Color(0xFFB3E5FC)
    )
    val pastelGreen = ColorPreset(
        id = 2, name = "Pastel Green",
        primaryColor = Color(0xFFC8E6C9), backgroundColor = Color(0xFFE8F5E9),
        textColor = Color(0xFF388E3C), accentColor = Color(0xFF43A047), borderColor = Color(0xFFC8E6C9)
    )
    val pastelYellow = ColorPreset(
        id = 3, name = "Pastel Yellow",
        primaryColor = Color(0xFFFFF9C4), backgroundColor = Color(0xFFFFFDE7),
        textColor = Color(0xFFF57F17), accentColor = Color(0xFFFBC02D), borderColor = Color(0xFFFFF9C4)
    )
    val pastelPurple = ColorPreset(
        id = 4, name = "Pastel Purple",
        primaryColor = Color(0xFFE1BEE7), backgroundColor = Color(0xFFF3E5F5),
        textColor = Color(0xFF7B1FA2), accentColor = Color(0xFFAF2CC5), borderColor = Color(0xFFE1BEE7)
    )
    val pastelPeach = ColorPreset(
        id = 5, name = "Pastel Peach",
        primaryColor = Color(0xFFFFCCBC), backgroundColor = Color(0xFFFFE0B2),
        textColor = Color(0xFFD84315), accentColor = Color(0xFFFF5722), borderColor = Color(0xFFFFCCBC)
    )
    val pastelMint = ColorPreset(
        id = 6, name = "Pastel Mint",
        primaryColor = Color(0xFFB2DFDB), backgroundColor = Color(0xFFE0F2F1),
        textColor = Color(0xFF00796B), accentColor = Color(0xFF009688), borderColor = Color(0xFFB2DFDB)
    )
    val pastelLavender = ColorPreset(
        id = 7, name = "Pastel Lavender",
        primaryColor = Color(0xFFDDA0DD), backgroundColor = Color(0xFFEDD5F1),
        textColor = Color(0xFF6A0572), accentColor = Color(0xFF9C27B0), borderColor = Color(0xFFDDA0DD)
    )
    val pastelCoral = ColorPreset(
        id = 8, name = "Pastel Coral",
        primaryColor = Color(0xFFFFAB91), backgroundColor = Color(0xFFFFE0B2),
        textColor = Color(0xFFE64A19), accentColor = Color(0xFFFF6E40), borderColor = Color(0xFFFFAB91)
    )
    val pastelCyan = ColorPreset(
        id = 9, name = "Pastel Cyan",
        primaryColor = Color(0xFF80DEEA), backgroundColor = Color(0xFFE0F7FA),
        textColor = Color(0xFF00838F), accentColor = Color(0xFF00ACC1), borderColor = Color(0xFF80DEEA)
    )

    // ==========================================
    // 2. VIBRANT SERIES (Kontras Tinggi & Tegas)
    // ==========================================
    val vibrantIndigo = ColorPreset(
        id = 10, name = "Vibrant Indigo",
        primaryColor = Color(0xFF3F51B5), backgroundColor = Color(0xFFF4F5FA), // Latar sangat terang
        textColor = Color(0xFF1A237E), accentColor = Color(0xFF303F9F), borderColor = Color(0xFFC5CAE9)
    )
    val vibrantCrimson = ColorPreset(
        id = 11, name = "Vibrant Crimson",
        primaryColor = Color(0xFFE53935), backgroundColor = Color(0xFFFFF5F5),
        textColor = Color(0xFFB71C1C), accentColor = Color(0xFFD32F2F), borderColor = Color(0xFFFFCDD2)
    )
    val vibrantEmerald = ColorPreset(
        id = 12, name = "Vibrant Emerald",
        primaryColor = Color(0xFF43A047), backgroundColor = Color(0xFFF1F8E9),
        textColor = Color(0xFF1B5E20), accentColor = Color(0xFF2E7D32), borderColor = Color(0xFFC8E6C9)
    )
    val vibrantOrange = ColorPreset(
        id = 13, name = "Vibrant Orange",
        primaryColor = Color(0xFFF57C00), backgroundColor = Color(0xFFFFF3E0),
        textColor = Color(0xFFE65100), accentColor = Color(0xFFEF6C00), borderColor = Color(0xFFFFE0B2)
    )
    val vibrantViolet = ColorPreset(
        id = 14, name = "Vibrant Violet",
        primaryColor = Color(0xFF8E24AA), backgroundColor = Color(0xFFF3E5F5),
        textColor = Color(0xFF4A148C), accentColor = Color(0xFF7B1FA2), borderColor = Color(0xFFE1BEE7)
    )

    // ==========================================
    // 3. DEEP / DARK SERIES (Elegan & Nyaman di Mata)
    // ==========================================
    val deepMidnight = ColorPreset(
        id = 15, name = "Midnight Blue",
        primaryColor = Color(0xFF7986CB), backgroundColor = Color(0xFF1A237E), // Latar gelap
        textColor = Color(0xFFFFFFFF), accentColor = Color(0xFF9FA8DA), borderColor = Color(0xFF3949AB)
    )
    val deepForest = ColorPreset(
        id = 16, name = "Forest Green",
        primaryColor = Color(0xFF81C784), backgroundColor = Color(0xFF1B5E20),
        textColor = Color(0xFFFFFFFF), accentColor = Color(0xFFA5D6A7), borderColor = Color(0xFF2E7D32)
    )
    val deepMaroon = ColorPreset(
        id = 17, name = "Deep Maroon",
        primaryColor = Color(0xFFF06292), backgroundColor = Color(0xFF880E4F),
        textColor = Color(0xFFFFFFFF), accentColor = Color(0xFFF48FB1), borderColor = Color(0xFFAD1457)
    )
    val deepCharcoal = ColorPreset(
        id = 18, name = "Charcoal Grey",
        primaryColor = Color(0xFFBDBDBD), backgroundColor = Color(0xFF212121),
        textColor = Color(0xFFFFFFFF), accentColor = Color(0xFFE0E0E0), borderColor = Color(0xFF424242)
    )
    val deepTeal = ColorPreset(
        id = 19, name = "Ocean Teal",
        primaryColor = Color(0xFF4DB6AC), backgroundColor = Color(0xFF004D40),
        textColor = Color(0xFFFFFFFF), accentColor = Color(0xFF80CBC4), borderColor = Color(0xFF00695C)
    )

    // List semua preset
    val allPresets = listOf(
        pastelPink, pastelBlue, pastelGreen, pastelYellow, pastelPurple,
        pastelPeach, pastelMint, pastelLavender, pastelCoral, pastelCyan,
        vibrantIndigo, vibrantCrimson, vibrantEmerald, vibrantOrange, vibrantViolet,
        deepMidnight, deepForest, deepMaroon, deepCharcoal, deepTeal
    )

    // Helper function untuk dapatkan preset by id
    fun getPresetById(id: Int): ColorPreset {
        return allPresets.find { it.id == id } ?: vibrantIndigo // Default fallback ke Vibrant Indigo yang lebih kontras
    }
}

// Top-level helpers (lebih mudah dipanggil dari composable lain)
@Composable
fun ColorPreset.contentColor(): Color {
    return contentColorFor(backgroundColor)
}

fun ColorPreset.softBackground(): Color {
    // Sedikit berbeda untuk Dark Series agar warnanya tidak terlalu pudar
    return if (id >= 15) backgroundColor.copy(alpha = 0.95f) else backgroundColor.copy(alpha = 0.9f)
}

fun ColorPreset.borderColor(): Color {
    return primaryColor.copy(alpha = 0.3f)
}