package com.valkaris.pokemondb.model

// SmallPokemon: Represents a Pokémon in the list
data class SmallPokemon(
    val id: Int,
    val name: String,
    val url: String
)

fun SmallPokemon.imageUrl(): String {
    val id = url.trimEnd('/').split("/").last()
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}

// SmallPokemonListResponse: Pokémon list response with results
data class SmallPokemonListResponse(
    val results: List<SmallPokemon>
)

// Sprites: Images of the Pokémon
data class Sprites(
    val front_default: String
)

// Ability: name of the ability
data class Ability(
    val name: String
)

// AbilityEntry: Structure for the list of abilities
data class AbilityEntry(
    val ability: Ability
)

data class PokemonSpecies(
    val flavor_text_entries: List<FlavorTextEntry>?
)

data class FlavorTextEntry(
    val flavor_text: String,
    val language: FlavorTextLanguage
)

data class FlavorTextLanguage(
    val name: String
)

// LargePokemon: Complete Pokémon Details
data class LargePokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val abilities: List<AbilityEntry>,
    val flavor_text_entries: List<FlavorTextEntry>? = null,
    val types: List<PokemonTypeEntry>
)

data class PokemonTypeEntry(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String,
    val url: String
)