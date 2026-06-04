package com.example.weathersimulator.ui.screens.games.memory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.max

private val MemoryBackground = Color(0xFF061625)
private val MemoryBackgroundDeep = Color(0xFF03111F)
private val MemorySurface = Color(0xFF092237)
private val MemorySurfaceSoft = Color(0xFF0F3450)
private val MemoryBorder = Color(0xFF2E6A90)
private val MemoryCyan = Color(0xFF4ED7FF)
private val MemoryYellow = Color(0xFFFFCF54)
private val MemoryGreen = Color(0xFF63E6A6)
private val MemoryRed = Color(0xFFFF867A)
private val MemoryTextMuted = Color.White.copy(alpha = 0.72f)

@Composable
fun WeatherMemoryScreen(
    onBack: () -> Unit,
    viewModel: WeatherMemoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.phase == WeatherMemoryPhase.Finished) {
        WeatherMemoryResultScreen(
            state = state,
            onPlayAgain = viewModel::restartGame,
            onNextLevel = viewModel::startNextLevel,
            onBack = onBack
        )
        return
    }

    Scaffold(containerColor = MemoryBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MemoryBackgroundDeep,
                            MemoryBackground,
                            Color(0xFF0B3145)
                        )
                    )
                )
                .padding(padding)
        ) {
            MemoryBackgroundPattern()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherMemoryHeader(onBack = onBack)

                WeatherMemoryLevelSelector(
                    selectedLevel = state.selectedLevel,
                    onLevelSelected = viewModel::startLevel
                )

                WeatherMemoryStats(state = state)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(state.selectedLevel.columns),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = state.cards,
                        key = { it.instanceId }
                    ) { card ->
                        WeatherMemoryCardTile(
                            card = card,
                            onClick = { viewModel.flipCard(card.instanceId) }
                        )
                    }
                }

                WeatherMemoryBottomProgress(state = state)
            }

            state.activeDiscovery?.let { discoveredCard ->
                WeatherMemoryDiscoveryDialog(
                    card = discoveredCard,
                    onContinue = viewModel::continueDiscovery
                )
            }
        }
    }
}

@Composable
private fun WeatherMemoryHeader(
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
                text = "Weather Memory",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 27.sp,
                lineHeight = 30.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Gaseste perechile meteo",
                color = MemoryTextMuted,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MemoryYellow.copy(alpha = 0.14f))
                .border(BorderStroke(1.dp, MemoryYellow.copy(alpha = 0.42f)), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MemoryYellow,
                modifier = Modifier.size(29.dp)
            )
        }
    }
}

