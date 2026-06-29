package com.example.weathersimulator.ui.screens.games.memory

data class WeatherMemoryCard(
    val id: String,
    val title: String,
    val icon: String,
    val category: String,
    val explanation: String,
    val difficulty: String
)

data class WeatherMemoryBoardCard(
    val instanceId: String,
    val pairId: String,
    val title: String,
    val icon: String,
    val category: String,
    val explanation: String,
    val difficulty: String,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

enum class WeatherMemoryLevel(
    val label: String,
    val subtitle: String,
    val pairCount: Int,
    val columns: Int,
    val targetSeconds: Int
) {
    Easy(
        label = "Nivel 1",
        subtitle = "Ușor",
        pairCount = 6,
        columns = 3,
        targetSeconds = 75
    ),
    Medium(
        label = "Nivel 2",
        subtitle = "Mediu",
        pairCount = 8,
        columns = 4,
        targetSeconds = 110
    ),
    Advanced(
        label = "Nivel 3",
        subtitle = "Avansat",
        pairCount = 10,
        columns = 4,
        targetSeconds = 150
    )
}

enum class WeatherMemoryPhase {
    Playing,
    Finished
}

data class WeatherMemoryGameState(
    val roundId: Long = 0L,
    val selectedLevel: WeatherMemoryLevel = WeatherMemoryLevel.Easy,
    val cards: List<WeatherMemoryBoardCard> = emptyList(),
    val selectedCardIds: List<String> = emptyList(),
    val matchedPairs: Int = 0,
    val attempts: Int = 0,
    val mistakes: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val score: Int = 0,
    val elapsedSeconds: Int = 0,
    val isCheckingPair: Boolean = false,
    val activeDiscovery: WeatherMemoryCard? = null,
    val learnedCards: List<WeatherMemoryCard> = emptyList(),
    val phase: WeatherMemoryPhase = WeatherMemoryPhase.Playing
) {
    val totalPairs: Int
        get() = cards.size / 2

    val progressLabel: String
        get() = "$matchedPairs/$totalPairs perechi găsite"

    val accuracyPercent: Int
        get() = if (attempts == 0) 100 else ((matchedPairs * 100f) / attempts).toInt()

    val speedBonus: Int
        get() = (selectedLevel.targetSeconds - elapsedSeconds).coerceAtLeast(0)

    val accuracyBonus: Int
        get() = when {
            attempts == 0 -> 0
            accuracyPercent >= 90 -> 20
            accuracyPercent >= 75 -> 12
            accuracyPercent >= 60 -> 6
            else -> 0
        }

    val finalScore: Int
        get() = (score + speedBonus + accuracyBonus).coerceAtLeast(0)
}

fun WeatherMemoryCard.toBoardCard(copyIndex: Int): WeatherMemoryBoardCard {
    return WeatherMemoryBoardCard(
        instanceId = "${id}_$copyIndex",
        pairId = id,
        title = title,
        icon = icon,
        category = category,
        explanation = explanation,
        difficulty = difficulty
    )
}
