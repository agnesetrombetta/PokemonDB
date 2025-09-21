package com.valkaris.pokemondb.views

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.valkaris.pokemondb.R
import com.valkaris.pokemondb.model.*
import com.valkaris.pokemondb.ui.theme.PokemonDBTextStyles

// Pokemon List
@Composable
fun PokemonList(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as Application
    val factory = remember { PokemonViewModelFactory(application) }
    val viewModel: PokemonViewModel = viewModel(factory = factory)

    val filter = rememberSaveable { mutableStateOf("") }

    val pokemons by viewModel.filteredPokemons.observeAsState(emptyList())
    val detailsMap by viewModel.detailsMap.observeAsState(emptyMap())
    val expandedId by viewModel.expandedPokemonId.observeAsState()
    val favorites by viewModel.favorites.observeAsState(setOf())
    val showOnlyFavorites = viewModel.showOnlyFavorites.observeAsState(false)
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {

        // Search Bar
        OutlinedTextField(
            value = filter.value,
            onValueChange = {
                filter.value = it
                viewModel.setSearchQuery(it)
            },
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
            }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.clickable {
                        viewModel.setSearchQuery(filter.value)
                    },
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Show only favorites toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                "Show only favorites",
                color = MaterialTheme.colorScheme.onBackground,
                style = PokemonDBTextStyles.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = showOnlyFavorites.value,
                onCheckedChange = { nuovoValore ->
                    viewModel.setShowOnlyFavorites(nuovoValore)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Show only favorites filter
        val displayList = if (showOnlyFavorites.value) {
            pokemons.filter { favorites.contains(it.name) }
        } else {
            pokemons
        }

        // List of Pokemons
        LazyColumn {
            items(displayList) { pokemon ->
                val isExpanded = pokemon.name == expandedId
                val details = detailsMap[pokemon.name]
                val isFavorite = favorites.contains(pokemon.name)

                PokemonItem(
                    pokemon = pokemon,
                    details = details,
                    isExpanded = isExpanded,
                    isFavorite = isFavorite,
                    onFavoriteToggle = { name -> viewModel.toggleFavorite(name) },
                    onClick = { viewModel.togglePokemonExpansion(pokemon.name) }
                )
            }
        }
    }
}

// Pokemon Item
@Composable
fun PokemonItem(
    pokemon: SmallPokemon,
    details: LargePokemon?,
    isExpanded: Boolean,
    isFavorite: Boolean,
    onClick: (name: String) -> Unit,
    onFavoriteToggle: (name: String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick(pokemon.name) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val imageUrl = if (isExpanded && details != null) {
                        details.sprites.front_default
                    } else {
                        pokemon.imageUrl()
                    }
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = pokemon.name,
                        modifier = Modifier.size(70.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercaseChar() },
                        style = PokemonDBTextStyles.bodyCard,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    painter = painterResource(
                        id = if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                    ),
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onFavoriteToggle(pokemon.name) }
                )
            }

            if (isExpanded) {
                if (details != null) {
                    val rawFlavorText = details.flavor_text_entries
                        ?.find { it.language.name == "en" }
                        ?.flavor_text

                    val cleanedFlavorText = rawFlavorText
                        ?.replace("-", " ")
                        ?.replace("\n", " ")
                        ?.replace("\r", " ")
                        ?.replace("\t", " ")
                        ?.replace("\\f", " ")
                        ?.replace(Regex("[\\p{Cntrl}&&[^\\n\\r\\t]]"), "")
                        ?.trim()

                    // Description
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        // "Description:" in titleLarge
                        Text(
                            text = "Description:",
                            style = PokemonDBTextStyles.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Description text
                        Text(
                            text = cleanedFlavorText ?: "Description not available",
                            style = PokemonDBTextStyles.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            softWrap = true
                        )
                    }

                    // Abilities title
                    Text(
                        text = "Abilities:",
                        style = PokemonDBTextStyles.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Abilities list
                    details.abilities?.forEach { abilityEntry ->
                        abilityEntry?.ability?.name?.let { name ->
                            Text(
                                text = "• " + name.replaceFirstChar { it.uppercaseChar() },
                                style = PokemonDBTextStyles.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Text(
                        "Loading details…",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }
    }
}

// Splash Screen
@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFCB05)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.pokeball),
            contentDescription = "logo",
            modifier = Modifier.fillMaxWidth(0.8f),
            contentScale = ContentScale.FillWidth
        )
    }
}