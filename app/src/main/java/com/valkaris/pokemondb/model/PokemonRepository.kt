package com.valkaris.pokemondb.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class PokemonRepository(private val context: Context) {

    private val url = "https://pokeapi.co/api/v2/"
    private val urlById = "https://pokeapi.co/api/v2/pokemon/"

    fun fetchPokemon(filter: String, onResult: (List<SmallPokemon>?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val search = "$url?s=$filter"

        val request = StringRequest(Request.Method.GET, search,
            { response ->
                val Pokemons = Gson().fromJson(response, SmallPokemonResponse::class.java).Pokemons
                onResult(Pokemons)
            },
            { error ->
                error.printStackTrace()
                onResult(null)
            })

        queue.add(request)
    }

    fun fetchPokemon(id: Int, onResult: (List<LargePokemon>?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val search = "$urlById?i=$id"

        val request = StringRequest(Request.Method.GET, search,
            { response ->
                val Pokemons = Gson().fromJson(response, PokemonDetailsResponse::class.java).Pokemons
                onResult(Pokemons)
            },
            { error ->
                error.printStackTrace()
                onResult(null)
            })

        queue.add(request)
    }
}

}