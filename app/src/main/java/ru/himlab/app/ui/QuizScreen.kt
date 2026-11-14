package ru.himlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ru.himlab.app.AppState
import ru.himlab.app.quizById

@Composable
fun QuizScreen(state: AppState, quizId: String, onBack: () -> Unit) {
    val quiz = quizById(quizId)
    if (quiz == null) {
        ScreenScaffold("Викторина", state.stars, onBack) {
            Text("Эта викторина не найдена.", fontSize = 24.sp, modifier = Modifier.padding(24.dp))
        }
        return
    }

    var index by remember(quizId) { mutableStateOf(0) }
    var selected by remember(quizId) { mutableStateOf<Int?>(null) }
    var correct by remember(quizId) { mutableStateOf(0) }
    var finished by remember(quizId) { mutableStateOf(false) }

    val total = quiz.questions.size

    ScreenScaffold(quiz.title, state.stars, onBack) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (finished) {
                val good = correct * 4 >= total * 3
                HelperRow(
                    if (good) Mood.CHEER else Mood.HAPPY,
                    if (good) "Отлично! Ты настоящий химик-исследователь!" else "Хорошая попытка! Попробуй ещё раз — у тебя получится лучше.",
                    Modifier.widthIn(max = 1000.dp),
                )
                InfoCard(Modifier.fillMaxWidth().widthIn(max = 1000.dp)) {
                    Text("Результат: $correct из $total", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Purple)
                    Text(
                        "Звёзды за викторину считаются по лучшему результату: сейчас это ${state.quizBest[quizId] ?: 0} ⭐",
                        fontSize = 22.sp, lineHeight = 30.sp, color = Palette.Ink,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        BigButton("Ещё раз", {
                            index = 0; selected = null; correct = 0; finished = false
                        }, Modifier.weight(1f), Palette.Green)
                        BigButton("Готово", onBack, Modifier.weight(1f), Palette.Purple)
                    }
                }
            } else {
                val q = quiz.questions[index]
                val sel = selected
                Text("Вопрос ${index + 1} из $total", fontSize = 22.sp, color = Palette.Soft, fontWeight = FontWeight.Bold)
                InfoCard(Modifier.fillMaxWidth().widthIn(max = 1000.dp)) {
                    Text(q.text, fontSize = 32.sp, lineHeight = 42.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                }
                q.options.forEachIndexed { i, text ->
                    val bg = when {
                        sel == null -> Color.White
                        i == q.correct -> Color(0xFFB8F0CB)
                        i == sel -> Color(0xFFFFC9C9)
                        else -> Color(0xFFF1F3F9)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .widthIn(max = 1000.dp)
                            .heightIn(min = 84.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(bg)
                            .clickable(enabled = sel == null) {
                                selected = i
                                if (i == q.correct) correct++
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(text, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Palette.Ink)
                    }
                }
                if (sel != null) {
                    val right = sel == q.correct
                    HelperRow(
                        if (right) Mood.CHEER else Mood.THINK,
                        (if (right) "Верно! " else "Не совсем. ") + q.explain,
                        Modifier.widthIn(max = 1000.dp),
                    )
                    BigButton(
                        if (index == total - 1) "Узнать результат" else "Дальше →",
                        {
                            if (index == total - 1) {
                                state.recordQuiz(quizId, correct)
                                finished = true
                            } else {
                                index++
                                selected = null
                            }
                        },
                        Modifier.widthIn(max = 1000.dp).fillMaxWidth(),
                        Palette.Purple,
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
