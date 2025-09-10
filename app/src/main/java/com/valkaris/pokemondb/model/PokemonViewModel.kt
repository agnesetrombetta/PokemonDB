package com.valkaris.pokemondb.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PokemonRepository(application)

    private val _Pokemons = MutableLiveData<List<SmallPokemon>>()
    val Pokemons: LiveData<List<SmallPokemon>> = _Pokemons

    private val _Pokemons = MutableLiveData<List<LargePokemon>>()
    val Pokemons: LiveData<List<LargePokemon>> = _Pokemons

    init {
        loadPokemons("Pikachu")
    }

    fun loadPokemons(filter: String = "") {
        if (filter.isBlank()) {
            _Pokemons.value = emptyList()
            return
        }
        repo.fetchPokemons(filter) {
            _Pokemons.value = it ?: emptyList()
        }
    }
    fun removePokemon() {
        _Pokemons.value = emptyList()
    }
    fun loadPokemons(id: Int) {
        repo.fetchPokemon(id) {
            _Pokemons.value = it ?: emptyList()
        }
    }
}

class PokemonViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
