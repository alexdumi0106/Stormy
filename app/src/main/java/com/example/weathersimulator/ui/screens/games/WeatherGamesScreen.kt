package com.example.weathersimulator.ui.screens.games

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersimulator.R
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.random.Random

private const val TotalGameMillis = 60_000L
private const val StartingLives = 3
private const val PlaneX = 0.57f
private const val MinPlaneY = 0.26f
private const val MaxPlaneY = 0.72f

private val GamesBackground = Color(0xFF061625)
private val GamesBackgroundDeep = Color(0xFF03111F)
private val GamesSurface = Color(0xFF092237)
private val GamesSurfaceSoft = Color(0xFF0E304B)
private val GamesBorder = Color(0xFF2E6A90)
private val GamesCyan = Color(0xFF4ED7FF)
private val GamesYellow = Color(0xFFFFCF54)
private val GamesGreen = Color(0xFF63E6A6)
private val GamesRed = Color(0xFFFF867A)
private val GamesMutedText = Color.White.copy(alpha = 0.70f)

private enum class CloudCatcherPhase {
    Ready,
    Running,
    Paused,
    Finished
}

private enum class CloudCatcherObjectType {
    WhiteCloud,
    RainDrop,
    StormCloud,
    Lightning
}

private data class CloudCatcherObject(
    val id: Long,
    val type: CloudCatcherObjectType,
    val x: Float,
    val y: Float,
    val speed: Float,
    val radius: Float
)

private data class WeatherGameItem(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accent: Color
)

private enum class WeatherGameScene {
    CloudCatcher,
    WeatherMemory
}

@Composable
fun WeatherGamesScreen(
    onBack: () -> Unit,
    onCloudCatcherClick: () -> Unit,
    onWeatherMemoryClick: () -> Unit
) {
    Scaffold(
        containerColor = GamesBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            GamesBackgroundDeep,
                            GamesBackground,
                            Color(0xFF0B3145)
                        )
                    )
                )
                .padding(padding)
        ) {
            GamesBackgroundPattern()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 17.dp)
            ) {
                WeatherGamesHeader(onBack = onBack)

                WeatherGamesReferenceImage(
                    drawableRes = R.drawable.weather_games_hero_card,
                    contentDescription = "Weather Games",
                    aspectRatio = 820f / 418f
                )

                AdventureTitle()

                WeatherGamesReferenceImage(
                    drawableRes = R.drawable.weather_games_cloud_catcher_card,
                    contentDescription = "Cloud Catcher",
                    aspectRatio = 820f / 380f,
                    onClick = onCloudCatcherClick
                )

                Spacer(Modifier.height(15.dp))

                WeatherMemoryHubCard(
                    onClick = onWeatherMemoryClick
                )

                Spacer(Modifier.height(15.dp))

                WeatherGamesReferenceImage(
                    drawableRes = R.drawable.weather_games_next_card,
                    contentDescription = "Urmatorul joc",
                    aspectRatio = 820f / 120f
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CloudCatcherScreen(
    onBack: () -> Unit
) {
    var phase by remember { mutableStateOf(CloudCatcherPhase.Ready) }
    var score by remember { mutableStateOf(0) }
    var lives by remember { mutableStateOf(StartingLives) }
    var remainingMillis by remember { mutableStateOf(TotalGameMillis) }
    var planeY by remember { mutableStateOf(0.56f) }
    var objects by remember { mutableStateOf(emptyList<CloudCatcherObject>()) }
    var nextObjectId by remember { mutableStateOf(0L) }

    fun startGame() {
        score = 0
        lives = StartingLives
        remainingMillis = TotalGameMillis
        planeY = 0.56f
        objects = initialCloudCatcherObjects()
        nextObjectId = 5L
        phase = CloudCatcherPhase.Running
    }

    if (phase == CloudCatcherPhase.Ready) {
        CloudCatcherIntroScreen(
            onBack = onBack,
            onStartClick = ::startGame
        )
        return
    }

    LaunchedEffect(phase) {
        if (phase != CloudCatcherPhase.Running) return@LaunchedEffect

        val random = Random(System.currentTimeMillis())
        var lastFrameNanos = 0L
        var spawnAccumulator = 0f

        while (phase == CloudCatcherPhase.Running) {
            withFrameNanos { frameNanos ->
                if (lastFrameNanos == 0L) {
                    lastFrameNanos = frameNanos
                    return@withFrameNanos
                }

                val deltaSeconds = ((frameNanos - lastFrameNanos) / 1_000_000_000f)
                    .coerceIn(0f, 0.05f)
                lastFrameNanos = frameNanos

                remainingMillis = (remainingMillis - (deltaSeconds * 1000f).roundToLong())
                    .coerceAtLeast(0L)

                spawnAccumulator += deltaSeconds
                val interval = spawnInterval(remainingMillis)
                if (spawnAccumulator >= interval) {
                    spawnAccumulator = 0f
                    val newObject = createCloudCatcherObject(
                        id = nextObjectId,
                        random = random,
                        progress = gameProgress(remainingMillis)
                    )
                    nextObjectId += 1L
                    objects = objects + newObject
                }

                objects = objects.mapNotNull { item ->
                    val moved = item.copy(x = item.x - item.speed * deltaSeconds)
                    val collided = moved.collidesWithPlane(planeY)

                    when {
                        collided -> {
                            when (moved.type) {
                                CloudCatcherObjectType.WhiteCloud -> score += 10
                                CloudCatcherObjectType.RainDrop -> score += 5
                                CloudCatcherObjectType.Lightning -> score = (score - 5).coerceAtLeast(0)
                                CloudCatcherObjectType.StormCloud -> lives = (lives - 1).coerceAtLeast(0)
                            }
                            null
                        }

                        moved.x < -0.16f -> null
                        else -> moved
                    }
                }

                if (remainingMillis <= 0L || lives <= 0) {
                    phase = CloudCatcherPhase.Finished
                }
            }
        }
    }

    CloudCatcherPlayScreen(
        phase = phase,
        score = score,
        lives = lives,
        remainingMillis = remainingMillis,
        planeY = planeY,
        objects = objects,
        onPlaneYChange = { planeY = it.coerceIn(MinPlaneY, MaxPlaneY) },
        onPauseClick = { phase = CloudCatcherPhase.Paused },
        onResumeClick = { phase = CloudCatcherPhase.Running },
        onBack = onBack,
        onRestartClick = ::startGame
    )
}

@Composable
private fun CloudCatcherIntroScreen(
    onBack: () -> Unit,
    onStartClick: () -> Unit
) {
    Scaffold(
        containerColor = GamesBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF020918),
                            Color(0xFF05182D),
                            Color(0xFF05233C)
                        )
                    )
                )
                .padding(padding)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1C5BAA).copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.90f, size.height * 0.20f),
                        radius = size.width * 0.95f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CloudCatcherHeaderImage(onBack = onBack)

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    CloudCatcherIntroImage(
                        drawableRes = R.drawable.cloud_catcher_intro_stats,
                        contentDescription = "Scor maxim, vieti si timp",
                        aspectRatio = 542f / 97f
                    )

                    Spacer(Modifier.height(24.dp))

                    CloudCatcherIntroHero(onStartClick = onStartClick)

                    Spacer(Modifier.height(28.dp))

                    CloudCatcherIntroImage(
                        drawableRes = R.drawable.cloud_catcher_intro_how,
                        contentDescription = "Cum functioneaza Cloud Catcher",
                        aspectRatio = 542f / 218f
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun CloudCatcherHeaderImage(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(599f / 128f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.cloud_catcher_intro_header),
            contentDescription = "Cloud Catcher",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(56.dp)
                .clip(CircleShape)
                .clickable { onBack() }
        )
    }
}

@Composable
private fun CloudCatcherIntroHero(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(542f / 756f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.cloud_catcher_intro_hero),
            contentDescription = "Cloud Catcher start",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 36.dp, end = 36.dp, bottom = 28.dp)
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(36.dp))
                .clickable { onStartClick() }
        )
    }
}

