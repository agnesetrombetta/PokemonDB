package com.valkaris.pokemondb.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.valkaris.pokemondb.R

object PokemonDBTextStyles {
    // Fredoka standard
    private val Fredoka = FontFamily(
        Font(R.font.fredoka_regular, FontWeight.Normal),
        Font(R.font.fredoka_bold, FontWeight.Bold)
    )

    // Fredoka Condensed
    private val FredokaCondensed = FontFamily(
        Font(R.font.fredoka_condensed_regular, FontWeight.Normal),
        Font(R.font.fredoka_condensed_bold, FontWeight.Bold)
    )

    // Titles with Pokémon fonts
    val titleExtraHuge = TextStyle(
        fontSize = 34.sp,
        fontFamily = FredokaCondensed,
        fontWeight = FontWeight.Bold
    )

    val titleHuge = TextStyle(
        fontSize = 20.sp,
        fontFamily = FredokaCondensed,
        fontWeight = FontWeight.Bold
    )
    val bodyCard = TextStyle(
        fontSize = 20.sp,
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold
    )
    val titleLarge = TextStyle(
        fontSize = 16.sp,
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold
    )

    // Body with Fredoka (more readable)
    val bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontFamily = Fredoka,
        fontWeight = FontWeight.Normal
    )
}
