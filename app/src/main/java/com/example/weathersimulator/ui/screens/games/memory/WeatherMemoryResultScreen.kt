package com.example.weathersimulator.ui.screens.games.memory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ResultBackground = Color(0xFF061625)
private val ResultBackgroundDeep = Color(0xFF03111F)
private val ResultSurface = Color(0xFF092237)
private val ResultSurfaceSoft = Color(0xFF0F3450)
private val ResultBorder = Color(0xFF2E6A90)
private val ResultCyan = Color(0xFF4ED7FF)
private val ResultYellow = Color(0xFFFFCF54)
private val ResultGreen = Color(0xFF63E6A6)
private val ResultRed = Color(0xFFFF867A)
private val ResultMutedText = Color.White.copy(alpha = 0.72f)

@Composable
fun WeatherMemoryResultScreen(
    state: WeatherMemoryGameState,
    onPlayAgain: () -> Unit,
    onNextLevel: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(containerColor = ResultBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ResultBackgroundDeep,
                            ResultBackground,
                            Color(0xFF0B3145)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WeatherMemoryResultHeader(onBack = onBack)
                WeatherMemoryResultHero(state = state)
                WeatherMemoryResultStats(state = state)
                LearnedCardsList(cards = state.learnedCards)
                WeatherMemoryResultActions(
                    onPlayAgain = onPlayAgain,
                    onNextLevel = onNextLevel,
                    onBack = onBack
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun WeatherMemoryResultHeader(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF102345).copy(alpha = 0.78f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Inapoi",
                tint = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Rezultat Weather Memory",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Recapitulare joc",
                color = ResultMutedText,
                fontSize = 16.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun WeatherMemoryResultHero(state: WeatherMemoryGameState) {
    val completed = state.matchedPairs == state.totalPairs && state.totalPairs > 0
    val title = if (completed) "Felicitari! Ai terminat jocul 🎉" else "Tura s-a incheiat"
    val subtitle = if (completed) {
        "Ai gasit toate perechile in ${state.elapsedSeconds}s, cu ${state.accuracyPercent}% acuratete."
    } else {
        "Ai descoperit ${state.matchedPairs}/${state.totalPairs} perechi. Poti relua nivelul pentru un scor mai bun."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        ResultSurfaceSoft,
                        Color(0xFF143857),
                        Color(0xFF13243D)
                    )
                )
            )
            .border(BorderStroke(1.dp, ResultCyan.copy(alpha = 0.45f)), RoundedCornerShape(26.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(ResultYellow.copy(alpha = 0.14f))
                .border(BorderStroke(1.dp, ResultYellow.copy(alpha = 0.44f)), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = null,
                tint = ResultYellow,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 25.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            color = ResultMutedText,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Scor final: ${state.finalScore}",
            color = ResultGreen,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 29.sp,
            lineHeight = 33.sp
        )
    }
}

@Composable
private fun WeatherMemoryResultStats(state: WeatherMemoryGameState) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ResultStatCard(
                label = "Perechi",
                value = "${state.matchedPairs}/${state.totalPairs}",
                icon = Icons.Rounded.SportsEsports,
                accent = ResultCyan,
                modifier = Modifier.weight(1f)
            )
            ResultStatCard(
                label = "Incercari",
                value = state.attempts.toString(),
                icon = Icons.Rounded.Flag,
                accent = ResultRed,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ResultStatCard(
                label = "Timp total",
                value = "${state.elapsedSeconds}s",
                icon = Icons.Rounded.Timer,
                accent = ResultGreen,
                modifier = Modifier.weight(1f)
            )
            ResultStatCard(
                label = "Acuratete",
                value = "${state.accuracyPercent}%",
                icon = Icons.Rounded.EmojiEvents,
                accent = ResultYellow,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ResultStatCard(
                label = "Greseli",
                value = state.mistakes.toString(),
                icon = Icons.Rounded.Flag,
                accent = ResultRed,
                modifier = Modifier.weight(1f)
            )
            ResultStatCard(
                label = "Serie max",
                value = state.bestStreak.toString(),
                icon = Icons.Rounded.EmojiEvents,
                accent = ResultYellow,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResultStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ResultSurface.copy(alpha = 0.90f))
            .border(BorderStroke(1.dp, accent.copy(alpha = 0.30f)), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(26.dp)
        )
        Column {
            Text(
                text = label,
                color = ResultMutedText,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LearnedCardsList(cards: List<WeatherMemoryCard>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ResultSurface.copy(alpha = 0.90f))
            .border(BorderStroke(1.dp, ResultBorder.copy(alpha = 0.54f)), RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Ai invatat despre:",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            lineHeight = 21.sp
        )

        if (cards.isEmpty()) {
            Text(
                text = "Nu ai descoperit inca un fenomen. O tura noua poate schimba rapid asta.",
                color = ResultMutedText,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        } else {
            cards.forEach { card ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ResultSurfaceSoft.copy(alpha = 0.64f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = card.icon,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        modifier = Modifier.size(32.dp),
                        textAlign = TextAlign.Center
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = card.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = card.category,
                            color = ResultMutedText,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMemoryResultActions(
    onPlayAgain: () -> Unit,
    onNextLevel: () -> Unit,
    onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ResultYellow,
                contentColor = Color(0xFF2A1C00)
            ),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Replay,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Joaca din nou",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onNextLevel,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                border = BorderStroke(1.dp, ResultCyan.copy(alpha = 0.70f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Flag,
                    contentDescription = null,
                    tint = ResultCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = "Alt nivel",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                border = BorderStroke(1.dp, ResultBorder.copy(alpha = 0.70f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = "Inapoi la jocuri",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
    }
}