@Composable
private fun WeatherMemoryLevelSelector(
    selectedLevel: WeatherMemoryLevel,
    onLevelSelected: (WeatherMemoryLevel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeatherMemoryLevel.values().forEach { level ->
            val selected = level == selectedLevel
            val colors = if (selected) {
                ButtonDefaults.buttonColors(
                    containerColor = MemoryCyan,
                    contentColor = Color(0xFF042235)
                )
            } else {
                ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            }

            if (selected) {
                Button(
                    onClick = { onLevelSelected(level) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = colors,
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    LevelButtonText(level = level)
                }
            } else {
                OutlinedButton(
                    onClick = { onLevelSelected(level) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = colors,
                    border = BorderStroke(1.dp, MemoryBorder.copy(alpha = 0.65f)),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    LevelButtonText(level = level)
                }
            }
        }
    }
}

@Composable
private fun LevelButtonText(level: WeatherMemoryLevel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = level.label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 13.sp,
            maxLines = 1
        )
        Text(
            text = level.subtitle,
            fontSize = 11.sp,
            lineHeight = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun WeatherMemoryStats(state: WeatherMemoryGameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeatherMemoryStatPill(
            label = "Scor",
            value = state.score.toString(),
            icon = Icons.Rounded.EmojiEvents,
            accent = MemoryYellow,
            modifier = Modifier.weight(1f)
        )
        WeatherMemoryStatPill(
            label = "Timp",
            value = "${state.elapsedSeconds}s",
            icon = Icons.Rounded.Timer,
            accent = MemoryCyan,
            modifier = Modifier.weight(1f)
        )
        WeatherMemoryStatPill(
            label = "Incercari",
            value = state.attempts.toString(),
            icon = Icons.Rounded.Flag,
            accent = MemoryRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WeatherMemoryStatPill(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MemorySurface.copy(alpha = 0.88f))
            .border(BorderStroke(1.dp, accent.copy(alpha = 0.32f)), RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                color = MemoryTextMuted,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                maxLines = 1
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun WeatherMemoryCardTile(
    card: WeatherMemoryBoardCard,
    onClick: () -> Unit
) {
    val isRevealed = card.isFaceUp || card.isMatched
    val shape = RoundedCornerShape(18.dp)
    val borderColor = when {
        card.isMatched -> MemoryGreen
        isRevealed -> MemoryCyan
        else -> MemoryBorder
    }

    Box(
        modifier = Modifier
            .aspectRatio(0.76f)
            .clip(shape)
            .background(
                brush = if (isRevealed) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF183D5A),
                            Color(0xFF0D2B44)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF143B58),
                            Color(0xFF0B2237),
                            Color(0xFF1C4D63)
                        )
                    )
                }
            )
            .border(BorderStroke(1.2.dp, borderColor.copy(alpha = 0.72f)), shape)
            .clickable(enabled = !isRevealed) { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRevealed) {
            RevealedWeatherCard(card = card)
        } else {
            HiddenWeatherCard()
        }
    }
}

@Composable
private fun RevealedWeatherCard(card: WeatherMemoryBoardCard) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = card.icon,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = card.title,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = card.category,
            color = MemoryTextMuted,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HiddenWeatherCard() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDimension = minOf(size.width, size.height)
            drawCircle(
                color = MemoryCyan.copy(alpha = 0.16f),
                radius = minDimension * 0.48f,
                center = Offset(size.width * 0.26f, size.height * 0.18f)
            )
            drawCircle(
                color = MemoryYellow.copy(alpha = 0.13f),
                radius = minDimension * 0.34f,
                center = Offset(size.width * 0.82f, size.height * 0.88f)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "?",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                lineHeight = 32.sp
            )
            Text(
                text = "Meteo",
                color = MemoryTextMuted,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun WeatherMemoryBottomProgress(state: WeatherMemoryGameState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MemorySurface.copy(alpha = 0.90f))
            .border(BorderStroke(1.dp, MemoryBorder.copy(alpha = 0.56f)), RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progres: ${state.progressLabel}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
            Text(
                text = "+${state.speedBonus} bonus",
                color = MemoryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 15.sp
            )
        }

        LinearProgressIndicator(
            progress = {
                if (state.totalPairs == 0) 0f else state.matchedPairs / max(1, state.totalPairs).toFloat()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = MemoryGreen,
            trackColor = Color.White.copy(alpha = 0.16f)
        )
    }
}

@Composable
private fun WeatherMemoryDiscoveryDialog(
    card: WeatherMemoryCard,
    onContinue: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        containerColor = MemorySurface,
        titleContentColor = Color.White,
        textContentColor = MemoryTextMuted,
        title = {
            Text(
                text = "Ai descoperit: ${card.title} ${card.icon}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 21.sp,
                lineHeight = 25.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MemoryCyan.copy(alpha = 0.12f))
                        .border(BorderStroke(1.dp, MemoryCyan.copy(alpha = 0.36f)), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = card.icon,
                        fontSize = 42.sp,
                        lineHeight = 46.sp
                    )
                }

                Text(
                    text = card.explanation,
                    color = MemoryTextMuted,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MemoryYellow,
                    contentColor = Color(0xFF2A1C00)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Continua",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun MemoryBackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = MemoryCyan.copy(alpha = 0.10f),
            radius = size.width * 0.54f,
            center = Offset(size.width * 0.98f, size.height * 0.08f)
        )
        drawCircle(
            color = MemoryYellow.copy(alpha = 0.08f),
            radius = size.width * 0.42f,
            center = Offset(size.width * 0.06f, size.height * 0.84f)
        )
    }
}
