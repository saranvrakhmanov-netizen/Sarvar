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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.himlab.app.ACHIEVEMENTS
import ru.himlab.app.AppState
import ru.himlab.app.OutcomeType
import ru.himlab.app.PAIR_COUNT
import ru.himlab.app.SUBSTANCES
import ru.himlab.app.outcomeTypeByKey
import ru.himlab.app.pairKey

@Composable
fun AchievementsScreen(state: AppState, onBack: () -> Unit) {
    ScreenScaffold("Достижения", state.stars, onBack) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val wide = maxWidth >= 900.dp
                val mapCard: @Composable (Modifier) -> Unit = { m ->
                    InfoCard(m) {
                        Text("🗺️ Карта открытий", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                        Text(
                            "Открыто ${state.discovered.size} из $PAIR_COUNT смешиваний. Цвет клетки показывает, что получилось, когда ты смешал два вещества.",
                            fontSize = 20.sp, lineHeight = 27.sp, color = Palette.Soft,
                        )
                        DiscoveryMap(state)
                        OutcomeType.values().forEach { t ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                TypeChip(t)
                                Text(t.meaning, fontSize = 18.sp, lineHeight = 24.sp, color = Palette.Ink, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                val listCard: @Composable (Modifier) -> Unit = { m ->
                    Column(m, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "🏅 Награды: ${state.unlocked.size} из ${ACHIEVEMENTS.size}",
                            fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink,
                        )
                        ACHIEVEMENTS.forEach { a ->
                            val cur = a.current(state).coerceAtMost(a.goal)
                            val done = a.id in state.unlocked
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (done) Color(0xFFFFF4D6) else Color.White)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Text(a.emoji, fontSize = 44.sp, modifier = Modifier.alpha(if (done) 1f else 0.35f))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(a.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                                    Text(a.description, fontSize = 18.sp, color = Palette.Soft)
                                    ProgressBar(cur.toFloat() / a.goal.toFloat())
                                }
                                Text(
                                    if (done) "✅" else "$cur/${a.goal}",
                                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Palette.Ink,
                                )
                            }
                        }
                    }
                }
                if (wide) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        mapCard(Modifier.weight(1f))
                        listCard(Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        mapCard(Modifier.fillMaxWidth())
                        listCard(Modifier.fillMaxWidth())
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProgressBar(fraction: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFFE3E8F5)),
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(7.dp))
                .background(Palette.Green),
        )
    }
}

@Composable
private fun DiscoveryMap(state: AppState) {
    val cell = 46.dp
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Spacer(Modifier.size(cell))
            SUBSTANCES.forEach { s ->
                Box(Modifier.size(cell), contentAlignment = Alignment.Center) { Text(s.emoji, fontSize = 26.sp) }
            }
        }
        SUBSTANCES.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(cell), contentAlignment = Alignment.Center) { Text(row.emoji, fontSize = 26.sp) }
                SUBSTANCES.forEach { col ->
                    val same = row.id == col.id
                    val key = pairKey(row.id, col.id)
                    val found = !same && key in state.discovered
                    val type = if (found) outcomeTypeByKey(key) else null
                    Box(
                        Modifier
                            .size(cell)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    same -> Color(0xFFF1F3F9)
                                    type != null -> type.color
                                    else -> Color(0xFFDCE3F2)
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        val mark = when (type) {
                            OutcomeType.MIX -> "≈"
                            OutcomeType.LAYERS -> "≡"
                            OutcomeType.REACTION -> "💥"
                            OutcomeType.DRY -> "·"
                            null -> if (same) "" else "?"
                        }
                        Text(mark, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (type == null) Palette.Soft else Color.White)
                    }
                }
            }
        }
    }
}
