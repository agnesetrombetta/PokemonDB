package com.valkaris.pokemondb.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class PokemonRepository (private val context: Context) {
    private val baseUrl = "https://pokeapi.co/api/v2/"
    private val urlById = "${baseUrl}pokemon/"

    // Pokémon Page List
    fun fetchPokemonsPage(limit: Int = 20, offset: Int = 0, onResult: (List<SmallPokemon>?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = "${baseUrl}pokemon?limit=$limit&offset=$offset"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                val result = Gson().fromJson(response, SmallPokemonListResponse::class.java)
                onResult(result.results)
            },
            { error ->
                error.printStackTrace()
                onResult(null)
            }
        )

        queue.add(request)
    }

    // Details of a single Pokémon
    fun fetchPokemon(nameOrId: String, onResult: (LargePokemon?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = "$urlById$nameOrId"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                val pokemon = Gson().fromJson(response, LargePokemon::class.java)
                onResult(pokemon)
            },
            { error ->
                error.printStackTrace()
                onResult(null)
            }
        )

        queue.add(request)
    }

    fun fetchPokemonSpecies(name: String, onResult: (PokemonSpecies?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = "${baseUrl}pokemon-species/$name"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                val species = Gson().fromJson(response, PokemonSpecies::class.java)
                onResult(species)
            },
            { error ->
                error.printStackTrace()
                onResult(null)
            }
        )

        queue.add(request)
    }
}