@Composable
private fun CloudCatcherIntroImage(
    drawableRes: Int,
    contentDescription: String,
    aspectRatio: Float
) {
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun CloudCatcherPlayScreen(
    phase: CloudCatcherPhase,
    score: Int,
    lives: Int,
    remainingMillis: Long,
    planeY: Float,
    objects: List<CloudCatcherObject>,
    onPlaneYChange: (Float) -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onBack: () -> Unit,
    onRestartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF041021))
    ) {
        Image(
            painter = painterResource(id = R.drawable.cloud_catcher_game_background),
            contentDescription = "Cloud Catcher gameplay",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCloudCatcherInteractiveLayer(
                planeY = planeY,
                objects = objects
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CloudCatcherGameHud(
                phase = phase,
                score = score,
                lives = lives,
                remainingMillis = remainingMillis,
                onPauseClick = onPauseClick,
                onExitClick = onBack
            )

            CloudCatcherProgressBar(
                progress = (remainingMillis / TotalGameMillis.toFloat()).coerceIn(0f, 1f)
            )
        }

        CloudCatcherControls(
            onUpClick = { onPlaneYChange(planeY - 0.08f) },
            onDownClick = { onPlaneYChange(planeY + 0.08f) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 230.dp)
        )

        CloudCatcherGameLegend(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 14.dp, end = 14.dp, bottom = 18.dp)
        )

        if (phase == CloudCatcherPhase.Finished) {
            CloudCatcherOverlay(
                phase = phase,
                score = score,
                lives = lives,
                onStartClick = onRestartClick,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (phase == CloudCatcherPhase.Paused) {
            CloudCatcherPauseDialog(
                onContinueClick = onResumeClick,
                onExitClick = onBack,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun CloudCatcherGameHud(
    phase: CloudCatcherPhase,
    score: Int,
    lives: Int,
    remainingMillis: Long,
    onPauseClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(
            onClick = {
                when (phase) {
                    CloudCatcherPhase.Running -> onPauseClick()
                    CloudCatcherPhase.Finished -> onExitClick()
                    else -> Unit
                }
            },
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFF112A4C).copy(alpha = 0.70f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)), CircleShape)
        ) {
            if (phase == CloudCatcherPhase.Finished) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Iesi din joc",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(
                    text = "II",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(74.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF071D39).copy(alpha = 0.72f))
                .border(BorderStroke(1.dp, Color(0xFF6EA7FF).copy(alpha = 0.42f)), RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scor",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = score.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(44.dp)
                    .background(Color.White.copy(alpha = 0.13f))
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vieti",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(StartingLives) { index ->
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = if (index < lives) GamesRed else Color.White.copy(alpha = 0.28f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .height(74.dp)
                .width(96.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF071D39).copy(alpha = 0.72f))
                .border(BorderStroke(1.dp, Color(0xFF6EA7FF).copy(alpha = 0.42f)), RoundedCornerShape(22.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(27.dp)
            )
            Column {
                Text(
                    text = "Timp",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Text(
                    text = remainingMillis.secondsText(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CloudCatcherProgressBar(
    progress: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 68.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF07182E).copy(alpha = 0.72f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF61A9FF),
                                GamesCyan,
                                Color(0xFF9C50FF)
                            )
                        )
                    )
            )
        }

        Icon(
            imageVector = Icons.Rounded.Flag,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun CloudCatcherGameLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071D39).copy(alpha = 0.78f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text(
                text = "Fenomene in joc",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CloudCatcherLegendTile(
                    type = CloudCatcherObjectType.WhiteCloud,
                    title = "Nor alb",
                    value = "+10",
                    valueColor = GamesCyan,
                    modifier = Modifier.weight(1f)
                )
                CloudCatcherLegendTile(
                    type = CloudCatcherObjectType.RainDrop,
                    title = "Picatura",
                    value = "+5",
                    valueColor = GamesCyan,
                    modifier = Modifier.weight(1f)
                )
                CloudCatcherLegendTile(
                    type = CloudCatcherObjectType.Lightning,
                    title = "Fulger",
                    value = "-5",
                    valueColor = GamesRed,
                    modifier = Modifier.weight(1f)
                )
                CloudCatcherLegendTile(
                    type = CloudCatcherObjectType.StormCloud,
                    title = "Nor negru",
                    value = "-1 viata",
                    valueColor = GamesRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CloudCatcherLegendTile(
    type: CloudCatcherObjectType,
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF102A4E).copy(alpha = 0.78f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)), RoundedCornerShape(16.dp))
            .padding(horizontal = 4.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Canvas(
                modifier = Modifier.size(52.dp)
            ) {
                val objectRadius = when (type) {
                    CloudCatcherObjectType.WhiteCloud -> 0.24f
                    CloudCatcherObjectType.RainDrop -> 0.24f
                    CloudCatcherObjectType.Lightning -> 0.25f
                    CloudCatcherObjectType.StormCloud -> 0.24f
                }
                drawCloudCatcherObject(
                    CloudCatcherObject(
                        id = 0L,
                        type = type,
                        x = 0.50f,
                        y = 0.48f,
                        speed = 0f,
                        radius = objectRadius
                    )
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun GamesBackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF183E8E).copy(alpha = 0.28f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.95f, size.height * 0.04f),
                radius = size.width * 0.86f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF7A2CFF).copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.84f, size.height * 0.55f),
                radius = size.width * 0.86f
            )
        )
        drawCircle(
            color = GamesCyan.copy(alpha = 0.07f),
            radius = size.width * 0.44f,
            center = Offset(size.width * 0.02f, size.height * 0.30f)
        )
    }
}

@Composable
private fun WeatherGamesHero() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GamesSurface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF123A62),
                            Color(0xFF0F2E4A),
                            Color(0xFF061625)
                        )
                    )
                )
        ) {
            CloudCatcherPreviewIllustration(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Weather Games",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        lineHeight = 32.sp
                    )
                    Text(
                        text = "Invata vremea prin jocuri scurte, rapide si vizuale.",
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth(0.72f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WeatherGameChip(
                        icon = Icons.Rounded.Cloud,
                        text = "nori"
                    )
                    WeatherGameChip(
                        icon = Icons.Rounded.Bolt,
                        text = "furtuni"
                    )
                    WeatherGameChip(
                        icon = Icons.Rounded.WaterDrop,
                        text = "ploaie"
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherGameCard(
    item: WeatherGameItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GamesSurface.copy(alpha = 0.96f)),
        border = BorderStroke(1.dp, GamesBorder.copy(alpha = 0.62f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(item.accent.copy(alpha = 0.14f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.accent,
                    modifier = Modifier.size(42.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    color = item.accent,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
                Text(
                    text = item.description,
                    color = GamesMutedText,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }

            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NextGamePlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GamesSurfaceSoft.copy(alpha = 0.56f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.SportsEsports,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.48f),
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "Urmatorul joc",
                    color = Color.White.copy(alpha = 0.74f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Slot pregatit pentru un nou mini-joc meteo.",
                    color = Color.White.copy(alpha = 0.54f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun WeatherGameChip(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GamesYellow,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.86f),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
private fun WeatherGamesReferenceImage(
    drawableRes: Int,
    contentDescription: String,
    aspectRatio: Float,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(27.dp)
    val baseModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio)
        .clip(shape)

    Box(
        modifier = if (onClick != null) {
            baseModifier.clickable { onClick() }
        } else {
            baseModifier
        }
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
private fun WeatherMemoryHubCard(
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(27.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1744f / 902f)
            .clip(shape)
            .background(Color(0xFF071334))
    ) {
        Image(
            painter = painterResource(id = R.drawable.weather_memory_card),
            contentDescription = "Weather Memory",
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, GamesCyan.copy(alpha = 0.76f)), shape),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(onClick) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width * 0.179f
                        val centerY = size.height * 0.848f
                        val radius = size.height * 0.108f
                        val dx = tapOffset.x - centerX
                        val dy = tapOffset.y - centerY

                        if (dx * dx + dy * dy <= radius * radius) {
                            onClick()
                        }
                    }
                }
        )
    }
}

@Composable
private fun WeatherGamesHeader(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(106.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(58.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Inapoi",
                tint = Color.White,
                modifier = Modifier.size(43.dp)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Jocuri meteo",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 30.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Mini-jocuri despre vreme",
                color = Color.White.copy(alpha = 0.66f),
                fontSize = 20.sp,
                lineHeight = 22.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF102345).copy(alpha = 0.72f))
                .border(
                    BorderStroke(1.dp, Color(0xFF7F9BDA).copy(alpha = 0.58f)),
                    RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.SportsEsports,
                contentDescription = null,
                tint = GamesYellow,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun WeatherGamesHeroRedesign() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(257.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(Color(0xFF071334))
            .border(
                BorderStroke(1.2.dp, Color(0xFF32A4FF)),
                RoundedCornerShape(27.dp)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawWeatherGamesHeroArt()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, top = 28.dp, bottom = 27.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(
                        text = "Weather✦",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 37.sp,
                        lineHeight = 40.sp
                    )
                    Text(
                        text = "Games",
                        color = Color(0xFF54B9FF),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 43.sp,
                        lineHeight = 45.sp
                    )
                }

                Text(
                    text = "Învață vremea prin jocuri scurte,\nrapide și vizuale.",
                    color = Color.White.copy(alpha = 0.70f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                WeatherGameChipRedesign(
                    icon = Icons.Rounded.Cloud,
                    text = "nori"
                )
                WeatherGameChipRedesign(
                    icon = Icons.Rounded.Bolt,
                    text = "furtuni"
                )
                WeatherGameChipRedesign(
                    icon = Icons.Rounded.WaterDrop,
                    text = "ploaie"
                )
            }
        }
    }
}

@Composable
private fun AdventureTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 27.dp, bottom = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "Alege-ți aventura",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun WeatherAdventureCard(
    tag: String,
    titleTop: String,
    titleBottom: String,
    description: String,
    score: String,
    accent: Color,
    scene: WeatherGameScene,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(27.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
            .clip(shape)
            .background(Color(0xFF071334))
            .border(BorderStroke(1.dp, accent.copy(alpha = 0.70f)), shape)
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (scene) {
                WeatherGameScene.CloudCatcher -> drawCloudCatcherCardArt()
                WeatherGameScene.WeatherMemory -> drawWeatherMemoryCardArt()
            }

            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFF06122E).copy(alpha = 0.32f),
                        Color(0xFF06122E).copy(alpha = 0.96f)
                    ),
                    startX = size.width * 0.38f,
                    endX = size.width
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.47f))

            Box(
                modifier = Modifier
                    .weight(0.53f)
                    .fillMaxHeight()
                    .padding(top = 34.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(accent.copy(alpha = 0.16f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        Text(
                            text = titleTop,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 29.sp,
                            lineHeight = 31.sp,
                            maxLines = 1
                        )
                        Text(
                            text = titleBottom,
                            color = accent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 33.sp,
                            lineHeight = 35.sp,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = description,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScoreMaxPill(
                        score = score,
                        accent = accent,
                        modifier = Modifier.weight(1f)
                    )
                    PlayRoundButton(
                        accent = accent,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreMaxPill(
    score: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF132246).copy(alpha = 0.76f))
            .border(
                BorderStroke(1.dp, Color.White.copy(alpha = 0.20f)),
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.EmojiEvents,
            contentDescription = null,
            tint = GamesYellow,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = "Scor maxim",
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = score,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun PlayRoundButton(
    accent: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        accent.copy(alpha = 0.34f),
                        Color(0xFF14235C).copy(alpha = 0.96f)
                    )
                )
            )
            .border(BorderStroke(1.4.dp, accent), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = "Porneste jocul",
            tint = Color.White,
            modifier = Modifier.size(38.dp)
        )
    }
}

@Composable
private fun NextGamePlaceholderRedesign() {
    val shape = RoundedCornerShape(27.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(shape)
            .background(Color(0xFF09172D).copy(alpha = 0.88f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)), shape)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1C2E52).copy(alpha = 0.78f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = GamesYellow,
                    modifier = Modifier.size(34.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = "Următorul joc",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1
                )
                Text(
                    text = "Slot pregătit pentru un nou mini-joc meteo.",
                    color = Color.White.copy(alpha = 0.66f),
                    fontSize = 14.sp,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }

            Box(
                modifier = Modifier.size(66.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.38f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width / 2f, size.height / 2f),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.58f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun WeatherGameChipRedesign(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .height(45.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(Color(0xFF18315F).copy(alpha = 0.82f))
            .border(
                BorderStroke(1.dp, Color(0xFF5572B5).copy(alpha = 0.52f)),
                RoundedCornerShape(19.dp)
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = when (icon) {
                Icons.Rounded.Bolt -> GamesYellow
                Icons.Rounded.WaterDrop -> GamesCyan
                else -> Color(0xFF70CBFF)
            },
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun CloudCatcherScoreBar(
    score: Int,
    lives: Int,
    remainingMillis: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GamesSurface.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, GamesBorder.copy(alpha = 0.52f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScorePill(
                    icon = Icons.Rounded.WbSunny,
                    label = "Scor",
                    value = score.toString(),
                    accent = GamesYellow,
                    modifier = Modifier.weight(1f)
                )
                ScorePill(
                    icon = Icons.Rounded.Favorite,
                    label = "Vieti",
                    value = lives.toString(),
                    accent = GamesRed,
                    modifier = Modifier.weight(1f)
                )
                ScorePill(
                    icon = Icons.Rounded.Cloud,
                    label = "Timp",
                    value = remainingMillis.secondsText(),
                    accent = GamesCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            LinearProgressIndicator(
                progress = { (remainingMillis / TotalGameMillis.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = GamesCyan,
                trackColor = Color.White.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
private fun ScorePill(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(58.dp)
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(accent.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                color = GamesMutedText,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CloudCatcherBoard(
    phase: CloudCatcherPhase,
    score: Int,
    lives: Int,
    remainingMillis: Long,
    planeY: Float,
    objects: List<CloudCatcherObject>,
    onPlaneYChange: (Float) -> Unit,
    onStartClick: () -> Unit
) {
    val boardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .clip(boardShape)
            .background(Color(0xFF1F78B7))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)), boardShape)
            .pointerInput(phase) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (phase == CloudCatcherPhase.Running && size.height > 0) {
                            onPlaneYChange(offset.y / size.height.toFloat())
                        }
                    },
                    onDrag = { change, _ ->
                        if (phase == CloudCatcherPhase.Running && size.height > 0) {
                            onPlaneYChange(change.position.y / size.height.toFloat())
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCloudCatcherScene(
                planeY = planeY,
                objects = objects,
                progress = gameProgress(remainingMillis)
            )
        }

        if (phase == CloudCatcherPhase.Running) {
            CloudCatcherControls(
                onUpClick = { onPlaneYChange(planeY - 0.08f) },
                onDownClick = { onPlaneYChange(planeY + 0.08f) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 14.dp, bottom = 14.dp)
            )
        }

        if (phase == CloudCatcherPhase.Ready || phase == CloudCatcherPhase.Finished) {
            CloudCatcherOverlay(
                phase = phase,
                score = score,
                lives = lives,
                onStartClick = onStartClick,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun CloudCatcherControls(
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onUpClick,
            modifier = Modifier
                .size(54.dp)
                .background(Color(0xFF082B46).copy(alpha = 0.78f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = "Sus",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
        IconButton(
            onClick = onDownClick,
            modifier = Modifier
                .size(54.dp)
                .background(Color(0xFF082B46).copy(alpha = 0.78f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Jos",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun CloudCatcherPauseDialog(
    onContinueClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dialogShape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.34f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(dialogShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF12314E).copy(alpha = 0.97f),
                            Color(0xFF071A2B).copy(alpha = 0.98f),
                            Color(0xFF03101D).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    BorderStroke(1.dp, Color(0xFF75C8FF).copy(alpha = 0.34f)),
                    dialogShape
                )
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    GamesYellow.copy(alpha = 0.42f),
                                    Color(0xFF153B58).copy(alpha = 0.88f)
                                )
                            )
                        )
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "II",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                }

                Text(
                    text = "Joc in pauza",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Doresti sa inchei sesiunea de joc?",
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onExitClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(
                            text = "Iesi din joc",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onContinueClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(22.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GamesYellow,
                            contentColor = Color(0xFF111827)
                        )
                    ) {
                        Text(
                            text = "Continua",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CloudCatcherOverlay(
    phase: CloudCatcherPhase,
    score: Int,
    lives: Int,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReady = phase == CloudCatcherPhase.Ready
    val lostLives = !isReady && lives <= 0
    val overlayShape = RoundedCornerShape(30.dp)
    val iconTint = when {
        isReady -> GamesCyan
        lostLives -> GamesRed
        else -> GamesYellow
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .clip(overlayShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF102B45).copy(alpha = 0.96f),
                        Color(0xFF071827).copy(alpha = 0.97f),
                        Color(0xFF03101D).copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                BorderStroke(1.dp, Color(0xFF75C8FF).copy(alpha = 0.34f)),
                overlayShape
            )
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                iconTint.copy(alpha = 0.46f),
                                Color(0xFF103A57).copy(alpha = 0.82f)
                            )
                        )
                    )
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isReady -> Icons.Rounded.Flight
                        lostLives -> Icons.Rounded.Bolt
                        else -> Icons.Rounded.EmojiEvents
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = if (isReady) "Cloud Catcher" else "Runda incheiata",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF071D39).copy(alpha = 0.72f))
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isReady) "Misiune" else "Scor final",
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Text(
                        text = if (isReady) "60s" else score.toString(),
                        color = if (isReady) GamesCyan else GamesYellow,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 38.sp,
                        lineHeight = 40.sp,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = if (isReady) {
                    "Nori albi +10, picaturi +5, fulgerele scad scorul, iar norii de furtuna iau o viata."
                } else if (lostLives) {
                    "Ai pierdut toate vietile. Evita norii de furtuna si fulgerele."
                } else {
                    "Timpul s-a incheiat. Ai colectat cat mai multe fenomene bune."
                },
                color = Color.White.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(22.dp),
                contentPadding = PaddingValues(horizontal = 18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isReady) GamesCyan else GamesYellow,
                    contentColor = Color(0xFF111827)
                )
            ) {
                Icon(
                    imageVector = if (isReady) Icons.Rounded.PlayArrow else Icons.Rounded.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = if (isReady) "Start joc" else "Joaca din nou",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun CloudCatcherLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GamesSurface.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, GamesBorder.copy(alpha = 0.52f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Fenomene in joc",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendItem(
                    icon = Icons.Rounded.Cloud,
                    title = "Nor alb",
                    value = "+10",
                    accent = Color.White,
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    icon = Icons.Rounded.WaterDrop,
                    title = "Picatura",
                    value = "+5",
                    accent = GamesCyan,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendItem(
                    icon = Icons.Rounded.Bolt,
                    title = "Fulger",
                    value = "-5",
                    accent = GamesRed,
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    icon = Icons.Rounded.Cloud,
                    title = "Nor negru",
                    value = "-1 viata",
                    accent = GamesRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    icon: ImageVector,
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(66.dp)
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(accent.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(23.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(
                text = value,
                color = GamesMutedText,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CloudCatcherLearningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GamesSurfaceSoft.copy(alpha = 0.86f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Lectia meteo",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(
                text = "Norii albi si picaturile reprezinta fenomene obisnuite. Fulgerele si norii foarte intunecati indica vreme severa si cer atentie.",
                color = GamesMutedText,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun CloudCatcherPreviewIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = GamesYellow.copy(alpha = 0.48f),
            radius = h * 0.18f,
            center = Offset(w * 0.80f, h * 0.28f)
        )
        drawCloud(
            center = Offset(w * 0.78f, h * 0.56f),
            radius = h * 0.12f,
            baseColor = Color.White.copy(alpha = 0.78f),
            shadowColor = Color(0xFFD8ECFF).copy(alpha = 0.66f)
        )
        drawLightningBolt(
            center = Offset(w * 0.88f, h * 0.69f),
            size = h * 0.13f,
            color = GamesYellow.copy(alpha = 0.88f)
        )
        drawPlane(
            center = Offset(w * 0.54f, h * 0.48f),
            unit = h * 0.085f
        )
        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    Color(0xFF061625).copy(alpha = 0.50f)
                )
            )
        )
    }
}

private fun DrawScope.drawWeatherGamesHeroArt() {
    val w = size.width
    val h = size.height

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF244DCE).copy(alpha = 0.62f),
                Color(0xFF0B143A).copy(alpha = 0.94f),
                Color(0xFF050B23)
            ),
            center = Offset(w * 0.82f, h * 0.36f),
            radius = w * 0.74f
        )
    )
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(
                Color(0xFF050A20),
                Color(0xFF071139).copy(alpha = 0.74f),
                Color.Transparent
            ),
            startX = 0f,
            endX = w * 0.68f
        )
    )

    drawSmallStars(
        color = Color(0xFFFF8AFF).copy(alpha = 0.74f),
        offsets = listOf(
            Offset(w * 0.58f, h * 0.18f),
            Offset(w * 0.65f, h * 0.30f),
            Offset(w * 0.88f, h * 0.15f),
            Offset(w * 0.52f, h * 0.38f),
            Offset(w * 0.94f, h * 0.44f)
        )
    )

    val globeCenter = Offset(w * 0.76f, h * 0.49f)
    val globeRadius = h * 0.29f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                Color(0xFF73E6FF),
                Color(0xFF1F82E8),
                Color(0xFF102678)
            ),
            center = Offset(globeCenter.x - globeRadius * 0.25f, globeCenter.y - globeRadius * 0.28f),
            radius = globeRadius * 1.25f
        ),
        radius = globeRadius,
        center = globeCenter
    )
    drawGlobeLand(globeCenter, globeRadius)

    drawSun(
        center = Offset(w * 0.88f, h * 0.30f),
        radius = h * 0.085f,
        color = GamesYellow
    )
    drawCloud(
        center = Offset(w * 0.70f, h * 0.26f),
        radius = h * 0.075f,
        baseColor = Color.White.copy(alpha = 0.92f),
        shadowColor = Color(0xFFC9D2E8).copy(alpha = 0.82f)
    )
    drawCloud(
        center = Offset(w * 0.90f, h * 0.43f),
        radius = h * 0.084f,
        baseColor = Color.White.copy(alpha = 0.90f),
        shadowColor = Color(0xFFC4CEEA).copy(alpha = 0.82f)
    )
    drawLightningBolt(
        center = Offset(w * 0.88f, h * 0.68f),
        size = h * 0.16f,
        color = Color(0xFFFFA646)
    )
    drawLine(
        color = Color(0xFF226BFF).copy(alpha = 0.90f),
        start = Offset(w * 0.49f, h * 0.62f),
        end = Offset(w * 0.18f, h * 0.77f),
        strokeWidth = 3.6f
    )
    drawPlane(
        center = Offset(w * 0.58f, h * 0.60f),
        unit = h * 0.058f
    )
}

private fun DrawScope.drawCloudCatcherFullScene(
    planeY: Float,
    objects: List<CloudCatcherObject>,
    progress: Float
) {
    val w = size.width
    val h = size.height

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF0A2546),
                Color(0xFF397FB5),
                Color(0xFF1A5C91),
                Color(0xFF082742),
                Color(0xFF03101F)
            )
        )
    )

    drawRect(
        brush = Brush.radialGradient(
            listOf(
                Color(0xFFFFF3B1).copy(alpha = 0.56f),
                Color(0xFF8CD6FF).copy(alpha = 0.18f),
                Color.Transparent
            ),
            center = Offset(w * 0.84f, h * 0.20f),
            radius = w * 0.72f
        )
    )
    drawRect(
        brush = Brush.radialGradient(
            listOf(
                Color(0xFF95E3FF).copy(alpha = 0.34f),
                Color.Transparent
            ),
            center = Offset(w * 0.50f, h * 0.45f),
            radius = w * 0.68f
        )
    )

    drawCloudCatcherRain(progress)
    drawDistantValley()

    drawSun(
        center = Offset(w * 0.87f, h * 0.22f),
        radius = min(w, h) * 0.090f,
        color = GamesYellow
    )

    drawCloud(
        center = Offset(w * 0.16f, h * 0.30f),
        radius = min(w, h) * 0.078f,
        baseColor = Color(0xFF6F8198).copy(alpha = 0.76f),
        shadowColor = Color(0xFF17263F).copy(alpha = 0.96f)
    )
    drawCloud(
        center = Offset(w * 0.48f, h * 0.22f),
        radius = min(w, h) * 0.066f,
        baseColor = Color.White.copy(alpha = 0.92f),
        shadowColor = Color(0xFFB8D6EA).copy(alpha = 0.74f)
    )
    drawCloud(
        center = Offset(w * 0.18f, h * 0.50f),
        radius = min(w, h) * 0.082f,
        baseColor = Color.White.copy(alpha = 0.84f),
        shadowColor = Color(0xFF8AA8C8).copy(alpha = 0.62f)
    )
    drawCloud(
        center = Offset(w * 0.82f, h * 0.44f),
        radius = min(w, h) * 0.086f,
        baseColor = Color(0xFF536173).copy(alpha = 0.82f),
        shadowColor = Color(0xFF151D2A).copy(alpha = 0.88f)
    )
    drawCloud(
        center = Offset(w * 0.18f, h * 0.70f),
        radius = min(w, h) * 0.070f,
        baseColor = Color.White.copy(alpha = 0.82f),
        shadowColor = Color(0xFF829EBB).copy(alpha = 0.58f)
    )

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color.Transparent,
                Color(0xFF03101E).copy(alpha = 0.38f),
                Color(0xFF03101E).copy(alpha = 0.82f)
            ),
            startY = h * 0.58f,
            endY = h
        )
    )

    objects.forEach { item ->
        drawCloudCatcherObject(item)
    }

    drawRearPlane(
        center = Offset(w * PlaneX, h * planeY),
        unit = min(w, h) * 0.115f
    )
}

private fun DrawScope.drawCloudCatcherInteractiveLayer(
    planeY: Float,
    objects: List<CloudCatcherObject>
) {
    objects.forEach { item ->
        drawCloudCatcherObject(item)
    }

    drawRearPlane(
        center = Offset(size.width * PlaneX, size.height * planeY),
        unit = min(size.width, size.height) * 0.118f
    )
}

private fun DrawScope.drawCloudCatcherRain(progress: Float) {
    val w = size.width
    val h = size.height
    val offset = progress * h * 0.32f

    for (index in 0 until 54) {
        val x = ((index * 43) % 520) / 520f * w
        val y = ((index * 79) % 920) / 920f * h + offset
        val start = Offset(x, y % h)
        val alpha = if (index % 3 == 0) 0.24f else 0.13f
        val length = if (index % 4 == 0) 74f else 48f
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = start,
            end = start + Offset(-18f, length),
            strokeWidth = if (index % 4 == 0) 2.0f else 1.2f
        )
    }
}

private fun DrawScope.drawDistantValley() {
    val w = size.width
    val h = size.height

    drawRect(
        brush = Brush.radialGradient(
            listOf(
                Color.White.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(w * 0.52f, h * 0.48f),
            radius = w * 0.52f
        )
    )

    val farMountains = Path().apply {
        moveTo(0f, h * 0.50f)
        lineTo(w * 0.14f, h * 0.35f)
        lineTo(w * 0.30f, h * 0.49f)
        lineTo(w * 0.47f, h * 0.31f)
        lineTo(w * 0.64f, h * 0.49f)
        lineTo(w * 0.83f, h * 0.34f)
        lineTo(w, h * 0.50f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(farMountains, Color(0xFF3E6F8F).copy(alpha = 0.56f))

    val midMountains = Path().apply {
        moveTo(0f, h * 0.66f)
        lineTo(w * 0.16f, h * 0.47f)
        lineTo(w * 0.30f, h * 0.63f)
        lineTo(w * 0.50f, h * 0.43f)
        lineTo(w * 0.68f, h * 0.64f)
        lineTo(w * 0.86f, h * 0.48f)
        lineTo(w, h * 0.62f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(midMountains, Color(0xFF224E68).copy(alpha = 0.72f))

    val nearMountains = Path().apply {
        moveTo(0f, h * 0.88f)
        lineTo(w * 0.18f, h * 0.62f)
        lineTo(w * 0.33f, h * 0.82f)
        lineTo(w * 0.50f, h * 0.60f)
        lineTo(w * 0.66f, h * 0.84f)
        lineTo(w * 0.86f, h * 0.64f)
        lineTo(w, h * 0.82f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(nearMountains, Color(0xFF0D314C).copy(alpha = 0.94f))

    val foregroundLeft = Path().apply {
        moveTo(0f, h)
        lineTo(w * 0.20f, h * 0.78f)
        lineTo(w * 0.39f, h)
        close()
    }
    drawPath(foregroundLeft, Color(0xFF06233B).copy(alpha = 0.96f))

    val foregroundRight = Path().apply {
        moveTo(w, h)
        lineTo(w * 0.78f, h * 0.78f)
        lineTo(w * 0.58f, h)
        close()
    }
    drawPath(foregroundRight, Color(0xFF061E35).copy(alpha = 0.96f))

    val river = Path().apply {
        moveTo(w * 0.52f, h * 0.49f)
        cubicTo(w * 0.44f, h * 0.58f, w * 0.50f, h * 0.68f, w * 0.42f, h)
        lineTo(w * 0.62f, h)
        cubicTo(w * 0.58f, h * 0.75f, w * 0.68f, h * 0.62f, w * 0.57f, h * 0.49f)
        close()
    }
    drawPath(
        path = river,
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF9CEEFF).copy(alpha = 0.40f),
                Color(0xFF2E9BD0).copy(alpha = 0.42f),
                Color(0xFF0D4D7B).copy(alpha = 0.64f)
            )
        )
    )

    drawLine(
        color = Color.White.copy(alpha = 0.16f),
        start = Offset(w * 0.52f, h * 0.51f),
        end = Offset(w * 0.47f, h * 0.82f),
        strokeWidth = 2.2f
    )
}

private fun DrawScope.drawRearPlane(
    center: Offset,
    unit: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                Color.White.copy(alpha = 0.10f),
                Color.Transparent
            ),
            center = center,
            radius = unit * 1.20f
        ),
        radius = unit * 1.20f,
        center = center
    )

    val planePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = unit * 1.25f
        typeface = Typeface.DEFAULT
        color = android.graphics.Color.WHITE
        setShadowLayer(
            unit * 0.10f,
            0f,
            unit * 0.08f,
            android.graphics.Color.argb(150, 4, 14, 28)
        )
    }
    val baseline = center.y - (planePaint.ascent() + planePaint.descent()) / 2f

    drawContext.canvas.nativeCanvas.apply {
        save()
        rotate(45f, center.x, center.y)
        drawText("✈️", center.x, baseline, planePaint)
        restore()
    }
}

private fun DrawScope.drawCloudCatcherCardArt() {
    val w = size.width
    val h = size.height

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF14376F),
                Color(0xFF14285A),
                Color(0xFF231843)
            )
        )
    )
    drawRect(
        brush = Brush.radialGradient(
            listOf(
                Color(0xFFFFA64D).copy(alpha = 0.32f),
                Color.Transparent
            ),
            center = Offset(w * 0.42f, h * 0.74f),
            radius = w * 0.42f
        )
    )

    drawCloud(
        center = Offset(w * 0.13f, h * 0.30f),
        radius = h * 0.105f,
        baseColor = Color(0xFF8EDCFF).copy(alpha = 0.78f),
        shadowColor = Color(0xFF203B6A).copy(alpha = 0.96f)
    )
    drawCloud(
        center = Offset(w * 0.17f, h * 0.72f),
        radius = h * 0.145f,
        baseColor = Color(0xFF9FE0FF).copy(alpha = 0.72f),
        shadowColor = Color(0xFF142B5A).copy(alpha = 0.96f)
    )
    drawCloud(
        center = Offset(w * 0.49f, h * 0.30f),
        radius = h * 0.09f,
        baseColor = Color(0xFF5E687C).copy(alpha = 0.90f),
        shadowColor = Color(0xFF242C3E).copy(alpha = 0.98f)
    )
    drawLightningBolt(
        center = Offset(w * 0.11f, h * 0.50f),
        size = h * 0.17f,
        color = Color(0xFF5DE2FF)
    )
    drawStar(
        center = Offset(w * 0.43f, h * 0.44f),
        radius = h * 0.075f,
        color = GamesYellow
    )
    drawRainDrop(
        center = Offset(w * 0.55f, h * 0.84f),
        radius = h * 0.055f,
        color = GamesCyan
    )
    drawPlane(
        center = Offset(w * 0.25f, h * 0.58f),
        unit = h * 0.075f
    )
    drawPlane(
        center = Offset(w * 0.40f, h * 0.29f),
        unit = h * 0.027f
    )
}

