package ru.himlab.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.himlab.app.OutcomeType
import ru.himlab.app.Substance
import java.util.Random

// ------------------------------------------------------------------
// Персонаж-помощник «Пузырёк»
// ------------------------------------------------------------------

enum class Mood { HAPPY, THINK, WOW, CHEER }

@Composable
fun Helper(mood: Mood, modifier: Modifier = Modifier, sizeDp: Dp = 140.dp) {
    val t = rememberInfiniteTransition(label = "helper")
    val bob by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob",
    )
    Canvas(modifier.size(sizeDp)) {
        val w = this.size.width
        val h = this.size.height
        translate(top = (bob - 0.5f) * h * 0.05f) {
            // пузырьки над горлышком
            drawCircle(Color.White.copy(alpha = 0.9f), radius = w * 0.035f, center = Offset(w * 0.47f, h * (0.05f + 0.02f * bob)))
            drawCircle(Color.White.copy(alpha = 0.9f), radius = w * 0.025f, center = Offset(w * 0.55f, h * (0.02f + 0.03f * bob)))

            // стекло колбы
            val body = Path().apply {
                moveTo(w * 0.40f, h * 0.10f)
                lineTo(w * 0.60f, h * 0.10f)
                lineTo(w * 0.60f, h * 0.30f)
                lineTo(w * 0.88f, h * 0.80f)
                quadraticBezierTo(w * 0.92f, h * 0.95f, w * 0.78f, h * 0.95f)
                lineTo(w * 0.22f, h * 0.95f)
                quadraticBezierTo(w * 0.08f, h * 0.95f, w * 0.12f, h * 0.80f)
                lineTo(w * 0.40f, h * 0.30f)
                close()
            }
            drawPath(body, Color(0xFFD7F3FF))
            // жидкость
            val liquid = Path().apply {
                moveTo(w * 0.2208f, h * 0.62f)
                lineTo(w * 0.7792f, h * 0.62f)
                lineTo(w * 0.88f, h * 0.80f)
                quadraticBezierTo(w * 0.92f, h * 0.95f, w * 0.78f, h * 0.95f)
                lineTo(w * 0.22f, h * 0.95f)
                quadraticBezierTo(w * 0.08f, h * 0.95f, w * 0.12f, h * 0.80f)
                close()
            }
            drawPath(liquid, Palette.Mint.copy(alpha = 0.9f))
            drawPath(
                body, Palette.PurpleDark,
                style = Stroke(width = w * 0.025f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            // пробка
            drawRoundRect(
                Palette.Sun, topLeft = Offset(w * 0.37f, h * 0.06f),
                size = Size(w * 0.26f, h * 0.06f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f),
            )

            // глаза
            val eyeY = h * 0.69f
            val look = if (mood == Mood.THINK) Offset(w * 0.012f, -h * 0.012f) else Offset.Zero
            for (ex in listOf(0.39f, 0.61f)) {
                drawCircle(Color.White, radius = w * 0.07f, center = Offset(w * ex, eyeY))
                drawCircle(Palette.Ink, radius = w * 0.035f, center = Offset(w * ex, eyeY) + look)
            }
            // щёчки
            drawCircle(Color(0x66FF6B6B), radius = w * 0.04f, center = Offset(w * 0.28f, h * 0.79f))
            drawCircle(Color(0x66FF6B6B), radius = w * 0.04f, center = Offset(w * 0.72f, h * 0.79f))
            // рот
            val my = h * 0.79f
            when (mood) {
                Mood.HAPPY -> drawArc(
                    Palette.Ink, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(w * 0.43f, my - w * 0.07f), size = Size(w * 0.14f, w * 0.14f),
                    style = Stroke(width = w * 0.022f, cap = StrokeCap.Round),
                )
                Mood.THINK -> drawLine(
                    Palette.Ink, Offset(w * 0.45f, my + h * 0.02f), Offset(w * 0.56f, my + h * 0.005f),
                    strokeWidth = w * 0.022f, cap = StrokeCap.Round,
                )
                Mood.WOW -> drawCircle(Color(0xFF7A1F3D), radius = w * 0.04f, center = Offset(w * 0.5f, my + h * 0.02f))
                Mood.CHEER -> drawArc(
                    Color(0xFF7A1F3D), startAngle = 0f, sweepAngle = 180f, useCenter = true,
                    topLeft = Offset(w * 0.42f, my - w * 0.05f), size = Size(w * 0.16f, w * 0.14f),
                )
            }
        }
    }
}

@Composable
fun SpeechBubble(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(3.dp, Palette.Purple.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            .padding(horizontal = 22.dp, vertical = 16.dp),
    ) {
        Text(text, fontSize = 22.sp, lineHeight = 30.sp, color = Palette.Ink)
    }
}

@Composable
fun HelperRow(mood: Mood, text: String, modifier: Modifier = Modifier, sizeDp: Dp = 120.dp) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Helper(mood, sizeDp = sizeDp)
        Box(Modifier.padding(start = 8.dp).weight(1f)) { SpeechBubble(text, Modifier.fillMaxWidth()) }
    }
}

