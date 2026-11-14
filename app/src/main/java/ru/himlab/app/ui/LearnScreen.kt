package ru.himlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.himlab.app.AppState
import ru.himlab.app.LESSON_PAGES
import ru.himlab.app.QUIZZES

@Composable
fun LearnScreen(state: AppState, onQuiz: (String) -> Unit, onBack: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val p = LESSON_PAGES[page]

    ScreenScaffold("Азбука химии", state.stars, onBack) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            InfoCard(Modifier.fillMaxWidth().widthIn(max = 1000.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(p.emoji, fontSize = 72.sp)
                    Text(p.title, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                }
                Text(p.text, fontSize = 26.sp, lineHeight = 38.sp, color = Palette.Ink)
            }

            Row(
                Modifier.widthIn(max = 1000.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BigButton("← Раньше", { if (page > 0) page-- }, Modifier.weight(1f), Palette.Soft, enabled = page > 0)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    LESSON_PAGES.indices.forEach { i ->
                        Box(
                            Modifier
                                .size(if (i == page) 18.dp else 12.dp)
                                .clip(CircleShape)
                                .background(if (i == page) Palette.Purple else Color(0xFFC8D1EA)),
                        )
                    }
                }
                BigButton("Дальше →", { if (page < LESSON_PAGES.lastIndex) page++ }, Modifier.weight(1f), Palette.Purple, enabled = page < LESSON_PAGES.lastIndex)
            }

            Text(
                "Проверь себя!", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink,
                modifier = Modifier.padding(top = 10.dp),
            )
            QUIZZES.forEach { q ->
                val best = state.quizBest[q.id] ?: 0
                InfoCard(Modifier.fillMaxWidth().widthIn(max = 1000.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(q.emoji, fontSize = 48.sp)
                        Column(Modifier.weight(1f)) {
                            Text(q.title, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                            Text(q.subtitle, fontSize = 20.sp, color = Palette.Soft)
                            Text("Лучший результат: $best из ${q.questions.size}", fontSize = 20.sp, color = Palette.Orange, fontWeight = FontWeight.Bold)
                        }
                        BigButton("Играть", { onQuiz(q.id) }, color = Palette.Green)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