private fun DrawScope.drawWeatherMemoryCardArt() {
    val w = size.width
    val h = size.height

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF4F86A8),
                Color(0xFF1D5B59),
                Color(0xFF123C2C)
            )
        )
    )
    drawMountainRange(
        color = Color(0xFF173A4A).copy(alpha = 0.82f),
        baseY = h * 0.61f,
        peakY = h * 0.18f
    )
    drawMountainRange(
        color = Color(0xFF244E43).copy(alpha = 0.92f),
        baseY = h * 0.74f,
        peakY = h * 0.38f
    )
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color.Transparent,
                Color(0xFF143D2B).copy(alpha = 0.86f)
            )
        )
    )

    drawWeatherCapsule(
        center = Offset(w * 0.26f, h * 0.61f),
        width = w * 0.22f,
        height = h * 0.50f
    )
    drawCloud(
        center = Offset(w * 0.26f, h * 0.56f),
        radius = h * 0.062f,
        baseColor = Color.White.copy(alpha = 0.90f),
        shadowColor = Color(0xFFBFE8FF).copy(alpha = 0.78f)
    )
    drawControlTube(
        center = Offset(w * 0.09f, h * 0.66f),
        color = Color(0xFFFF7A3C)
    )
    drawControlTube(
        center = Offset(w * 0.43f, h * 0.61f),
        color = GamesCyan
    )
    drawControlTube(
        center = Offset(w * 0.51f, h * 0.62f),
        color = Color(0xFF4796FF)
    )
}

