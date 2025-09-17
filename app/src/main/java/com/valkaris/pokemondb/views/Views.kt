package com.valkaris.pokemondb.views

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.valkaris.pokemondb.R
import com.valkaris.pokemondb.model.PokemonViewModel
import com.valkaris.pokemondb.model.PokemonViewModelFactory
import com.valkaris.pokemondb.model.LargePokemon
import com.valkaris.pokemondb.model.SmallPokemon
import com.valkaris.pokemondb.ui.theme.PokemonDBTextStyles
import com.valkaris.pokemondb.model.imageUrl
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import com.valkaris.pokemondb.data.favorites
import com.valkaris.pokemondb.data.toggleFavorite
import androidx.compose.material3.Switch


@Composable
fun PokemonList(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as Application

    val factory = remember { PokemonViewModelFactory(application) }
    val filter = rememberSaveable { mutableStateOf("") }
    val viewModel: PokemonViewModel = viewModel(factory = factory)
    val pokemons by viewModel.filteredPokemons.observeAsState(emptyList())
    val detailsMap by viewModel.detailsMap.observeAsState(emptyMap())
    val expandedId by viewModel.expandedPokemonId.observeAsState()
    val favorites by viewModel.favorites.observeAsState(setOf())
    val showOnlyFavorites = viewModel.showOnlyFavorites.observeAsState(false)
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        // 🔍 Barra di ricerca
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
                    }
                )
            }
        )

        // ⭐ Toggle per mostrare solo preferiti
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Show only favorites")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked =  showOnlyFavorites.value,
                onCheckedChange = { nuovoValore ->
                    viewModel.setShowOnlyFavorites(nuovoValore) }
            )
        }

        // 📂 Filtro della lista da mostrare
        val displayList = if (showOnlyFavorites.value) {
            pokemons.filter { favorites.contains(it.name) }
        } else {
            pokemons
        }

        // 📜 Lista dei Pokémon (filtrata)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
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
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercaseChar() },
                        style = PokemonDBTextStyles.titleHuge
                    )
                }

                Icon(
                    painter = painterResource(
                        id = if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                    ),
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onFavoriteToggle(pokemon.name)
                        }
                )
            }

            if (isExpanded) {
                if (details != null) {
                    details.abilities?.forEach { abilityEntry ->
                        abilityEntry?.ability?.name?.let { name ->
                            Text(name.replaceFirstChar { it.uppercaseChar() })
                        }
                    }
                    val rawFlavorText = details.flavor_text_entries?.find { it.language.name == "en" }?.flavor_text
                    val cleanedFlavorText = rawFlavorText
                        ?.replace("\n", " ") // Replace newlines with spaces
                        ?.replace("\r", " ") // Replace carriage returns with spaces (often found with \n)
                        ?.replace("\t", " ") // Replace tabs with spaces
                        ?.replace("\\f", " ") // Replace form feeds with spaces (this is the key change to ensure \f is treated literally if that was the intent, or handled correctly)
                        ?.replace(Regex("[\\p{Cntrl}&&[^\\n\\r\\t]]"), "") // Remove other control characters except \n, \r, \t (which we handled)
                        ?.trim()
                    Text(
                        text = cleanedFlavorText?.let { "Description: $it" } ?: "No description available",
                        style = PokemonDBTextStyles.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Text("Loading details…", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun HorizontalDetails(details: LargePokemon) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text(text = "ID: ${details.id}", style = PokemonDBTextStyles.bodyMedium)
        Text(text = "Height: ${details.height}", style = PokemonDBTextStyles.bodyMedium)
        Text(text = "Weight: ${details.weight}", style = PokemonDBTextStyles.bodyMedium)
    }
}

@Composable
fun Title(text: String) {
    Text(
        color = Color.Black,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = text,
        style = PokemonDBTextStyles.titleLarge
    )
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
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
