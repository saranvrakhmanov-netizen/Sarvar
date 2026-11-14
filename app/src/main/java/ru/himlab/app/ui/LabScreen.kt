package ru.himlab.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.himlab.app.*

private enum class Phase { PICK, MIXING, DONE }

/**
 * Виртуальная лаборатория. Если задан missionId — это опыт-задание:
 * нужно смешать конкретную пару и ответить на вопрос.
 */
@Composable
fun LabScreen(state: AppState, missionId: String?, onBack: () -> Unit) {
    val mission: Experiment? = missionId?.let { experimentById(it) }

    var chosen by remember { mutableStateOf(listOf<String>()) }
    var phase by remember { mutableStateOf(Phase.PICK) }
    var hint by remember { mutableStateOf<Pair<String, String>?>(null) }
    var hintUsed by remember { mutableStateOf(false) }
    var wasNew by remember { mutableStateOf(false) }

    var wrongOptions by remember { mutableStateOf(setOf<Int>()) }
    var answered by remember { mutableStateOf(false) }
    var earnedStars by remember { mutableStateOf(0) }

    LaunchedEffect(phase) {
        if (phase == Phase.MIXING && chosen.size == 2) {
            delay(1400)
            wasNew = !state.isDiscovered(chosen[0], chosen[1])
            state.recordMix(chosen[0], chosen[1])
            phase = Phase.DONE
        }
    }

    val a: Substance? = chosen.getOrNull(0)?.let { substanceById(it) }
    val b: Substance? = chosen.getOrNull(1)?.let { substanceById(it) }
    val outcome: Outcome? = if (a != null && b != null) outcomeFor(a.id, b.id) else null
    val isMissionPair = mission != null && a != null && b != null &&
        pairKey(a.id, b.id) == pairKey(mission.a, mission.b)

    val beakerSpec: BeakerSpec =
        if (phase == Phase.DONE && a != null && b != null && outcome != null) {
            resultSpec(a, b, outcome.type)
        } else {
            previewSpec(chosen.map { substanceById(it) })
        }

    val (mood, helperText) = when {
        phase == Phase.MIXING -> Mood.THINK to "Мешаем… Что же получится?"
        phase == Phase.DONE && outcome != null -> {
            val m = when (outcome.type) {
                OutcomeType.MIX -> Mood.HAPPY
                OutcomeType.LAYERS -> Mood.WOW
                OutcomeType.REACTION -> Mood.CHEER
                OutcomeType.DRY -> Mood.THINK
            }
            m to outcome.helper
        }
        chosen.isEmpty() -> Mood.HAPPY to (mission?.let { "Задание: ${it.goal}" }
            ?: "Выбери на полке два вещества, и посмотрим, что будет!")
        chosen.size == 1 -> Mood.THINK to "Хороший выбор! Теперь выбери второе вещество."
        else -> Mood.HAPPY to "Готово! Нажимай «Смешать!»"
    }

    fun resetAnswer() {
        answered = false
        wrongOptions = emptySet()
    }

    fun onTile(id: String) {
        if (phase == Phase.MIXING) return
        hint = null
        if (phase == Phase.DONE) {
            chosen = listOf(id)
            phase = Phase.PICK
            resetAnswer()
            return
        }
        chosen = when {
            id in chosen -> chosen - id
            chosen.size >= 2 -> listOf(id)
            else -> chosen + id
        }
    }

    fun computeHint(): Pair<String, String>? {
        if (mission != null) return mission.a to mission.b
        val key = ALL_PAIR_KEYS.firstOrNull { it !in state.discovered } ?: return null
        val parts = key.split("+")
        return parts[0] to parts[1]
    }

    fun pickAnswer(m: Experiment, i: Int) {
        if (answered || i in wrongOptions) return
        if (i == m.correct) {
            var s = 3
            if (hintUsed) s -= 1
            if (wrongOptions.isNotEmpty()) s -= 1
            s = s.coerceAtLeast(1)
            earnedStars = s
            answered = true
            state.completeExperiment(m.id, s)
        } else {
            wrongOptions = wrongOptions + i
        }
    }

    val title = if (mission != null) "Опыт: ${mission.title}" else "Лаборатория"

    ScreenScaffold(title, state.stars, onBack) {
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val wide = maxWidth >= 900.dp

            val helperSection: @Composable () -> Unit = {
                HelperRow(mood, helperText)
            }

            val missionSection: @Composable () -> Unit = {
                if (mission != null) {
                    InfoCard(Modifier.fillMaxWidth(), background = Color(0xFFFFF4D6)) {
                        Text("🎯 Задание: ${mission.goal}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Palette.Ink)
                        Text(
                            "Смешай: ${substanceById(mission.a).emoji} ${substanceById(mission.a).name} " +
                                "и ${substanceById(mission.b).emoji} ${substanceById(mission.b).name}",
                            fontSize = 22.sp, color = Palette.Ink,
                        )
                        Text("👨‍👩‍👧 Для взрослых: ${mission.adultNote}", fontSize = 18.sp, color = Palette.Soft, lineHeight = 25.sp)
                    }
                }
            }

            val shelfSection: @Composable () -> Unit = {
                val cols = if (wide) 3 else 4
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Полка с веществами", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Palette.Ink)
                    SUBSTANCES.chunked(cols).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowItems.forEach { s ->
                                SubstanceTile(
                                    s = s,
                                    selected = s.id in chosen,
                                    hinted = hint?.let { s.id == it.first || s.id == it.second } == true,
                                    onClick = { onTile(s.id) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(cols - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }

            val controlsSection: @Composable () -> Unit = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigButton(
                        "🧪 Смешать!",
                        onClick = { phase = Phase.MIXING },
                        modifier = Modifier.weight(1.3f),
                        color = Palette.Green,
                        enabled = chosen.size == 2 && phase == Phase.PICK,
                    )
                    BigButton(
                        "💡 Подсказка",
                        onClick = { hint = computeHint(); hintUsed = true },
                        modifier = Modifier.weight(1f),
                        color = Palette.Orange,
                        enabled = phase != Phase.MIXING,
                    )
                    BigButton(
                        "Заново",
                        onClick = { chosen = emptyList(); phase = Phase.PICK; hint = null; resetAnswer() },
                        modifier = Modifier.weight(0.8f),
                        color = Palette.Soft,
                        enabled = chosen.isNotEmpty() && phase != Phase.MIXING,
                    )
                }
            }

            val beakerSection: @Composable () -> Unit = {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SlotChip(a)
                        Text("+", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Palette.Purple)
                        SlotChip(b)
                    }
                    Beaker(beakerSpec, Modifier.size(260.dp, 300.dp), shaking = phase == Phase.MIXING)
                }
            }

            val resultSection: @Composable () -> Unit = {
                if (phase == Phase.DONE && outcome != null && a != null && b != null) {
                    InfoCard(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            TypeChip(outcome.type)
                            Text(outcome.title, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink, modifier = Modifier.weight(1f))
                        }
                        if (wasNew) {
                            Text("🎉 Новое открытие! +1 ⭐", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Palette.Orange)
                        }
                        Text("Что произошло", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Palette.Purple)
                        Text(outcome.what, fontSize = 22.sp, lineHeight = 30.sp, color = Palette.Ink)
                        Text("Почему так?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Palette.Purple)
                        Text(outcome.why, fontSize = 22.sp, lineHeight = 30.sp, color = Palette.Ink)
                        Text(
                            if (outcome.type == OutcomeType.REACTION) "🆕 Появились новые вещества — это химическая реакция!"
                            else "Новых веществ не появилось — это не реакция.",
                            fontSize = 21.sp, fontWeight = FontWeight.Bold,
                            color = if (outcome.type == OutcomeType.REACTION) Palette.Orange else Palette.Soft,
                        )
                    }
                    if (mission != null) {
                        Spacer(Modifier.height(14.dp))
                        if (!isMissionPair) {
                            InfoCard(Modifier.fillMaxWidth(), background = Color(0xFFFFF4D6)) {
                                Text(
                                    "Интересный результат! Но в задании нужно смешать ${substanceById(mission.a).name.lowercase()} " +
                                        "и ${substanceById(mission.b).name.lowercase()}. Попробуй ещё!",
                                    fontSize = 22.sp, color = Palette.Ink, lineHeight = 30.sp,
                                )
                            }
                        } else {
                            MissionQuiz(mission, wrongOptions, answered, earnedStars, onPick = { pickAnswer(mission, it) }, onFinish = onBack)
                        }
                    }
                }
            }

            val safetySection: @Composable () -> Unit = {
                Text(
                    "🔒 $SAFETY_LINE",
                    fontSize = 17.sp, lineHeight = 24.sp, color = Palette.Soft,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
            }

            if (wide) {
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    Column(
                        Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        helperSection()
                        missionSection()
                        shelfSection()
                        controlsSection()
                        safetySection()
                    }
                    Column(
                        Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        beakerSection()
                        resultSection()
                        Spacer(Modifier.height(24.dp))
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    helperSection()
                    missionSection()
                    shelfSection()
                    controlsSection()
                    beakerSection()
                    resultSection()
                    safetySection()
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SlotChip(s: Substance?) {
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (s == null) Color(0xFFEAEFFA) else Color.White)
            .border(BorderStroke(3.dp, if (s == null) Color(0xFFC8D1EA) else s.color), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (s == null) "?" else "${s.emoji} ${s.name}",
            fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Palette.Ink, textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MissionQuiz(
    m: Experiment,
    wrong: Set<Int>,
    answered: Boolean,
    stars: Int,
    onPick: (Int) -> Unit,
    onFinish: () -> Unit,
) {
    InfoCard(Modifier.fillMaxWidth(), background = Color(0xFFEFEAFF)) {
        Text("❓ ${m.question}", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Palette.Ink, lineHeight = 33.sp)
        m.options.forEachIndexed { i, text ->
            val color = when {
                answered && i == m.correct -> Palette.Green
                i in wrong -> Palette.Coral
                else -> Palette.Purple
            }
            BigButton(text, onClick = { onPick(i) }, modifier = Modifier.fillMaxWidth(), color = color)
        }
        if (wrong.isNotEmpty() && !answered) {
            Text("Почти! Подумай ещё и выбери другой ответ.", fontSize = 21.sp, color = Palette.Soft)
        }
        if (answered) {
            Text(m.explain, fontSize = 22.sp, lineHeight = 30.sp, color = Palette.Ink)
            Text(
                "Опыт пройден! Ты получил " + "⭐".repeat(stars) + "  (" + stars + " из 3)",
                fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Orange,
            )
            BigButton("К списку опытов", onFinish, Modifier.fillMaxWidth(), Palette.Green)
        }
    }
}
