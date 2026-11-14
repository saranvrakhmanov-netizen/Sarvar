package ru.himlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.himlab.app.AppState

private data class MenuItem(
    val route: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val color: Color,
)

private val MENU = listOf(
    MenuItem("lab", "🧪", "Лаборатория", "Смешивай вещества сам", Color(0xFF6C4DFF)),
    MenuItem("experiments", "📋", "Опыты-задания", "Открытия шаг за шагом", Color(0xFF14A89A)),
    MenuItem("learn", "📖", "Азбука химии", "Атомы, вещества, смеси", Color(0xFFF26B1D)),
    MenuItem("facts", "💡", "Интересные факты", "Карточки открытий", Color(0xFF2F80ED)),
    MenuItem("achievements", "🏅", "Достижения", "Награды и карта открытий", Color(0xFFE5484D)),
    MenuItem("adults", "👨‍👩‍👧", "Для взрослых", "О приложении и безопасности", Color(0xFF5B607F)),
)

@Composable
fun HomeScreen(state: AppState, onNavigate: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Palette.Sky)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ХимЛаб", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Purple)
                Text("лаборатория открытий", fontSize = 28.sp, color = Palette.Soft)
            }
            StarsChip(state.stars)
        }
        HelperRow(
            Mood.CHEER,
            "Привет! Я Пузырёк, твой помощник в лаборатории. Здесь все опыты виртуальные. Выбирай, с чего начнём!",
            sizeDp = 150.dp,
        )
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cols = if (maxWidth >= 900.dp) 3 else 2
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                MENU.chunked(cols).forEach { rowItems ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        rowItems.forEach { item ->
                            MenuCard(item, Modifier.weight(1f)) { onNavigate(item.route) }
                        }
                        repeat(cols - rowItems.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCard(item: MenuItem, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .heightIn(min = 190.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(item.color)
            .clickable(onClick = onClick)
            .padding(22.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(item.emoji, fontSize = 64.sp)
        Text(item.title, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(top = 6.dp))
        Text(item.subtitle, fontSize = 20.sp, color = Color.White.copy(alpha = 0.92f))
    }
}