// ------------------------------------------------------------------
// Колба с веществами
// ------------------------------------------------------------------

data class LiquidLayer(val color: Color, val weight: Float)

data class BeakerSpec(
    /** Жидкие слои сверху вниз. */
    val layers: List<LiquidLayer> = emptyList(),
    /** Осадок на дне (растворилось не всё, и оно тяжелее жидкости). */
    val sediment: Color? = null,
    /** Сухие порошки, лежащие горкой. */
    val powder: List<Color> = emptyList(),
    val bubbles: Boolean = false,
    val foam: Boolean = false,
)

/** Что видно в колбе, пока вещества ещё не смешали: по очереди налили/насыпали. */
fun previewSpec(chosen: List<Substance>): BeakerSpec {
    val liquids = chosen.filter { it.isLiquid }
    val powders = chosen.filter { !it.isLiquid }
    return BeakerSpec(
        // первый налитый — внизу, поэтому порядок переворачиваем
        layers = liquids.reversed().map { LiquidLayer(it.color.copy(alpha = 0.92f), 1f) },
        powder = if (liquids.isEmpty()) powders.map { it.color } else emptyList(),
        sediment = if (liquids.isNotEmpty()) powders.firstOrNull()?.color else null,
    )
}

/** Что видно в колбе после смешивания. */
fun resultSpec(a: Substance, b: Substance, type: OutcomeType): BeakerSpec {
    val liquids = listOf(a, b).filter { it.isLiquid }
    val powders = listOf(a, b).filter { !it.isLiquid }
    return when (type) {
        OutcomeType.MIX -> {
            val color = when (liquids.size) {
                2 -> {
                    val (x, y) = liquids
                    lerp(x.color, y.color, y.tint / (x.tint + y.tint))
                }
                else -> liquids.firstOrNull()?.color ?: Color(0xFF7EC8F2)
            }
            BeakerSpec(layers = listOf(LiquidLayer(color.copy(alpha = 0.92f), 1f)))
        }
        OutcomeType.LAYERS -> {
            val layers = liquids.sortedBy { it.density }.map { LiquidLayer(it.color.copy(alpha = 0.92f), 1f) }
            BeakerSpec(layers = layers, sediment = powders.firstOrNull()?.color)
        }
        OutcomeType.REACTION -> BeakerSpec(
            layers = listOf(LiquidLayer(Color(0xFFF2E8C9), 1f)),
            bubbles = true,
            foam = true,
        )
        OutcomeType.DRY -> {
            val dye = listOf(a, b).firstOrNull { it.id == "dye" }
            val colors = if (dye != null) {
                powders.map { lerp(it.color, dye.color, 0.6f) }
            } else powders.map { it.color }
            BeakerSpec(powder = colors.ifEmpty { listOf(Color.White) })
        }
    }
}