private fun DrawScope.drawWeatherMemoryHubArt() {
    val w = size.width
    val h = size.height
    val cardWidth = w * 0.18f
    val cardHeight = h * 0.50f

    fun drawMemoryMiniCard(
        center: Offset,
        rotationHint: Float,
        faceUp: Boolean,
        accent: Color,
        symbol: Int
    ) {
        val topLeft = Offset(center.x - cardWidth / 2f, center.y - cardHeight / 2f)
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.22f),
            topLeft = topLeft + Offset(w * 0.012f, h * 0.018f + rotationHint),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(cardWidth * 0.16f, cardWidth * 0.16f)
        )
        drawRoundRect(
            brush = if (faceUp) {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEAFBFF),
                        Color(0xFFBCE8FF)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF145A7B),
                        Color(0xFF0B2439),
                        Color(0xFF2D7B69)
                    )
                )
            },
            topLeft = topLeft,
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(cardWidth * 0.16f, cardWidth * 0.16f)
        )
        drawRoundRect(
            color = accent.copy(alpha = if (faceUp) 0.30f else 0.18f),
            topLeft = topLeft + Offset(cardWidth * 0.08f, cardHeight * 0.08f),
            size = Size(cardWidth * 0.84f, cardHeight * 0.84f),
            cornerRadius = CornerRadius(cardWidth * 0.12f, cardWidth * 0.12f),
            style = Stroke(width = cardWidth * 0.018f)
        )

        if (faceUp) {
            when (symbol) {
                0 -> drawCloud(
                    center = center,
                    radius = h * 0.040f,
                    baseColor = Color.White,
                    shadowColor = Color(0xFFB8D5E8)
                )
                1 -> drawSun(
                    center = center,
                    radius = h * 0.050f,
                    color = GamesYellow
                )
                else -> drawLightningBolt(
                    center = center,
                    size = h * 0.10f,
                    color = Color(0xFFFFC928)
                )
            }
        } else {
            drawContext.canvas.nativeCanvas.drawText(
                "?",
                center.x,
                center.y + h * 0.045f,
                Paint().apply {
                    color = android.graphics.Color.WHITE
                    textAlign = Paint.Align.CENTER
                    textSize = h * 0.24f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
            )
        }
    }

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF1B6F8D),
                Color(0xFF103B58),
                Color(0xFF081C33)
            )
        )
    )
    drawCircle(
        color = GamesGreen.copy(alpha = 0.22f),
        radius = w * 0.28f,
        center = Offset(w * 0.82f, h * 0.20f)
    )
    drawCircle(
        color = GamesYellow.copy(alpha = 0.18f),
        radius = w * 0.22f,
        center = Offset(w * 0.70f, h * 0.92f)
    )
    drawSmallStars(
        color = Color.White.copy(alpha = 0.68f),
        offsets = listOf(
            Offset(w * 0.57f, h * 0.18f),
            Offset(w * 0.91f, h * 0.18f),
            Offset(w * 0.84f, h * 0.82f),
            Offset(w * 0.63f, h * 0.78f)
        )
    )

    drawMemoryMiniCard(
        center = Offset(w * 0.70f, h * 0.39f),
        rotationHint = -h * 0.012f,
        faceUp = false,
        accent = GamesCyan,
        symbol = 0
    )
    drawMemoryMiniCard(
        center = Offset(w * 0.84f, h * 0.48f),
        rotationHint = h * 0.010f,
        faceUp = true,
        accent = GamesYellow,
        symbol = 1
    )
    drawMemoryMiniCard(
        center = Offset(w * 0.66f, h * 0.72f),
        rotationHint = h * 0.018f,
        faceUp = true,
        accent = GamesGreen,
        symbol = 0
    )
    drawMemoryMiniCard(
        center = Offset(w * 0.92f, h * 0.74f),
        rotationHint = -h * 0.008f,
        faceUp = true,
        accent = GamesYellow,
        symbol = 2
    )
}

