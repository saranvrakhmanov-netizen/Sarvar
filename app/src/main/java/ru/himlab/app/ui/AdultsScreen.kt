package ru.himlab.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.himlab.app.AppState

@Composable
fun AdultsScreen(state: AppState, onBack: () -> Unit) {
    var confirmReset by remember { mutableStateOf(false) }

    ScreenScaffold("Для взрослых", state.stars, onBack) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Section(
                "О приложении",
                "«ХимЛаб» работает без интернета. В приложении нет рекламы, встроенных покупок, ссылок наружу и регистрации. " +
                    "У приложения нет разрешения на доступ в интернет, личные данные не собираются и никуда не отправляются. " +
                    "Прогресс (звёзды, открытия, награды) хранится только на этом устройстве и удаляется вместе с приложением.",
            )
            Section(
                "О безопасности",
                "Все смешивания в игре виртуальные. Приложение не призывает повторять опыты и не называет бытовые вещества полностью безопасными: " +
                    "даже уксус, сода, соль и пищевой краситель могут раздражать глаза, кожу и дыхательные пути, а пена от соды и уксуса может выплеснуться.\n\n" +
                    "Если ребёнок захочет повторить опыт в жизни, решение за вами. Тогда: проводите опыт вместе, на подносе, в проветриваемом месте, " +
                    "в защитных очках; ничего не пробуйте на вкус; держите смеси подальше от младших детей и животных; не смешивайте другие бытовые вещества " +
                    "и тем более бытовую химию (чистящие средства, отбеливатели и т. п.).",
            )
            Section(
                "О научной точности",
                "Для возраста 9 лет объяснения упрощены. Химический элемент здесь объясняется как «вид атомов», вещество — как то, что из них построено, " +
                    "смесь — как несколько веществ вместе без образования новых. Растворение соли, сахара и соды в воде показано как смешивание (физический процесс), " +
                    "хотя у каждого вещества есть предел растворимости. Пищевой краситель в игре — водный; жирорастворимые красители ведут себя иначе. " +
                    "Реакция соды с уксусной кислотой даёт ацетат натрия, воду и углекислый газ; на этом уровне ребёнку говорится о «новых веществах» и пузырьках газа.",
            )

            InfoCard(Modifier.fillMaxWidth()) {
                Text("Сбросить прогресс", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
                Text("Удалит все звёзды, открытия, пройденные опыты и награды на этом устройстве.", fontSize = 20.sp, color = Palette.Soft)
                BigButton("Сбросить всё", { confirmReset = true }, color = Palette.Coral)
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(28.dp),
            title = { Text("Сбросить прогресс?", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Это действие нельзя отменить.", fontSize = 22.sp) },
            confirmButton = {
                BigButton("Да, сбросить", { state.resetAll(); confirmReset = false }, color = Palette.Coral)
            },
            dismissButton = {
                BigButton("Отмена", { confirmReset = false }, color = Palette.Soft)
            },
        )
    }
}

@Composable
private fun Section(title: String, text: String) {
    InfoCard(Modifier.fillMaxWidth()) {
        Text(title, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink)
        Text(text, fontSize = 21.sp, lineHeight = 30.sp, color = Palette.Ink)
    }
}
