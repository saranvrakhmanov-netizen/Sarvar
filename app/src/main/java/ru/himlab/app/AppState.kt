package ru.himlab.app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Достижение. Открывается, когда current(state) >= goal. */
data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val goal: Int,
    val current: (AppState) -> Int,
)

val ACHIEVEMENTS: List<Achievement> = listOf(
    Achievement("first_mix", "🧪", "Первый опыт", "Смешай любые два вещества", 1) { it.discovered.size },
    Achievement("explorer_5", "🔍", "Юный исследователь", "Открой 5 разных смешиваний", 5) { it.discovered.size },
    Achievement("explorer_15", "🥼", "Опытный химик", "Открой 15 разных смешиваний", 15) { it.discovered.size },
    Achievement("explorer_all", "🏆", "Мастер лаборатории", "Открой все смешивания", PAIR_COUNT) { it.discovered.size },
    Achievement("reaction", "🌋", "Охотник за реакциями", "Найди химическую реакцию", 1) { it.discoveredOfType(OutcomeType.REACTION) },
    Achievement("layers", "🥞", "Мастер слоёв", "Найди 3 смеси со слоями", 3) { it.discoveredOfType(OutcomeType.LAYERS) },
    Achievement("dissolve", "🫗", "Мастер растворения", "Найди 3 смеси, где всё растворилось", 3) { it.discoveredOfType(OutcomeType.MIX) },
    Achievement("experiments", "🎓", "Юный учёный", "Пройди все 4 опыта-задания", EXPERIMENTS.size) { it.experimentStars.size },
    Achievement("no_hints", "🌟", "Без подсказок", "Пройди опыт на 3 звезды", 1) { s -> s.experimentStars.values.count { it >= 3 } },
    Achievement("facts", "📚", "Знаток фактов", "Прочитай все карточки с фактами", FACTS.size) { it.factsRead.size },
    Achievement("quiz", "🧠", "Умник", "Дай 8 правильных ответов в викторинах", 8) { s -> s.quizBest.values.sum() },
    Achievement("stars", "⭐", "Звёздный коллекционер", "Собери 40 звёзд", 40) { it.stars },
)

/**
 * Весь прогресс ребёнка. Хранится только на устройстве (SharedPreferences),
 * наружу ничего не отправляется.
 */
class AppState(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("himlab_progress", Context.MODE_PRIVATE)

    var discovered: Set<String> by mutableStateOf(loadSet(K_DISC))
        private set
    var experimentStars: Map<String, Int> by mutableStateOf(loadMap(K_EXP))
        private set
    var quizBest: Map<String, Int> by mutableStateOf(loadMap(K_QUIZ))
        private set
    var factsRead: Set<String> by mutableStateOf(loadSet(K_FACTS))
        private set
    var unlocked: Set<String> by mutableStateOf(loadSet(K_UNLOCKED))
        private set

    /** Очередь новых достижений, которые надо показать. */
    var pending: List<Achievement> by mutableStateOf(emptyList())
        private set

    val stars: Int
        get() = discovered.size + experimentStars.values.sum() + factsRead.size + quizBest.values.sum()

    fun discoveredOfType(type: OutcomeType): Int =
        discovered.count { outcomeTypeByKey(it) == type }

    fun isDiscovered(a: String, b: String): Boolean = pairKey(a, b) in discovered

    fun recordMix(a: String, b: String) {
        val key = pairKey(a, b)
        if (key in discovered) return
        discovered = discovered + key
        prefs.edit().putStringSet(K_DISC, discovered).apply()
        checkAchievements()
    }

    fun completeExperiment(id: String, stars: Int) {
        val best = experimentStars[id] ?: 0
        if (stars <= best) return
        experimentStars = experimentStars + (id to stars)
        saveMap(K_EXP, experimentStars)
        checkAchievements()
    }

    fun markFactRead(id: String) {
        if (id in factsRead) return
        factsRead = factsRead + id
        prefs.edit().putStringSet(K_FACTS, factsRead).apply()
        checkAchievements()
    }

    fun recordQuiz(id: String, correct: Int) {
        val best = quizBest[id] ?: 0
        if (correct <= best) return
        quizBest = quizBest + (id to correct)
        saveMap(K_QUIZ, quizBest)
        checkAchievements()
    }

    fun dismissAchievement() {
        if (pending.isNotEmpty()) pending = pending.drop(1)
    }

    fun resetAll() {
        prefs.edit().clear().apply()
        discovered = emptySet()
        experimentStars = emptyMap()
        quizBest = emptyMap()
        factsRead = emptySet()
        unlocked = emptySet()
        pending = emptyList()
    }

    private fun checkAchievements() {
        val newly = ACHIEVEMENTS.filter { it.id !in unlocked && it.current(this) >= it.goal }
        if (newly.isEmpty()) return
        unlocked = unlocked + newly.map { it.id }
        prefs.edit().putStringSet(K_UNLOCKED, unlocked).apply()
        pending = pending + newly
    }

    private fun loadSet(key: String): Set<String> =
        prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()

    private fun loadMap(key: String): Map<String, Int> =
        loadSet(key).mapNotNull { entry ->
            val i = entry.lastIndexOf(':')
            if (i < 0) null else entry.substring(0, i) to (entry.substring(i + 1).toIntOrNull() ?: 0)
        }.toMap()

    private fun saveMap(key: String, map: Map<String, Int>) {
        prefs.edit().putStringSet(key, map.map { "${it.key}:${it.value}" }.toSet()).apply()
    }

    private companion object {
        const val K_DISC = "discovered"
        const val K_EXP = "experiment_stars"
        const val K_QUIZ = "quiz_best"
        const val K_FACTS = "facts_read"
        const val K_UNLOCKED = "unlocked"
    }
}
