package com.example.playlist_maker_android_rogachevegor_.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color

private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
private const val DARK_THEME_KEY = "dark_theme_key"

fun Context.isDarkThemeEnabled(): Boolean =
    getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        .getBoolean(DARK_THEME_KEY, false)

fun Context.saveDarkThemeEnabled(enabled: Boolean) {
    getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(DARK_THEME_KEY, enabled)
        .apply()
}

data class AppColors(
    val mainHeaderBackground: Color,
    val screenBackground: Color,
    val contentBackground: Color,
    val titleOnHeader: Color,
    val primaryText: Color,
    val primaryIcon: Color,
    val primaryMainIcon: Color,
    val secondaryIcon: Color,
    val switchCheckedThumb: Color,
    val switchCheckedTrack: Color,
    val searchFieldBackground: Color
)

val LightColors = AppColors(
    mainHeaderBackground = Color(0xFF3772E7),
    screenBackground = Color.White,
    contentBackground = Color.White,
    titleOnHeader = Color.White,
    primaryText = Color(0xFF1A1B22),
    primaryIcon = Color(0xFFAEAFB4),
    primaryMainIcon = Color(0xFF1A1B22),
    secondaryIcon = Color(0xFFAEAFB4),
    switchCheckedThumb = Color(0xFF3772E7),
    switchCheckedTrack = Color(0x553772E7),
    searchFieldBackground = Color(0xFFE6E8EB)
)

val DarkColors = AppColors(
    mainHeaderBackground = Color(0xFF3772E7),
    screenBackground = Color(0xFF1A1B22),
    contentBackground = Color(0xFF1A1B22),
    titleOnHeader = Color.White,
    primaryText = Color.White,
    primaryIcon = Color.White,
    primaryMainIcon = Color.White,
    secondaryIcon = Color.White,
    switchCheckedThumb = Color(0xFF3772E7),
    switchCheckedTrack = Color(0x553772E7),
    searchFieldBackground = Color.White
)
