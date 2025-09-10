package com.valkaris.pokemondb.model

data class SmallPokemon(
    val idPokemon: String,
    val strPokemon: String,
    val strPokemonThumb: String
)

data class SmallPokemonResponse(
    val Pokemons: List<SmallPokemon>?
)

data class LargePokemon(
    val idPokemon: String,
    val strPokemon: String,
    val strPokemonThumb: String,
    val strPokemonDescription: String,
    val strPokemonType: String,
    val strPokemonHeight: String,
    val strPokemonWeight: String,
    val strPokemonAbilities: String,
    val strPokemonStats: String,
    val strPokemonMoves: String,
    val strPokemonSpecies: String,
    val strPokemonGender: String,
    val strPokemonEggGroup: String,
    val strPokemonHabitat: String,
    val strPokemonShape: String,
    val strPokemonBaseExperience: String,
    val strPokemonCaptureRate: String,
    val strPokemonGrowthRate: String,
    val strPokemonEggCycles: String,
    val strPokemonColor: String
)

data class PokemonDetailsResponse(
    val Pokemons: List<LargePokemon>
)
