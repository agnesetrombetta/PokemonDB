package com.valkaris.pokemondb.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension to access the DataStore
private val Context.dataStore by preferencesDataStore(name = "favorites")

class FavoriteManager(private val context: Context) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_pokemon")

    // Saved Favorites Stream
    val favorites: Flow<Set<String>> = context.dataStore.data
        .map { preferences -> preferences[FAVORITES_KEY] ?: emptySet() }

    // Adds or removes the Pokémon from your favorites
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