@Composable
fun Beaker(spec: BeakerSpec, modifier: Modifier = Modifier, shaking: Boolean = false) {
    val inf = rememberInfiniteTransition(label = "beaker")
    val phase by inf.animateFloat(
        0f, 1f, infiniteRepeatable(tween(2400, easing = LinearEasing)), label = "phase",
    )
    val shake by inf.animateFloat(
        -1f, 1f, infiniteRepeatable(tween(160, easing = LinearEasing), RepeatMode.Reverse), label = "shake",
    )
    // заранее посчитанные случайные «зёрнышки» и пузырьки (чтобы не прыгали при каждой перерисовке)
    val grains = remember {
        val r = Random(11)
        List(220) { floatArrayOf(r.nextFloat(), r.nextFloat(), r.nextFloat()) }
    }
    val bubblePos = remember {
        val r = Random(5)
        List(16) { floatArrayOf(r.nextFloat(), r.nextFloat(), 0.012f + r.nextFloat() * 0.022f, 0.6f + r.nextFloat() * 0.9f) }
    }

    Canvas(
        modifier.graphicsLayer {
            rotationZ = if (shaking) shake * 7f else 0f
            transformOrigin = TransformOrigin(0.5f, 1f)
        },
    ) {
        val w = this.size.width
        val h = this.size.height
        val left = w * 0.14f
        val right = w * 0.86f
        val top = h * 0.05f
        val bottom = h * 0.95f
        val r = w * 0.08f
        val glassW = right - left
        val glassH = bottom - top
        val liquidTop = bottom - glassH * 0.66f

        val closed = Path().apply {
            moveTo(left, top)
            lineTo(left, bottom - r)
            quadraticBezierTo(left, bottom, left + r, bottom)
            lineTo(right - r, bottom)
            quadraticBezierTo(right, bottom, right, bottom - r)
            lineTo(right, top)
            close()
        }
        val open = Path().apply {
            moveTo(left, top)
            lineTo(left, bottom - r)
            quadraticBezierTo(left, bottom, left + r, bottom)
            lineTo(right - r, bottom)
            quadraticBezierTo(right, bottom, right, bottom - r)
            lineTo(right, top)
        }

        // стекло
        drawPath(closed, Color(0x22FFFFFF))

        clipPath(closed) {
            // жидкие слои
            if (spec.layers.isNotEmpty()) {
                val total = spec.layers.sumOf { it.weight.toDouble() }.toFloat()
                var y = liquidTop
                spec.layers.forEach { layer ->
                    val lh = (bottom - liquidTop) * layer.weight / total
                    drawRect(layer.color, Offset(left, y), Size(glassW, lh + 1f))
                    y += lh
                }
            }
            // осадок
            spec.sediment?.let { c ->
                val sh = glassH * 0.12f
                val mound = Path().apply {
                    moveTo(left, bottom - sh * 0.6f)
                    quadraticBezierTo(w * 0.5f, bottom - sh * 1.5f, right, bottom - sh * 0.6f)
                    lineTo(right, bottom)
                    lineTo(left, bottom)
                    close()
                }
                drawPath(mound, c)
                for (g in grains.take(60)) {
                    drawCircle(
                        c.copy(alpha = 1f).let { lerp(it, Color(0xFF8A7A5A), 0.35f) },
                        radius = 2.5f + g[2] * 2.5f,
                        center = Offset(left + g[0] * glassW, bottom - g[1] * sh * 0.9f),
                    )
                }
            }
            // сухой порошок горкой
            if (spec.powder.isNotEmpty()) {
                val ph = glassH * 0.30f
                val base = spec.powder.reduce { acc, c -> lerp(acc, c, 0.5f) }
                val mound = Path().apply {
                    moveTo(left, bottom)
                    quadraticBezierTo(w * 0.35f, bottom - ph * 0.9f, w * 0.5f, bottom - ph * 1.25f)
                    quadraticBezierTo(w * 0.65f, bottom - ph * 0.9f, right, bottom)
                    close()
                }
                drawPath(mound, base)
                grains.forEachIndexed { i, g ->
                    val gx = left + glassW * (0.1f + 0.8f * g[0])
                    // зёрна только внутри горки
                    val dx = kotlin.math.abs(gx - w * 0.5f) / (glassW * 0.5f)
                    val maxH = ph * 1.2f * (1f - dx * dx)
                    val gy = bottom - g[1] * maxH
                    drawCircle(
                        spec.powder[i % spec.powder.size],
                        radius = 3f + g[2] * 3.5f,
                        center = Offset(gx, gy),
                    )
                }
            }
            // пузырьки газа
            if (spec.bubbles) {
                for (b in bubblePos) {
                    val p = (phase * b[3] + b[1]) % 1f
                    val by = bottom - p * (bottom - liquidTop)
                    val bx = left + glassW * (0.1f + 0.8f * b[0]) + kotlin.math.sin((p * 6.28f * 2f)) * 6f
                    val br = w * b[2]
                    drawCircle(Color.White.copy(alpha = 0.35f), br, Offset(bx, by))
                    drawCircle(Color.White.copy(alpha = 0.9f), br, Offset(bx, by), style = Stroke(2.5f))
                }
            }
            // пена
            if (spec.foam) {
                for (g in grains.take(70)) {
                    val pulse = 1f + 0.12f * kotlin.math.sin((phase + g[0]) * 6.28f)
                    drawCircle(
                        Color.White.copy(alpha = 0.95f),
                        radius = (w * 0.025f + g[2] * w * 0.03f) * pulse,
                        center = Offset(left + glassW * g[0], liquidTop - g[1] * glassH * 0.14f + glassH * 0.02f),
                    )
                }
            }
        }

        // контур и блик
        drawPath(open, Color(0xFF8AA4C8), style = Stroke(width = 7f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(Color(0xFF8AA4C8), Offset(left - 10f, top), Offset(right + 10f, top), strokeWidth = 7f, cap = StrokeCap.Round)
        drawLine(
            Color.White.copy(alpha = 0.55f), Offset(left + w * 0.05f, top + glassH * 0.12f),
            Offset(left + w * 0.05f, bottom - glassH * 0.25f), strokeWidth = 8f, cap = StrokeCap.Round,
        )
        // деления
        for (i in 1..4) {
            val y = bottom - glassH * 0.18f * i
            drawLine(Color(0xFF8AA4C8), Offset(right - w * 0.08f, y), Offset(right, y), strokeWidth = 4f)
        }
    }
}

// ------------------------------------------------------------------
// Кнопки, плитки, шапка экрана
// ------------------------------------------------------------------

@Composable
fun BigButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Palette.Purple,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 68.dp),
        shape = RoundedCornerShape(34.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFC9CCDD),
            disabledContentColor = Color.White,
        ),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun StarsChip(stars: Int, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Palette.Sun)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Text("⭐ $stars", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Palette.Ink)
    }
}

