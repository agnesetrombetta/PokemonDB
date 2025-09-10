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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.valkaris.pokemondb.model.PokemonViewModel.model.LargePokemon
import com.valkaris.pokemondb.model.SmallPokemon
import com.valkaris.pokemondb.ui.theme.DrinkMeTextStyles
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun PokemonList(
    modifier: Modifier = Modifier,
    application: Application = LocalContext.current.applicationContext as Application
) {
    val factory = remember { PokemonViewModelFactory(application) }
    val filter = rememberSaveable { mutableStateOf("") }
    val lastId = rememberSaveable { mutableIntStateOf(-1) }
    val viewModel: PokemonViewModel = viewModel(factory = factory)
    val cocktails by viewModel.Pokemons.observeAsState(emptyList())
    val drinks by viewModel.Pokemons.observeAsState(emptyList())

    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = filter.value, onValueChange = {
                filter.value = it
            },
            keyboardActions = KeyboardActions (onSearch={
                focusManager.clearFocus()
                viewModel.loadPokemons(filter.value)
            }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search),
            maxLines = 1,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            trailingIcon = {
                Icon(
                    Icons.Default.Search, contentDescription = "Search",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            viewModel.loadPokemons(filter.value)
                        })
            }
        )
        LazyColumn {
            if (Pokemons.isEmpty()) {
                item {
                    Text(
                        text = "No Pokemons found",
                        style = PokemonDBTextStyles.titleHuge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(Pokemons) { Pokemon ->
                    PokemonItem(Pokemon, drinks, lastId) {
                        if (it == lastId.intValue)
                            lastId.intValue = -1
                        else {
                            viewModel.removePokemon()
                            viewModel.loadPokemons(it)
                            lastId.intValue = it
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonItem(
    Pokemon: SmallPokemon,
    largeCocktails: List<LargePokemon>,
    lastId: MutableState<Int>,
    onClick: (id: Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFEE9)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.clickable {
                    onClick(Pokemon.idPokemon.toInt())
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(Pokemon.strPokemonThumb),
                    contentDescription = Pokemon.strPokemon,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    text = Pokemon.strPokemon,
                    style = DrinkMeTextStyles.titleHuge
                )
            }
            if (lastId.value == Pokemon.idPokemon.toInt()) {
                if (largeCocktails.isNotEmpty()) {
                    ShowInstructions(largeCocktails)
                }
            }
        }
    }
}

@Composable
fun ShowInstructions(largePokemons: List<LargePokemon>) {
    HorizontalInfo(largePokemons)
    Title("Ingredients")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Ingredients(largePokemons)
        Measures(largePokemons)
    }
    Title("Instructions")
    Text(
        color = Color.Black,
        text = largePokemons[0].strInstructions,
        style = PokemonDBTextStyles.bodyLarge
    )
}

@Composable
fun HorizontalInfo(largePokemons: List<LargePokemon>) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        PokemonProperty(painterResource(id = R.drawable.ic_local_Pokemon),
            largePokemons[0].strPokemon)[0].strAlcoholic)
        PokemonProperty(painterResource(id = R.drawable.ic_category),
            largePokemons[0].strCategory)
        PokemonProperty(painterResource(id = R.drawable.ic_local_bar),
            largePokemons[0].strGlass)
    }
}

@Composable
fun PokemonProperty(painterRes: Painter, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterRes,
            contentDescription = "Glass",
            tint = Color.Black
        )
        Text(
            color = Color.Black,
            text = label,
            style = PokemonDBTextStyles.bodyMedium
        )

    }
}

@Composable
fun Ingredients(largeCocktails: List<LargeCocktail>) {
    Column {
        for (ingredient in 1..15) {
            val c = LargeCocktail::class
            val field = c.members.find { it.name == "strIngredient$ingredient" }
            val value = field?.call(largeCocktails[0])
            if (value != null) {
                Text(
                    color = Color.Black,
                    text = "$value", textAlign = TextAlign.End,
                    style = DrinkMeTextStyles.bodyLarge
                )
            }
        }
    }
}

@Composable
fun Measures(largeCocktails: List<LargeCocktail>) {
    Column {
        for (measure in 1..15) {
            val c = LargeCocktail::class
            val field = c.members.find { it.name == "strMeasure$measure" }
            val value = field?.call(largeCocktails[0])
            if (value != null && value != "") {
                Text(
                    color = Color.Black,
                    text = "$value",
                    style = DrinkMeTextStyles.bodyLarge
                )
            }
        }
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
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.LOGO),
            contentDescription = "logo",
            modifier=Modifier.fillMaxWidth(0.8f),
            contentScale = ContentScale.FillWidth)
    }
}