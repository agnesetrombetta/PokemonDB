package com.valkaris.pokemondb.ui.theme


import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.valkaris.pokemondb.R

object PokemonDBTextStyles {
    private val tt = FontFamily(Font(R.font.pokemonfont))
    val titleVeryHuge = TextStyle(
        fontSize = 24.sp,
        fontFamily = tt,
        fontWeight = FontWeight.Bold
    )
    val titleHuge = TextStyle(
        fontSize = 20.sp,
        fontFamily = tt,
        fontWeight = FontWeight.Bold
    )

    val titleLarge = TextStyle(
        fontSize = 16.sp,
        fontFamily = tt,
        fontWeight = FontWeight.Bold
    )
    val titleMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = tt,
        fontWeight = FontWeight.Bold
    )

    val bodyLarge = TextStyle(
        fontSize = 14.sp,
        fontFamily = tt,
        fontWeight = FontWeight.Normal
    )
    val bodyMedium = TextStyle(
        fontSize = 12.sp,
        fontFamily = tt,
        fontWeight = FontWeight.Normal,
    )

}