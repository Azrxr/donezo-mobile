package com.jasawira.donezo.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Color Preset Configuration untuk 10 warna pastel
 * Setiap preset memiliki:
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
    // 1. Pastel Pink
    val pastelPink = ColorPreset(
        id = 0,
        name = "Pastel Pink",
        primaryColor = Color(0xFFFFB3D9),
        backgroundColor = Color(0xFFFFE5F0),
        textColor = Color(0xFFC2185B),
        accentColor = Color(0xFFFF69B4),
        borderColor = Color(0xFFFFB3D9)
    )

    // 2. Pastel Blue
    val pastelBlue = ColorPreset(
        id = 1,
        name = "Pastel Blue",
        primaryColor = Color(0xFFB3E5FC),
        backgroundColor = Color(0xFFE1F5FE),
        textColor = Color(0xFF0277BD),
        accentColor = Color(0xFF0288D1),
        borderColor = Color(0xFFB3E5FC)
    )

    // 3. Pastel Green
    val pastelGreen = ColorPreset(
        id = 2,
        name = "Pastel Green",
        primaryColor = Color(0xFFC8E6C9),
        backgroundColor = Color(0xFFE8F5E9),
        textColor = Color(0xFF388E3C),
        accentColor = Color(0xFF43A047),
        borderColor = Color(0xFFC8E6C9)
    )

    // 4. Pastel Yellow
    val pastelYellow = ColorPreset(
        id = 3,
        name = "Pastel Yellow",
        primaryColor = Color(0xFFFFF9C4),
        backgroundColor = Color(0xFFFFFDE7),
        textColor = Color(0xFFF57F17),
        accentColor = Color(0xFFFBC02D),
        borderColor = Color(0xFFFFF9C4)
    )

    // 5. Pastel Purple
    val pastelPurple = ColorPreset(
        id = 4,
        name = "Pastel Purple",
        primaryColor = Color(0xFFE1BEE7),
        backgroundColor = Color(0xFFF3E5F5),
        textColor = Color(0xFF7B1FA2),
        accentColor = Color(0xFFAF2CC5),
        borderColor = Color(0xFFE1BEE7)
    )

    // 6. Pastel Peach
    val pastelPeach = ColorPreset(
        id = 5,
        name = "Pastel Peach",
        primaryColor = Color(0xFFFFCCBC),
        backgroundColor = Color(0xFFFFE0B2),
        textColor = Color(0xFFD84315),
        accentColor = Color(0xFFFF5722),
        borderColor = Color(0xFFFFCCBC)
    )

    // 7. Pastel Mint
    val pastelMint = ColorPreset(
        id = 6,
        name = "Pastel Mint",
        primaryColor = Color(0xFFB2DFDB),
        backgroundColor = Color(0xFFE0F2F1),
        textColor = Color(0xFF00796B),
        accentColor = Color(0xFF009688),
        borderColor = Color(0xFFB2DFDB)
    )

    // 8. Pastel Lavender
    val pastelLavender = ColorPreset(
        id = 7,
        name = "Pastel Lavender",
        primaryColor = Color(0xFFDDA0DD),
        backgroundColor = Color(0xFFEDD5F1),
        textColor = Color(0xFF6A0572),
        accentColor = Color(0xFF9C27B0),
        borderColor = Color(0xFFDDA0DD)
    )

    // 9. Pastel Coral
    val pastelCoral = ColorPreset(
        id = 8,
        name = "Pastel Coral",
        primaryColor = Color(0xFFFFAB91),
        backgroundColor = Color(0xFFFFE0B2),
        textColor = Color(0xFFE64A19),
        accentColor = Color(0xFFFF6E40),
        borderColor = Color(0xFFFFAB91)
    )

    // 10. Pastel Cyan
    val pastelCyan = ColorPreset(
        id = 9,
        name = "Pastel Cyan",
        primaryColor = Color(0xFF80DEEA),
        backgroundColor = Color(0xFFE0F7FA),
        textColor = Color(0xFF00838F),
        accentColor = Color(0xFF00ACC1),
        borderColor = Color(0xFF80DEEA)
    )

    // List semua preset
    val allPresets = listOf(
        pastelPink,
        pastelBlue,
        pastelGreen,
        pastelYellow,
        pastelPurple,
        pastelPeach,
        pastelMint,
        pastelLavender,
        pastelCoral,
        pastelCyan
    )

    // Helper function untuk dapatkan preset by id
    fun getPresetById(id: Int): ColorPreset {
        return allPresets.find { it.id == id } ?: pastelBlue // default ke blue
    }
}

