package com.example.weathersimulator.ui.screens.games.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherMemoryViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(WeatherMemoryGameState())
    val state: StateFlow<WeatherMemoryGameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var nextRoundId: Long = 1L

    init {
        startLevel(WeatherMemoryLevel.Easy)
    }

    fun startLevel(level: WeatherMemoryLevel) {
        timerJob?.cancel()
        _state.value = WeatherMemoryGameState(
            roundId = nextRoundId++,
            selectedLevel = level,
            cards = createDeck(level)
        )
        startTimer()
    }

    fun restartGame() {
        startLevel(_state.value.selectedLevel)
    }

    fun startNextLevel() {
        val nextLevel = when (_state.value.selectedLevel) {
            WeatherMemoryLevel.Easy -> WeatherMemoryLevel.Medium
            WeatherMemoryLevel.Medium -> WeatherMemoryLevel.Advanced
            WeatherMemoryLevel.Advanced -> WeatherMemoryLevel.Easy
        }
        startLevel(nextLevel)
    }

    fun flipCard(instanceId: String) {
        val current = _state.value
        if (
            current.phase != WeatherMemoryPhase.Playing ||
            current.isCheckingPair ||
            current.activeDiscovery != null
        ) {
            return
        }

        val tappedCard = current.cards.firstOrNull { it.instanceId == instanceId } ?: return
        if (tappedCard.isFaceUp || tappedCard.isMatched) return

        if (current.selectedCardIds.isEmpty()) {
            _state.update { state ->
                state.copy(
                    cards = state.cards.map { card ->
                        if (card.instanceId == instanceId) card.copy(isFaceUp = true) else card
                    },
                    selectedCardIds = listOf(instanceId)
                )
            }
            return
        }

        val firstCardId = current.selectedCardIds.first()
        if (firstCardId == instanceId) return

        val firstCard = current.cards.firstOrNull { it.instanceId == firstCardId } ?: return
        val isMatch = firstCard.pairId == tappedCard.pairId

        if (isMatch) {
            revealMatch(tappedCard.pairId, instanceId)
        } else {
            revealMismatch(firstCardId, instanceId)
        }
    }

    fun continueDiscovery() {
        val shouldFinish = _state.value.matchedPairs == _state.value.totalPairs
        _state.update { state ->
            state.copy(
                activeDiscovery = null,
                phase = if (shouldFinish) WeatherMemoryPhase.Finished else state.phase
            )
        }
        if (shouldFinish) {
            timerJob?.cancel()
        }
    }

    private fun revealMatch(pairId: String, secondCardId: String) {
        val discoveredCard = WeatherMemoryData.cardById(pairId) ?: return

        _state.update { state ->
            val newStreak = state.currentStreak + 1
            val streakBonus = ((newStreak - 1) * 2).coerceAtMost(8)
            val learnedCards = if (state.learnedCards.any { it.id == discoveredCard.id }) {
                state.learnedCards
            } else {
                state.learnedCards + discoveredCard
            }

            state.copy(
                cards = state.cards.map { card ->
                    if (card.pairId == pairId || card.instanceId == secondCardId) {
                        card.copy(isFaceUp = true, isMatched = true)
                    } else {
                        card
                    }
                },
                selectedCardIds = emptyList(),
                matchedPairs = state.matchedPairs + 1,
                attempts = state.attempts + 1,
                currentStreak = newStreak,
                bestStreak = maxOf(state.bestStreak, newStreak),
                score = state.score + 10 + streakBonus,
                activeDiscovery = discoveredCard,
                learnedCards = learnedCards
            )
        }
    }

    private fun revealMismatch(firstCardId: String, secondCardId: String) {
        val roundId = _state.value.roundId

        _state.update { state ->
            state.copy(
                cards = state.cards.map { card ->
                    if (card.instanceId == secondCardId) card.copy(isFaceUp = true) else card
                },
                selectedCardIds = listOf(firstCardId, secondCardId),
                attempts = state.attempts + 1,
                mistakes = state.mistakes + 1,
                currentStreak = 0,
                score = (state.score - 1).coerceAtLeast(0),
                isCheckingPair = true
            )
        }

        viewModelScope.launch {
            delay(1_000L)
            hideMismatch(
                roundId = roundId,
                firstCardId = firstCardId,
                secondCardId = secondCardId
            )
        }
    }

    private fun hideMismatch(
        roundId: Long,
        firstCardId: String,
        secondCardId: String
    ) {
        _state.update { state ->
            val selectedIds = setOf(firstCardId, secondCardId)
            if (state.roundId != roundId || state.selectedCardIds.toSet() != selectedIds) {
                return@update state
            }

            state.copy(
                cards = state.cards.map { card ->
                    if (card.instanceId in selectedIds && !card.isMatched) {
                        card.copy(isFaceUp = false)
                    } else {
                        card
                    }
                },
                selectedCardIds = emptyList(),
                isCheckingPair = false
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.phase == WeatherMemoryPhase.Playing) {
                delay(1_000L)
                val current = _state.value
                if (current.phase != WeatherMemoryPhase.Playing) break
                if (current.activeDiscovery != null) continue

                _state.update { state ->
                    if (state.phase == WeatherMemoryPhase.Playing && state.activeDiscovery == null) {
                        state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun createDeck(level: WeatherMemoryLevel): List<WeatherMemoryBoardCard> {
        return WeatherMemoryData.selectedCards(level)
            .flatMap { card -> listOf(card.toBoardCard(1), card.toBoardCard(2)) }
            .shuffled()
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
