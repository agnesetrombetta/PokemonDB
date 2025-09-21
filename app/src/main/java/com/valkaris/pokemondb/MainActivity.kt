package com.valkaris.pokemondb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valkaris.pokemondb.ui.theme.PokemonDBTextStyles
import com.valkaris.pokemondb.ui.theme.PokemonDBTheme
import com.valkaris.pokemondb.views.PokemonList
import com.valkaris.pokemondb.views.SplashScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val splash = rememberSaveable { mutableStateOf(true) }

            PokemonDBTheme {
                LaunchedEffect(Unit) {
                    delay(2500)
                    splash.value = false
                }

                if (splash.value) {
                    SplashScreen()
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            Column {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    text = "Pokémon DB",
                                    textAlign = TextAlign.Center,
                                    color= MaterialTheme.colorScheme.primary,
                                    style = PokemonDBTextStyles.titleExtraHuge
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Gotta data 'em all!",
                                    textAlign = TextAlign.Center,
                                    color= MaterialTheme.colorScheme.primary,
                                    style = PokemonDBTextStyles.titleHuge
                                )
                            }
                        }
                    ) { innerPadding ->
                        PokemonList(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}