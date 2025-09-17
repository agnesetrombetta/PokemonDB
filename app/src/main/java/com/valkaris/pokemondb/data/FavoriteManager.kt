package com.valkaris.pokemondb.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Estensione per accedere al DataStore
private val Context.dataStore by preferencesDataStore(name = "favorites")

class FavoriteManager(private val context: Context) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_pokemon")

    // Flusso dei preferiti salvati
    val favorites: Flow<Set<String>> = context.dataStore.data
        .map { preferences -> preferences[FAVORITES_KEY] ?: emptySet() }

    // Aggiunge o rimuove il Pokémon dai preferiti
    suspend fun toggleFavorite(pokemonName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY] ?: emptySet()
            preferences[FAVORITES_KEY] = if (pokemonName in current)
                current - pokemonName
            else
                current + pokemonName
        }
    }
}