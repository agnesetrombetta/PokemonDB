package com.valkaris.pokemondb.model

// SmallPokemon: rappresenta un Pokémon nella lista
data class SmallPokemon(
    val id: Int,
    val name: String,
    val url: String
)

fun SmallPokemon.imageUrl(): String {
    val id = url.trimEnd('/').split("/").last()
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}
// SmallPokemonResponse: risposta della lista Pokémon
data class SmallPokemonResponse(
    val pokemons: List<SmallPokemon>
)
// SmallPokemonListResponse: risposta della lista Pokémon con risultati
data class SmallPokemonListResponse(
    val results: List<SmallPokemon>
)

// Sprites: immagini del Pokémon
data class Sprites(
    val front_default: String
)

// Ability: nome dell'abilità
data class Ability(
    val name: String
)

// AbilityEntry: struttura per la lista delle abilità
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

// LargePokemon: dettagli completi del Pokémon
data class LargePokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val abilities: List<AbilityEntry>,
    val flavor_text_entries: List<FlavorTextEntry>? = null
)


data class LargePokemonResponse(
    val drinks: List<LargePokemon>
)