private fun DrawScope.drawSmallStars(
    color: Color,
    offsets: List<Offset>
) {
    offsets.forEachIndexed { index, offset ->
        drawCircle(
            color = color.copy(alpha = if (index % 2 == 0) 0.82f else 0.54f),
            radius = if (index % 2 == 0) 2.3f else 1.6f,
            center = offset
        )
    }
}

private fun DrawScope.drawGlobeLand(
    center: Offset,
    radius: Float
) {
    val landColor = Color(0xFF98CF64).copy(alpha = 0.92f)
    val shadowLand = Color(0xFF3A985F).copy(alpha = 0.82f)

    drawOval(
        color = landColor,
        topLeft = Offset(center.x - radius * 0.40f, center.y - radius * 0.54f),
        size = Size(radius * 0.55f, radius * 0.38f)
    )
    drawOval(
        color = shadowLand,
        topLeft = Offset(center.x + radius * 0.05f, center.y - radius * 0.12f),
        size = Size(radius * 0.44f, radius * 0.76f)
    )
    drawOval(
        color = Color(0xFFE7CE58).copy(alpha = 0.80f),
        topLeft = Offset(center.x - radius * 0.26f, center.y + radius * 0.12f),
        size = Size(radius * 0.28f, radius * 0.44f)
    )
}

