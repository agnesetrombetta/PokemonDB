package com.valkaris.pokemondb.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MediatorLiveData
import com.valkaris.pokemondb.data.FavoriteManager
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch



class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PokemonRepository(application)

    private val favoriteManager = FavoriteManager(application)
    val favorites: LiveData<Set<String>> = favoriteManager.favorites.asLiveData()

    private val _showOnlyFavorites = MutableLiveData(false)
    val showOnlyFavorites: LiveData<Boolean> = _showOnlyFavorites

    private val _pokemons = MutableLiveData<List<SmallPokemon>>()
    val pokemons: LiveData<List<SmallPokemon>> = _pokemons

    private val _detailsMap = MutableLiveData<Map<String, LargePokemon>>(emptyMap())
    val detailsMap: LiveData<Map<String, LargePokemon>> = _detailsMap

    private val _expandedPokemonId = MutableLiveData<String?>(null)
    val expandedPokemonId: LiveData<String?> = _expandedPokemonId

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filteredPokemons = MediatorLiveData<List<SmallPokemon>>()
    val filteredPokemons: LiveData<List<SmallPokemon>> = _filteredPokemons

    init {
        _filteredPokemons.addSource(_pokemons) { filterPokemons() }
        _filteredPokemons.addSource(_searchQuery) { filterPokemons() }
    }

    private fun filterPokemons() {
        val list = _pokemons.value ?: emptyList()
        val query = _searchQuery.value?.trim()?.lowercase() ?: ""
        if (query.isEmpty()) {
            _filteredPokemons.value = list
        } else {
            _filteredPokemons.value = list.filter { it.name.startsWith(query) }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(pokemonName: String) {
        viewModelScope.launch {
            favoriteManager.toggleFavorite(pokemonName)

        }
    }


    fun togglePokemonExpansion(name: String) {
        if (_expandedPokemonId.value == name) {
            _expandedPokemonId.value = null
        } else {
            _expandedPokemonId.value = name
            loadDetails(name)
        }
    }

    init {
        // Carichiamo i primi 20 Pokémon appena avviata l’app
        loadInitialPokemons()
    }

    // Carica i primi 20 Pokémon
    fun loadInitialPokemons() {
        repo.fetchPokemonsPage(limit = 1302, offset = 0) { list ->
            _pokemons.postValue(list ?: emptyList())
        }
    }

    // Carica altri Pokémon con paginazione
    fun loadMorePokemons(offset: Int) {
        repo.fetchPokemonsPage(limit = 20, offset = offset) { list ->
            val current = _pokemons.value ?: emptyList()
            _pokemons.postValue(current + (list ?: emptyList()))
        }
    }

    // Imposta se mostrare solo i preferiti
    fun setShowOnlyFavorites(shouldShow: Boolean) {
        _showOnlyFavorites.value = shouldShow
    }

    // Cerca Pokémon per nome (es. "pikachu")
    fun searchPokemonByName(name: String) {
        val cleanedName = name.trim().lowercase()
        if (cleanedName.isBlank()) return

        repo.fetchPokemon(cleanedName) { pokemon ->
            if (pokemon != null) {
                val currentMap = _detailsMap.value ?: emptyMap()
                _detailsMap.postValue(currentMap + (cleanedName to pokemon))
                _expandedPokemonId.value = cleanedName
            }
        }
    }

    // Mostra i dettagli di un Pokémon (nome o id)
    fun loadDetails(name: String) {
        repo.fetchPokemon(name) { pokemon ->
            if (pokemon != null) {
                // Qui dentro aggiungi la chiamata al species per il flavor text
                repo.fetchPokemonSpecies(name) { species ->
                    val flavorText = species?.flavor_text_entries
                        ?.find { it.language.name == "en" }
                        ?.flavor_text ?: "Flavor text non disponibile"

                    val fullPokemon = pokemon.copy(
                        flavor_text_entries = listOf(
                            FlavorTextEntry(
                                flavor_text = flavorText,
                                language = FlavorTextLanguage(name = "en")
                            )
                        )
                    )

                    val currentMap = _detailsMap.value ?: emptyMap()
                    _detailsMap.postValue(currentMap + (name to fullPokemon))
                }
            }
        }
    }


    // Rimuove i dettagli (quando chiudi la scheda di un Pokémon)
    fun removeDetails(name: String) {
        val currentMap = _detailsMap.value ?: return
        _detailsMap.value = currentMap - name
    }

}
// Factory per creare il ViewModel
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
