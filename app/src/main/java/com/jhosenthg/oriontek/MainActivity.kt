package com.jhosenthg.oriontek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jhosenthg.oriontek.presentation.screens.clients.ClientScreen
import com.jhosenthg.oriontek.presentation.theme.OrionTekTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrionTekTheme {
                ClientScreen()
            }
        }
    }
}
