package com.valkaris.pokemondb.data

val favorites = mutableSetOf<String>()

fun toggleFavorite(name: String) {
    if (favorites.contains(name)) {
        favorites.remove(name)
    } else {
        favorites.add(name)
    }
}