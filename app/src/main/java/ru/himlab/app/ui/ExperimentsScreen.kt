package ru.himlab.app.ui

import androidx.compose.foundation.background
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
import ru.himlab.app.EXPERIMENTS
import ru.himlab.app.Experiment
import ru.himlab.app.substanceById

@Composable
fun ExperimentsScreen(state: AppState, onOpen: (String) -> Unit, onBack: () -> Unit) {
    ScreenScaffold("Опыты-задания", state.stars, onBack) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HelperRow(
                Mood.HAPPY,
                "Выбери опыт. Я подскажу, что смешать, а потом задам вопрос. Чем меньше подсказок, тем больше звёзд!",
            )
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val cols = if (maxWidth >= 900.dp) 2 else 1
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    EXPERIMENTS.chunked(cols).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowItems.forEach { e ->
                                ExperimentCard(e, state.experimentStars[e.id] ?: 0, Modifier.weight(1f)) { onOpen(e.id) }
                            }
                            repeat(cols - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ExperimentCard(e: Experiment, stars: Int, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(e.emoji, fontSize = 52.sp)
            Column(Modifier.weight(1f)) {
                Text(e.title, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                Text(
                    "${substanceById(e.a).emoji} ${substanceById(e.a).name} + ${substanceById(e.b).emoji} ${substanceById(e.b).name}",
                    fontSize = 20.sp, color = Palette.Soft,
                )
            }
        }
        Text(e.goal, fontSize = 22.sp, lineHeight = 30.sp, color = Palette.Ink)
        Text(
            if (stars > 0) "⭐".repeat(stars) + "☆".repeat(3 - stars) + "  пройдено" else "☆☆☆  ещё не пройдено",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Palette.Orange,
        )
        BigButton(if (stars > 0) "Пройти ещё раз" else "Начать опыт", onClick, Modifier.fillMaxWidth(), Palette.Mint)
    }
}
