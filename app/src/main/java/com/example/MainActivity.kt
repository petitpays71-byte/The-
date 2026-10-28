package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.NuevaAiService
import com.example.data.NuevaRepository
import com.example.ui.NuevaMainScreen
import com.example.ui.theme.MidnightBase
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val repository = NuevaRepository()
    private val aiService = NuevaAiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MidnightBase
                ) {
                    NuevaMainScreen(
                        repository = repository,
                        aiService = aiService
                    )
                }
            }
        }
    }
}