private fun DrawScope.drawSun(
    center: Offset,
    radius: Float,
    color: Color
) {
    repeat(12) { index ->
        val angle = (index / 12.0) * PI * 2.0
        val start = Offset(
            center.x + cos(angle).toFloat() * radius * 1.12f,
            center.y + sin(angle).toFloat() * radius * 1.12f
        )
        val end = Offset(
            center.x + cos(angle).toFloat() * radius * 1.58f,
            center.y + sin(angle).toFloat() * radius * 1.58f
        )
        drawLine(
            color = color.copy(alpha = 0.88f),
            start = start,
            end = end,
            strokeWidth = radius * 0.15f
        )
    }
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFFFF2A6), color, Color(0xFFFF9B2D)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    repeat(10) { index ->
        val angle = -PI / 2.0 + index * PI / 5.0
        val pointRadius = if (index % 2 == 0) radius else radius * 0.46f
        val point = Offset(
            center.x + cos(angle).toFloat() * pointRadius,
            center.y + sin(angle).toFloat() * pointRadius
        )
        if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    path.close()
    drawPath(path, color)
    drawCircle(
        color = Color.White.copy(alpha = 0.28f),
        radius = radius * 0.28f,
        center = Offset(center.x - radius * 0.16f, center.y - radius * 0.18f)
    )
}

