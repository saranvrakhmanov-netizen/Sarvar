package ru.himlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.himlab.app.AppState
import ru.himlab.app.FACTS
import ru.himlab.app.Fact

@Composable
fun FactsScreen(state: AppState, onBack: () -> Unit) {
    var openFact by remember { mutableStateOf<Fact?>(null) }

    ScreenScaffold("Интересные факты", state.stars, onBack) {
        Text(
            "Нажми на карточку, чтобы узнать факт. За каждую новую карточку — звёздочка! Прочитано: ${state.factsRead.size} из ${FACTS.size}",
            fontSize = 22.sp, color = Palette.Soft, lineHeight = 30.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(FACTS) { f ->
                Column(
                    Modifier
                        .heightIn(min = 190.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(f.color)
                        .clickable {
                            openFact = f
                            state.markFactRead(f.id)
                        }
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(f.emoji, fontSize = 56.sp)
                    Text(f.title, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                    Text(
                        if (f.id in state.factsRead) "✓ прочитано" else "Нажми, чтобы узнать",
                        fontSize = 19.sp, color = Palette.Ink.copy(alpha = 0.75f),
                    )
                }
            }
        }
    }

    openFact?.let { f ->
        AlertDialog(
            onDismissRequest = { openFact = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(32.dp),
            title = { Text("${f.emoji} ${f.title}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink) },
            text = { Text(f.text, fontSize = 24.sp, lineHeight = 34.sp, color = Palette.Ink) },
            confirmButton = { BigButton("Понятно!", { openFact = null }) },
        )
    }
}
