package ru.himlab.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.himlab.app.AppState

private fun parentOf(route: String): String = when {
    route.startsWith("quiz:") -> "learn"
    route.startsWith("lab:") -> "experiments"
    else -> "home"
}

@Composable
fun App(state: AppState) {
    var route by rememberSaveable { mutableStateOf("home") }

    BackHandler(enabled = route != "home") { route = parentOf(route) }

    key(route) {
        when {
            route == "lab" -> LabScreen(state, null) { route = "home" }
            route.startsWith("lab:") -> LabScreen(state, route.removePrefix("lab:")) { route = "experiments" }
            route == "experiments" -> ExperimentsScreen(state, onOpen = { route = "lab:$it" }, onBack = { route = "home" })
            route == "learn" -> LearnScreen(state, onQuiz = { route = "quiz:$it" }, onBack = { route = "home" })
            route.startsWith("quiz:") -> QuizScreen(state, route.removePrefix("quiz:")) { route = "learn" }
            route == "facts" -> FactsScreen(state) { route = "home" }
            route == "achievements" -> AchievementsScreen(state) { route = "home" }
            route == "adults" -> AdultsScreen(state) { route = "home" }
            else -> HomeScreen(state) { route = it }
        }
    }

    // Новое достижение: радостное окно
    state.pending.firstOrNull()?.let { a ->
        AlertDialog(
            onDismissRequest = { state.dismissAchievement() },
            containerColor = Color.White,
            shape = RoundedCornerShape(32.dp),
            title = { Text("🎉 Новое достижение!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink) },
            text = {
                Text("${a.emoji} ${a.title}\n${a.description}", fontSize = 26.sp, lineHeight = 36.sp, color = Palette.Ink)
            },
            confirmButton = { BigButton("Ура!", { state.dismissAchievement() }, color = Palette.Green) },
        )
    }
}