private fun DrawScope.drawMountainRange(
    color: Color,
    baseY: Float,
    peakY: Float
) {
    val w = size.width
    val path = Path().apply {
        moveTo(0f, baseY)
        lineTo(w * 0.14f, peakY + (baseY - peakY) * 0.32f)
        lineTo(w * 0.28f, baseY * 0.82f)
        lineTo(w * 0.43f, peakY)
        lineTo(w * 0.62f, baseY * 0.78f)
        lineTo(w * 0.78f, peakY + (baseY - peakY) * 0.24f)
        lineTo(w, baseY * 0.76f)
        lineTo(w, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawWeatherCapsule(
    center: Offset,
    width: Float,
    height: Float
) {
    val topLeft = Offset(center.x - width / 2f, center.y - height / 2f)
    drawRoundRect(
        color = Color(0xFF0B1627).copy(alpha = 0.78f),
        topLeft = Offset(topLeft.x - width * 0.10f, topLeft.y + height * 0.72f),
        size = Size(width * 1.20f, height * 0.28f),
        cornerRadius = CornerRadius(width * 0.25f, width * 0.25f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.28f),
        topLeft = topLeft,
        size = Size(width, height),
        cornerRadius = CornerRadius(width * 0.34f, width * 0.34f)
    )
    drawRoundRect(
        color = Color(0xFF76E8FF).copy(alpha = 0.18f),
        topLeft = Offset(topLeft.x + width * 0.08f, topLeft.y + height * 0.10f),
        size = Size(width * 0.84f, height * 0.72f),
        cornerRadius = CornerRadius(width * 0.26f, width * 0.26f)
    )
    drawCircle(
        color = GamesCyan.copy(alpha = 0.38f),
        radius = width * 0.34f,
        center = Offset(center.x, center.y + height * 0.15f)
    )
}

private fun DrawScope.drawControlTube(
    center: Offset,
    color: Color
) {
    val tubeWidth = size.width * 0.045f
    val tubeHeight = size.height * 0.42f
    val topLeft = Offset(center.x - tubeWidth / 2f, center.y - tubeHeight / 2f)
    drawRoundRect(
        color = Color(0xFF10223A).copy(alpha = 0.82f),
        topLeft = Offset(topLeft.x - tubeWidth * 0.30f, topLeft.y - tubeWidth * 0.18f),
        size = Size(tubeWidth * 1.60f, tubeHeight * 1.08f),
        cornerRadius = CornerRadius(tubeWidth, tubeWidth)
    )
    drawRoundRect(
        color = color.copy(alpha = 0.72f),
        topLeft = Offset(topLeft.x + tubeWidth * 0.18f, topLeft.y + tubeHeight * 0.20f),
        size = Size(tubeWidth * 0.64f, tubeHeight * 0.62f),
        cornerRadius = CornerRadius(tubeWidth * 0.35f, tubeWidth * 0.35f)
    )
    drawCircle(
        color = color,
        radius = tubeWidth * 0.48f,
        center = Offset(center.x, topLeft.y + tubeHeight * 0.52f)
    )
}

private fun DrawScope.drawCloudCatcherScene(
    planeY: Float,
    objects: List<CloudCatcherObject>,
    progress: Float
) {
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF63C8FF),
                Color(0xFF2E91D3),
                Color(0xFF134C79)
            )
        )
    )

    drawCircle(
        color = GamesYellow.copy(alpha = 0.82f),
        radius = size.minDimension * 0.12f,
        center = Offset(size.width * 0.82f, size.height * 0.16f)
    )

    drawBackgroundCloud(
        x = 0.18f + progress * 0.06f,
        y = 0.18f,
        scale = 0.82f
    )
    drawBackgroundCloud(
        x = 0.70f - progress * 0.05f,
        y = 0.30f,
        scale = 0.64f
    )
    drawBackgroundCloud(
        x = 0.46f,
        y = 0.82f,
        scale = 0.52f
    )

    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color.Transparent,
                Color(0xFF082946).copy(alpha = 0.60f)
            )
        )
    )

    objects.forEach { item ->
        drawCloudCatcherObject(item)
    }

    drawPlane(
        center = Offset(size.width * PlaneX, size.height * planeY),
        unit = min(size.width, size.height) * 0.055f
    )
}

private fun DrawScope.drawBackgroundCloud(
    x: Float,
    y: Float,
    scale: Float
) {
    drawCloud(
        center = Offset(size.width * x, size.height * y),
        radius = size.minDimension * 0.055f * scale,
        baseColor = Color.White.copy(alpha = 0.18f),
        shadowColor = Color.White.copy(alpha = 0.08f)
    )
}

private fun DrawScope.drawCloudCatcherObject(item: CloudCatcherObject) {
    val center = Offset(size.width * item.x, size.height * item.y)
    val radius = size.minDimension * item.radius

    when (item.type) {
        CloudCatcherObjectType.WhiteCloud -> {
            drawCircle(
                color = Color.White.copy(alpha = 0.16f),
                radius = radius * 1.85f,
                center = center
            )
            drawCloud(
                center = center,
                radius = radius,
                baseColor = Color.White,
                shadowColor = Color(0xFFC7DDF2)
            )
        }

        CloudCatcherObjectType.RainDrop -> {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        GamesCyan.copy(alpha = 0.54f),
                        GamesCyan.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2.05f
                ),
                radius = radius * 2.05f,
                center = center
            )
            drawRainDrop(
                center = center,
                radius = radius,
                color = GamesCyan
            )
        }

        CloudCatcherObjectType.StormCloud -> {
            drawCircle(
                color = Color.Black.copy(alpha = 0.22f),
                radius = radius * 1.90f,
                center = center + Offset(radius * 0.08f, radius * 0.20f)
            )
            drawCloud(
                center = center,
                radius = radius,
                baseColor = Color(0xFF5B6675),
                shadowColor = Color(0xFF171C26)
            )
            drawLightningBolt(
                center = center + Offset(radius * 0.18f, radius * 0.94f),
                size = radius * 1.05f,
                color = Color(0xFFFFD34A)
            )
        }

        CloudCatcherObjectType.Lightning -> {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        GamesYellow.copy(alpha = 0.58f),
                        GamesYellow.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2.10f
                ),
                radius = radius * 2.10f,
                center = center
            )
            drawLightningBolt(
                center = center,
                size = radius * 1.82f,
                color = GamesYellow
            )
        }
    }
}

