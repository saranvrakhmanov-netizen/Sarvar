package ru.himlab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.himlab.app.ui.App
import ru.himlab.app.ui.HimLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = AppState(this)
        setContent {
            HimLabTheme {
                App(state)
            }
        }
    }
}