@Composable
fun TypeChip(type: OutcomeType, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(24.dp))
            .background(type.color)
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Text(type.label, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun ScreenScaffold(
    title: String,
    stars: Int,
    onBack: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Palette.Sky)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (onBack != null) {
                BigButton("← Назад", onBack, color = Palette.PurpleDark)
            }
            Text(
                title, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Palette.Ink,
                modifier = Modifier.weight(1f),
            )
            StarsChip(stars)
        }
        content()
    }
}

/** Плитка вещества на полке. */
@Composable
fun SubstanceTile(
    s: Substance,
    selected: Boolean,
    hinted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = rememberInfiniteTransition(label = "hint")
    val pulse by t.animateFloat(
        0.35f, 1f, infiniteRepeatable(tween(650), RepeatMode.Reverse), label = "pulse",
    )
    val borderColor = when {
        selected -> Palette.Purple
        hinted -> Palette.Sun.copy(alpha = pulse)
        else -> Color(0xFFD5DCEE)
    }
    Column(
        modifier
            .heightIn(min = 130.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) Color(0xFFEFEAFF) else Color.White)
            .border(if (selected || hinted) 6.dp else 2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(s.color.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center,
        ) { Text(s.emoji, fontSize = 36.sp) }
        Text(
            s.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            color = Palette.Ink, lineHeight = 24.sp, modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            if (s.isLiquid) "жидкость" else "порошок / крупинки",
            fontSize = 15.sp, color = Palette.Soft, textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(28.dp))
            .background(background)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}