private fun DrawScope.drawPlane(
    center: Offset,
    unit: Float
) {
    val body = Path().apply {
        moveTo(center.x + unit * 2.0f, center.y)
        cubicTo(
            center.x + unit * 0.70f,
            center.y - unit * 0.70f,
            center.x - unit * 1.30f,
            center.y - unit * 0.50f,
            center.x - unit * 1.65f,
            center.y - unit * 0.08f
        )
        lineTo(center.x - unit * 1.65f, center.y + unit * 0.08f)
        cubicTo(
            center.x - unit * 1.30f,
            center.y + unit * 0.50f,
            center.x + unit * 0.70f,
            center.y + unit * 0.70f,
            center.x + unit * 2.0f,
            center.y
        )
        close()
    }
    drawPath(body, Color(0xFFFFF3D1))

    val wing = Path().apply {
        moveTo(center.x - unit * 0.15f, center.y)
        lineTo(center.x - unit * 0.90f, center.y + unit * 1.18f)
        lineTo(center.x + unit * 0.82f, center.y + unit * 0.38f)
        close()
    }
    drawPath(wing, GamesCyan)

    val topWing = Path().apply {
        moveTo(center.x - unit * 0.10f, center.y)
        lineTo(center.x - unit * 0.78f, center.y - unit * 0.96f)
        lineTo(center.x + unit * 0.72f, center.y - unit * 0.30f)
        close()
    }
    drawPath(topWing, Color(0xFF7BE6FF))

    val tail = Path().apply {
        moveTo(center.x - unit * 1.28f, center.y - unit * 0.26f)
        lineTo(center.x - unit * 1.78f, center.y - unit * 1.0f)
        lineTo(center.x - unit * 0.92f, center.y - unit * 0.38f)
        close()
    }
    drawPath(tail, GamesYellow)

    drawCircle(
        color = Color(0xFF173A5E),
        radius = unit * 0.22f,
        center = Offset(center.x + unit * 0.70f, center.y - unit * 0.06f)
    )
}

private fun DrawScope.drawCloud(
    center: Offset,
    radius: Float,
    baseColor: Color,
    shadowColor: Color
) {
    drawRoundRect(
        color = shadowColor,
        topLeft = Offset(center.x - radius * 1.52f, center.y - radius * 0.10f),
        size = Size(radius * 3.04f, radius * 0.98f),
        cornerRadius = CornerRadius(radius * 0.50f, radius * 0.50f)
    )
    drawCircle(
        color = baseColor.copy(alpha = 0.96f),
        radius = radius * 0.84f,
        center = Offset(center.x - radius * 0.74f, center.y)
    )
    drawCircle(
        color = baseColor,
        radius = radius,
        center = Offset(center.x, center.y - radius * 0.24f)
    )
    drawCircle(
        color = baseColor.copy(alpha = 0.94f),
        radius = radius * 0.72f,
        center = Offset(center.x + radius * 0.78f, center.y + radius * 0.04f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.22f),
        radius = radius * 0.38f,
        center = Offset(center.x - radius * 0.18f, center.y - radius * 0.58f)
    )
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.10f),
        topLeft = Offset(center.x - radius * 1.30f, center.y + radius * 0.32f),
        size = Size(radius * 2.60f, radius * 0.42f),
        cornerRadius = CornerRadius(radius * 0.22f, radius * 0.22f)
    )
}

private fun DrawScope.drawRainDrop(
    center: Offset,
    radius: Float,
    color: Color
) {
    val drop = Path().apply {
        moveTo(center.x, center.y - radius * 1.35f)
        cubicTo(
            center.x - radius * 1.0f,
            center.y - radius * 0.34f,
            center.x - radius * 0.84f,
            center.y + radius * 0.84f,
            center.x,
            center.y + radius * 1.06f
        )
        cubicTo(
            center.x + radius * 0.84f,
            center.y + radius * 0.84f,
            center.x + radius * 1.0f,
            center.y - radius * 0.34f,
            center.x,
            center.y - radius * 1.35f
        )
        close()
    }
    drawPath(
        path = drop,
        brush = Brush.radialGradient(
            listOf(
                Color.White.copy(alpha = 0.88f),
                color,
                Color(0xFF1E88FF)
            ),
            center = Offset(center.x - radius * 0.20f, center.y - radius * 0.36f),
            radius = radius * 1.70f
        )
    )
    drawPath(
        path = drop,
        color = Color.White.copy(alpha = 0.42f),
        style = Stroke(width = radius * 0.11f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.70f),
        radius = radius * 0.18f,
        center = Offset(center.x - radius * 0.26f, center.y - radius * 0.28f)
    )
}

private fun DrawScope.drawLightningBolt(
    center: Offset,
    size: Float,
    color: Color
) {
    val bolt = Path().apply {
        moveTo(center.x - size * 0.12f, center.y - size * 0.64f)
        lineTo(center.x + size * 0.42f, center.y - size * 0.64f)
        lineTo(center.x + size * 0.08f, center.y - size * 0.06f)
        lineTo(center.x + size * 0.46f, center.y - size * 0.06f)
        lineTo(center.x - size * 0.22f, center.y + size * 0.72f)
        lineTo(center.x - size * 0.03f, center.y + size * 0.16f)
        lineTo(center.x - size * 0.42f, center.y + size * 0.16f)
        close()
    }
    drawPath(bolt, color)
}

private fun CloudCatcherObject.collidesWithPlane(planeY: Float): Boolean {
    val dx = x - PlaneX
    val dy = y - planeY
    val hitRadius = radius + 0.070f
    return dx * dx + dy * dy <= hitRadius * hitRadius
}

private fun createCloudCatcherObject(
    id: Long,
    random: Random,
    progress: Float
): CloudCatcherObject {
    val roll = random.nextFloat()
    val type = when {
        roll < 0.42f -> CloudCatcherObjectType.WhiteCloud
        roll < 0.70f -> CloudCatcherObjectType.RainDrop
        roll < 0.86f -> CloudCatcherObjectType.Lightning
        else -> CloudCatcherObjectType.StormCloud
    }

    val baseSpeed = when (type) {
        CloudCatcherObjectType.WhiteCloud -> 0.18f
        CloudCatcherObjectType.RainDrop -> 0.22f
        CloudCatcherObjectType.StormCloud -> 0.20f
        CloudCatcherObjectType.Lightning -> 0.26f
    }

    val radius = when (type) {
        CloudCatcherObjectType.WhiteCloud -> 0.074f
        CloudCatcherObjectType.RainDrop -> 0.050f
        CloudCatcherObjectType.StormCloud -> 0.082f
        CloudCatcherObjectType.Lightning -> 0.062f
    }

    return CloudCatcherObject(
        id = id,
        type = type,
        x = 1.12f,
        y = random.nextFloat().coerceIn(0f, 1f) * 0.46f + 0.18f,
        speed = baseSpeed + random.nextFloat() * 0.06f + progress * 0.08f,
        radius = radius
    )
}

private fun initialCloudCatcherObjects(): List<CloudCatcherObject> {
    return listOf(
        CloudCatcherObject(
            id = 0L,
            type = CloudCatcherObjectType.WhiteCloud,
            x = 0.50f,
            y = 0.24f,
            speed = 0.18f,
            radius = 0.074f
        ),
        CloudCatcherObject(
            id = 1L,
            type = CloudCatcherObjectType.RainDrop,
            x = 0.72f,
            y = 0.34f,
            speed = 0.23f,
            radius = 0.050f
        ),
        CloudCatcherObject(
            id = 2L,
            type = CloudCatcherObjectType.WhiteCloud,
            x = 0.25f,
            y = 0.43f,
            speed = 0.18f,
            radius = 0.078f
        ),
        CloudCatcherObject(
            id = 3L,
            type = CloudCatcherObjectType.Lightning,
            x = 0.66f,
            y = 0.44f,
            speed = 0.26f,
            radius = 0.064f
        ),
        CloudCatcherObject(
            id = 4L,
            type = CloudCatcherObjectType.StormCloud,
            x = 0.86f,
            y = 0.43f,
            speed = 0.20f,
            radius = 0.084f
        )
    )
}

private fun spawnInterval(remainingMillis: Long): Float {
    val progress = gameProgress(remainingMillis)
    return (0.92f - progress * 0.34f).coerceAtLeast(0.46f)
}

private fun gameProgress(remainingMillis: Long): Float {
    return (1f - remainingMillis / TotalGameMillis.toFloat()).coerceIn(0f, 1f)
}

private fun Long.secondsText(): String {
    return "${ceil(this / 1000.0).toInt().coerceAtLeast(0)}s"
}

private val Size.minDimension: Float
    get() = min(width, height)
