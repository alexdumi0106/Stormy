package com.example.weathersimulator.ui.screens.simulator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weathersimulator.sensors.pressure.PressureTrend
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Size
import kotlin.math.sin

internal fun isStormWeatherDescription(weatherDescription: String): Boolean {
    return weatherDescription.contains("Furtun", ignoreCase = true)
}

internal fun isRainWeatherDescription(weatherDescription: String): Boolean {
    return weatherDescription.contains("Ploaie", ignoreCase = true)
}

internal fun isSnowWeatherDescription(weatherDescription: String): Boolean {
    return weatherDescription.contains("Ninsoare", ignoreCase = true)
}

internal fun isFogWeatherDescription(weatherDescription: String): Boolean {
    return weatherDescription.contains("Cea", ignoreCase = true)
}

@Composable
fun WeatherScene(
    cloudCoverage: Float,
    isStormy: Boolean,
    pressureTrend: PressureTrend,
    windSpeed: Float,
    humidity: Float,
    weatherDescription: String
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AppleWeatherBaseSky(
            weatherDescription = weatherDescription,
            cloudCoverage = cloudCoverage
        )

        AppleWeatherMoodLayer(
            weatherDescription = weatherDescription,
            cloudCoverage = cloudCoverage,
            humidity = humidity,
            windSpeed = windSpeed
        )

        RainLayer(
            weatherDescription = weatherDescription,
            windSpeed = windSpeed
        )

        SnowLayer(
            weatherDescription = weatherDescription,
            windSpeed = windSpeed
        )

        LightningLayer(
            isStormy = isStormy
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSoftSunRay(
    center: Offset,
    angle: Float,
    inner: Float,
    outer: Float,
    baseHalfWidth: Float,
    alpha: Float,
    warm: Boolean
) {
    val dx = kotlin.math.cos(angle)
    val dy = kotlin.math.sin(angle)
    val px = -dy
    val py = dx
    val base = Offset(center.x + dx * inner, center.y + dy * inner)
    val tip = Offset(center.x + dx * outer, center.y + dy * outer)
    val c1 = Offset(center.x + dx * (inner + outer * 0.30f), center.y + dy * (inner + outer * 0.30f))
    val c2 = Offset(center.x + dx * (inner + outer * 0.62f), center.y + dy * (inner + outer * 0.62f))
    val tipHalfWidth = baseHalfWidth * 0.06f

    val rayPath = Path().apply {
        moveTo(base.x + px * baseHalfWidth, base.y + py * baseHalfWidth)
        cubicTo(
            c1.x + px * baseHalfWidth * 0.72f,
            c1.y + py * baseHalfWidth * 0.72f,
            c2.x + px * baseHalfWidth * 0.24f,
            c2.y + py * baseHalfWidth * 0.24f,
            tip.x + px * tipHalfWidth,
            tip.y + py * tipHalfWidth
        )
        cubicTo(
            c2.x - px * baseHalfWidth * 0.18f,
            c2.y - py * baseHalfWidth * 0.18f,
            c1.x - px * baseHalfWidth * 0.68f,
            c1.y - py * baseHalfWidth * 0.68f,
            base.x - px * baseHalfWidth,
            base.y - py * baseHalfWidth
        )
        close()
    }

    drawPath(
        path = rayPath,
        brush = Brush.radialGradient(
            colors = listOf(
                if (warm) Color(0xFFFFF3D3).copy(alpha = alpha) else Color.White.copy(alpha = alpha),
                Color.White.copy(alpha = alpha * 0.30f),
                Color.Transparent
            ),
            center = center,
            radius = outer
        )
    )
}

private enum class AppleCloudStyle {
    CLEAR,
    CIRRUS,
    CIRROCUMULUS,
    CUMULUS,
    STRATOCUMULUS,
    STRATUS,
    RAIN,
    STORM,
    SNOW,
    FOG
}

private fun appleCloudStyleFor(
    weatherDescription: String,
    cloudCoverage: Float
): AppleCloudStyle {
    val description = weatherDescription.lowercase()

    return when {
        isStormWeatherDescription(weatherDescription) -> AppleCloudStyle.STORM
        isRainWeatherDescription(weatherDescription) -> AppleCloudStyle.RAIN
        isSnowWeatherDescription(weatherDescription) -> AppleCloudStyle.SNOW
        isFogWeatherDescription(weatherDescription) -> AppleCloudStyle.FOG
        description.contains("predominant") && description.contains("nor") -> AppleCloudStyle.STRATOCUMULUS
        description.contains("noros") || cloudCoverage >= 95f -> AppleCloudStyle.STRATUS
        description.contains("nori") && description.contains("soare") -> AppleCloudStyle.CUMULUS
        description.contains("par") || cloudCoverage in 35f..55f -> AppleCloudStyle.CIRROCUMULUS
        description.contains("predominant") || cloudCoverage in 10f..35f -> AppleCloudStyle.CIRRUS
        else -> AppleCloudStyle.CLEAR
    }
}

@Composable
private fun AppleWeatherBaseSky(
    weatherDescription: String,
    cloudCoverage: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val style = appleCloudStyleFor(weatherDescription, cloudCoverage)

        val skyColors = when (style) {
            AppleCloudStyle.CLEAR,
            AppleCloudStyle.CIRRUS,
            AppleCloudStyle.CIRROCUMULUS -> listOf(
                Color(0xFF4E91D5),
                Color(0xFF1D67B7),
                Color(0xFF67B6E8)
            )

            AppleCloudStyle.CUMULUS -> listOf(
                Color(0xFF8FC7EC),
                Color(0xFF5EA9DE),
                Color(0xFFA9D7F1)
            )

            AppleCloudStyle.STRATOCUMULUS -> listOf(
                Color(0xFFA7C4DB),
                Color(0xFF78A9CF),
                Color(0xFFBBD4E8)
            )

            AppleCloudStyle.STRATUS,
            AppleCloudStyle.RAIN,
            AppleCloudStyle.FOG -> listOf(
                Color(0xFFBBC8D2),
                Color(0xFF879BAA),
                Color(0xFFB8C8D4)
            )

            AppleCloudStyle.STORM -> listOf(
                Color(0xFF8A9398),
                Color(0xFF566168),
                Color(0xFF9BA4A9)
            )

            AppleCloudStyle.SNOW -> listOf(
                Color(0xFFB8C7D6),
                Color(0xFF8EA7BC),
                Color(0xFFE5EEF6)
            )
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = skyColors,
                startY = 0f,
                endY = h
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.08f),
                radius = w * 0.75f
            )
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent,
                    Color.White.copy(alpha = 0.06f)
                )
            )
        )
    }
}

@Composable
private fun AppleWeatherMoodLayer(
    weatherDescription: String,
    cloudCoverage: Float,
    humidity: Float,
    windSpeed: Float
) {
    val style = appleCloudStyleFor(weatherDescription, cloudCoverage)
    val usesStaticSunnyClouds = style == AppleCloudStyle.CIRRUS ||
        style == AppleCloudStyle.CIRROCUMULUS ||
        style == AppleCloudStyle.CUMULUS
    val drift = if (usesStaticSunnyClouds) {
        0.18f
    } else {
        val infinite = rememberInfiniteTransition(label = "appleMood")
        val animatedDrift = infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (36000 - windSpeed.toInt() * 120).coerceIn(14000, 36000),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "cloudDrift"
        )
        animatedDrift.value
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val isRain = isRainWeatherDescription(weatherDescription)
        val isStorm = isStormWeatherDescription(weatherDescription)
        val isStormWithSun = isStorm && weatherDescription.lowercase().contains("soare")
        val isSnow = isSnowWeatherDescription(weatherDescription)
        val isFog = isFogWeatherDescription(weatherDescription)
        val hasVisibleSun = style in setOf(
            AppleCloudStyle.CLEAR,
            AppleCloudStyle.CIRRUS,
            AppleCloudStyle.CIRROCUMULUS,
            AppleCloudStyle.CUMULUS,
            AppleCloudStyle.STRATOCUMULUS
        ) || isStormWithSun

        if (hasVisibleSun) {
            val sunCenter = Offset(w * 0.21f, h * 0.14f)
            val sunAlpha = when (style) {
                AppleCloudStyle.CLEAR -> 0.98f
                AppleCloudStyle.CIRRUS -> 0.94f
                AppleCloudStyle.CIRROCUMULUS -> 0.92f
                AppleCloudStyle.CUMULUS -> 0.78f
                AppleCloudStyle.STRATOCUMULUS -> 0.58f
                AppleCloudStyle.STORM -> 0.42f
                else -> 0f
            }

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = sunAlpha * 0.34f),
                        Color.White.copy(alpha = sunAlpha * 0.16f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = w * 0.40f
                )
            )

            repeat(10) { ray ->
                val angle = (ray * 36f) * 0.017453292f
                val isLongRay = ray % 2 == 0
                val inner = w * 0.050f
                val outer = w * if (isLongRay) 0.215f else 0.145f
                drawSoftSunRay(
                    center = sunCenter,
                    angle = angle,
                    inner = inner,
                    outer = outer,
                    baseHalfWidth = w * if (isLongRay) 0.018f else 0.010f,
                    alpha = sunAlpha * if (isLongRay) 0.125f else 0.070f,
                    warm = false
                )
            }

            repeat(6) { ray ->
                val angle = (ray * 60f + 18f) * 0.017453292f
                val inner = w * 0.060f
                val outer = w * 0.190f
                drawSoftSunRay(
                    center = sunCenter,
                    angle = angle,
                    inner = inner,
                    outer = outer,
                    baseHalfWidth = w * 0.020f,
                    alpha = sunAlpha * 0.052f,
                    warm = true
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = sunAlpha),
                        Color.White.copy(alpha = sunAlpha * 0.46f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = w * 0.145f
                ),
                radius = w * 0.145f,
                center = sunCenter
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = sunAlpha),
                        Color.White.copy(alpha = sunAlpha),
                        Color.White.copy(alpha = sunAlpha * 0.92f),
                        Color.White.copy(alpha = sunAlpha * 0.16f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = w * 0.100f
                ),
                radius = w * 0.100f,
                center = sunCenter
            )

            drawCircle(
                color = Color.White.copy(alpha = sunAlpha),
                radius = w * 0.040f,
                center = sunCenter
            )

            repeat(5) { i ->
                drawCircle(
                    color = Color.White.copy(alpha = (0.016f - i * 0.0025f) * sunAlpha),
                    radius = w * (0.018f + i * 0.012f),
                    center = Offset(
                        x = w * (0.28f + i * 0.050f),
                        y = h * (0.20f + i * 0.070f)
                    )
                )
            }
        }

        when (style) {
            AppleCloudStyle.CLEAR -> Unit
            AppleCloudStyle.CIRRUS -> {
                drawPhotoLikePredominantSunnyClouds(drift, w, h, alpha = 0.78f)
            }
            AppleCloudStyle.CIRROCUMULUS -> {
                drawCirrusRichPartialSunnyClouds(drift, w, h, alpha = 0.92f)
            }
            AppleCloudStyle.CUMULUS -> {
                drawDensePartialSunnyClouds(drift, w, h, alpha = 0.92f)
            }
            AppleCloudStyle.STRATOCUMULUS -> {
                drawPredominantlyCloudyPuffyField(drift, w, h, alpha = 0.76f)
            }
            AppleCloudStyle.STRATUS -> drawOvercastPuffyField(drift, w, h, alpha = 0.78f)
            AppleCloudStyle.RAIN -> {
                drawOvercastPuffyField(drift, w, h, alpha = 0.74f)
            }
            AppleCloudStyle.STORM -> {
                drawOvercastPuffyField(drift, w, h, alpha = 0.84f)
                drawStormCloudShadow(w, h, alpha = 0.20f)
            }
            AppleCloudStyle.SNOW,
            AppleCloudStyle.FOG -> drawStratusClouds(drift, w, h, alpha = 0.60f)
        }

        if (isStorm) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF343A3F).copy(alpha = 0.34f),
                        Color(0xFF4E565C).copy(alpha = 0.10f),
                        Color(0xFF2E3439).copy(alpha = 0.24f)
                    )
                )
            )
        }

        if (isStormWithSun) {
            drawStormSunBreak(w, h, alpha = 0.72f)
        }

        if (isRain || isStorm) {
            repeat(28) { i ->
                val x = (i * 47f) % w
                val y = (i * 91f) % h
                drawCircle(
                    color = Color.White.copy(alpha = 0.055f),
                    radius = 3f + (i % 4) * 2.5f,
                    center = Offset(x, y)
                )
            }
        }

        if (isSnow || isFog) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isFog) 0.34f else 0.18f),
                        Color.White.copy(alpha = if (isFog) 0.20f else 0.10f),
                        Color.Transparent
                    )
                )
            )
        }

        if (cloudCoverage >= 80f && !isRain && !isStorm) {
            drawRect(
                color = Color(0xFF546575).copy(alpha = if (style == AppleCloudStyle.STRATUS) 0.22f else 0.12f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPredominantSunnyClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawMilkyCirrusPatch(
        center = Offset(w * 0.30f + drift * w * 0.025f, h * 0.105f),
        width = w * 0.62f,
        height = h * 0.16f,
        alpha = alpha * 0.74f,
        slant = -0.028f
    )

    drawMilkyCirrusPatch(
        center = Offset(w * 0.76f + drift * w * 0.040f, h * 0.175f),
        width = w * 0.68f,
        height = h * 0.17f,
        alpha = alpha * 0.70f,
        slant = -0.018f
    )

    drawMilkyCirrusPatch(
        center = Offset(w * 0.18f + drift * w * 0.030f, h * 0.285f),
        width = w * 0.42f,
        height = h * 0.13f,
        alpha = alpha * 0.48f,
        slant = -0.015f
    )

    drawCirrusStreamer(
        start = Offset(w * -0.08f + drift * w * 0.030f, h * 0.055f),
        length = w * 0.58f,
        lift = h * 0.030f,
        alpha = alpha * 0.70f,
        strands = 7
    )

    drawCirrusStreamer(
        start = Offset(w * 0.44f + drift * w * 0.045f, h * 0.205f),
        length = w * 0.68f,
        lift = h * 0.040f,
        alpha = alpha * 0.64f,
        strands = 6
    )

    drawCirrusStreamer(
        start = Offset(w * 0.56f + drift * w * 0.030f, h * 0.285f),
        length = w * 0.48f,
        lift = h * 0.026f,
        alpha = alpha * 0.42f,
        strands = 4
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPhotoLikePredominantSunnyClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.08f),
                Color.Transparent
            ),
            center = Offset(w * 0.15f, h * 0.14f),
            radius = w * 0.58f
        )
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.12f + drift * w * 0.014f, h * 0.245f),
        width = w * 1.08f,
        height = h * 0.34f,
        alpha = alpha * 0.86f,
        sweep = -0.18f,
        interiorGlow = 1.28f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.20f + drift * w * 0.018f, h * 0.105f),
        width = w * 0.92f,
        height = h * 0.27f,
        alpha = alpha * 0.74f,
        sweep = -0.12f,
        interiorGlow = 1.24f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.28f + drift * w * 0.012f, h * 0.085f),
        width = w * 0.86f,
        height = h * 0.20f,
        alpha = alpha * 0.34f,
        sweep = -0.12f,
        interiorGlow = 1.22f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.06f + drift * w * 0.015f, h * 0.178f),
        width = w * 0.84f,
        height = h * 0.22f,
        alpha = alpha * 0.46f,
        sweep = -0.14f,
        interiorGlow = 1.24f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.52f + drift * w * 0.020f, h * 0.230f),
        width = w * 0.64f,
        height = h * 0.18f,
        alpha = alpha * 0.44f,
        sweep = -0.08f,
        interiorGlow = 1.22f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.42f + drift * w * 0.018f, h * 0.170f),
        width = w * 0.74f,
        height = h * 0.20f,
        alpha = alpha * 0.38f,
        sweep = -0.11f,
        interiorGlow = 1.20f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.12f + drift * w * 0.014f, h * 0.278f),
        width = w * 0.96f,
        height = h * 0.21f,
        alpha = alpha * 0.44f,
        sweep = -0.10f,
        interiorGlow = 1.22f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.20f + drift * w * 0.011f, h * 0.190f),
        width = w * 0.82f,
        height = h * 0.18f,
        alpha = alpha * 0.34f,
        sweep = -0.13f,
        interiorGlow = 1.18f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.62f + drift * w * 0.019f, h * 0.075f),
        width = w * 0.70f,
        height = h * 0.19f,
        alpha = alpha * 0.32f,
        sweep = -0.10f,
        interiorGlow = 1.18f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.62f + drift * w * 0.015f, h * 0.320f),
        width = w * 0.62f,
        height = h * 0.14f,
        alpha = alpha * 0.26f,
        sweep = -0.06f,
        interiorGlow = 1.14f,
        showSoftKnots = false,
        detail = 0
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPaintedCirrusPlume(
    start: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    sweep: Float,
    interiorGlow: Float = 1f,
    showSoftKnots: Boolean = true,
    detail: Int = 1
) {
    val isLightDetail = detail <= 0
    val ribbonCount = if (isLightDetail) 5 else 7
    repeat(ribbonCount) { ribbon ->
        val t = if (ribbonCount == 1) 0f else ribbon / (ribbonCount - 1f)
        val ribbonStart = Offset(
            x = start.x + width * (0.02f + t * 0.030f),
            y = start.y + height * (0.10f + t * 0.095f)
        )
        val ribbonLength = width * (0.70f + (ribbon % 3) * 0.08f)
        val ribbonLift = height * (0.34f + t * 0.40f)
        val ribbonThickness = height * (0.10f + (ribbon % 2) * 0.030f)

        val plume = Path().apply {
            moveTo(ribbonStart.x, ribbonStart.y)
            cubicTo(
                ribbonStart.x + ribbonLength * 0.24f,
                ribbonStart.y - ribbonLift * 0.65f,
                ribbonStart.x + ribbonLength * 0.58f,
                ribbonStart.y + height * sweep - ribbonLift * 0.52f,
                ribbonStart.x + ribbonLength,
                ribbonStart.y + height * sweep - ribbonLift * 0.18f
            )
            cubicTo(
                ribbonStart.x + ribbonLength * 0.72f,
                ribbonStart.y + ribbonThickness * 0.44f,
                ribbonStart.x + ribbonLength * 0.34f,
                ribbonStart.y + ribbonThickness * 0.60f,
                ribbonStart.x,
                ribbonStart.y + ribbonThickness * 0.22f
            )
            close()
        }

        drawPath(
            path = plume,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * (if (isLightDetail) 0.105f else 0.080f) * interiorGlow),
                    Color(0xFFF4FAFF).copy(alpha = alpha * (if (isLightDetail) 0.215f else 0.178f) * interiorGlow),
                    Color.White.copy(alpha = alpha * (if (isLightDetail) 0.072f else 0.052f) * interiorGlow),
                    Color.Transparent
                )
            )
        )

        if (isLightDetail) {
            val denseCenter = Path().apply {
                moveTo(ribbonStart.x + ribbonLength * 0.22f, ribbonStart.y - ribbonLift * 0.33f)
                cubicTo(
                    ribbonStart.x + ribbonLength * 0.38f,
                    ribbonStart.y - ribbonLift * 0.62f,
                    ribbonStart.x + ribbonLength * 0.58f,
                    ribbonStart.y - ribbonLift * 0.44f,
                    ribbonStart.x + ribbonLength * 0.72f,
                    ribbonStart.y - ribbonLift * 0.24f
                )
            }

            drawPath(
                path = denseCenter,
                color = Color.White.copy(alpha = alpha * 0.092f * interiorGlow),
                style = Stroke(
                    width = height * 0.010f,
                    cap = StrokeCap.Round
                )
            )
        }

        val gapCount = if (isLightDetail) 2 else 3
        repeat(gapCount) { gap ->
            val g = if (gapCount == 1) 0f else gap / (gapCount - 1f)
            val gapStartX = ribbonStart.x + ribbonLength * (0.18f + g * 0.16f)
            val gapStartY = ribbonStart.y + ribbonThickness * (g - 0.5f) * 0.70f
            val gapLength = ribbonLength * (0.46f + gap * 0.08f)
            val gapLift = ribbonLift * (0.28f + g * 0.16f)

            val gapPath = Path().apply {
                moveTo(gapStartX, gapStartY)
                cubicTo(
                    gapStartX + gapLength * 0.28f,
                    gapStartY - gapLift,
                    gapStartX + gapLength * 0.62f,
                    gapStartY - gapLift * 0.10f,
                    gapStartX + gapLength,
                    gapStartY - gapLift * 0.36f
                )
            }

            drawPath(
                path = gapPath,
                color = Color(0xFF5FA7DE).copy(alpha = alpha * (0.030f + g * 0.018f)),
                style = Stroke(
                    width = width * (0.0080f - g * 0.0020f),
                    cap = StrokeCap.Round
                )
            )
        }

        if (!showSoftKnots) {
            val cutCount = if (detail <= 0) 1 else 3
            repeat(cutCount) { cut ->
                val c = if (cutCount == 1) 0f else cut / (cutCount - 1f)
                val cutStartX = ribbonStart.x + ribbonLength * (0.20f + c * 0.20f)
                val cutStartY = ribbonStart.y +
                    ribbonThickness * (c - 0.45f) * 0.42f -
                    ribbonLift * (0.16f + c * 0.10f)
                val cutLength = ribbonLength * (0.34f + cut * 0.10f)
                val cutLift = ribbonLift * (0.20f + c * 0.12f)

                val cutPath = Path().apply {
                    moveTo(cutStartX, cutStartY)
                    cubicTo(
                        cutStartX + cutLength * 0.28f,
                        cutStartY - cutLift + sin((cut + ribbon).toFloat()) * ribbonThickness * 0.18f,
                        cutStartX + cutLength * 0.64f,
                        cutStartY + cutLift * 0.20f,
                        cutStartX + cutLength,
                        cutStartY - cutLift * 0.26f
                    )
                }

                drawPath(
                    path = cutPath,
                    color = Color(0xFF4F98D0).copy(alpha = alpha * (0.026f + c * 0.018f)),
                    style = Stroke(
                        width = width * (0.0062f - c * 0.0014f),
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        val filamentCount = if (isLightDetail) 3 else 4
        repeat(filamentCount) { filament ->
            val f = if (filamentCount == 1) 0f else filament / (filamentCount - 1f)
            val fx = ribbonStart.x + ribbonLength * (0.08f + f * 0.06f)
            val fy = ribbonStart.y + ribbonThickness * (f - 0.5f) * 0.55f
            val filamentLength = ribbonLength * (0.70f + (filament % 2) * 0.14f)
            val filamentLift = ribbonLift * (0.46f + f * 0.30f)

            val path = Path().apply {
                moveTo(fx, fy)
                cubicTo(
                    fx + filamentLength * 0.26f,
                    fy - filamentLift * 0.70f,
                    fx + filamentLength * 0.58f,
                    fy - filamentLift * 0.22f,
                    fx + filamentLength,
                    fy - filamentLift * 0.42f
                )
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = alpha * (if (isLightDetail) 0.118f else 0.115f - f * 0.030f)),
                style = Stroke(
                    width = width * (if (isLightDetail) 0.00125f else 0.0028f - f * 0.0007f),
                    cap = StrokeCap.Round
                )
            )
        }

        val fringeCount = if (isLightDetail) 3 else 5
        repeat(fringeCount) { fringe ->
            val f = if (fringeCount == 1) 0f else fringe / (fringeCount - 1f)
            val fringeX = ribbonStart.x + ribbonLength * (0.08f + f * 0.70f)
            val fringeY = ribbonStart.y -
                ribbonLift * (0.32f + f * 0.16f) +
                sin((fringe + ribbon).toFloat()) * ribbonThickness * 0.35f
            val fringeLength = width * (0.075f + (fringe % 2) * 0.030f)
            val fringeLift = height * (0.030f + fringe * 0.004f)

            val fringePath = Path().apply {
                moveTo(fringeX, fringeY)
                cubicTo(
                    fringeX + fringeLength * 0.25f,
                    fringeY - fringeLift,
                    fringeX + fringeLength * 0.58f,
                    fringeY - fringeLift * 0.20f,
                    fringeX + fringeLength,
                    fringeY - fringeLift * 0.48f
                )
            }

            drawPath(
                path = fringePath,
                color = Color.White.copy(alpha = alpha * (if (isLightDetail) 0.066f else 0.070f - f * 0.026f)),
                style = Stroke(
                    width = width * (if (isLightDetail) 0.0008f else 0.0014f),
                    cap = StrokeCap.Round
                )
            )
        }

        if (showSoftKnots) {
            repeat(2) { knot ->
                val k = knot / 2f
                val knotCenter = Offset(
                    x = ribbonStart.x + ribbonLength * (0.34f + knot * 0.22f),
                    y = ribbonStart.y - ribbonLift * (0.20f + knot * 0.12f) + ribbonThickness * 0.25f
                )
                val knotW = width * (0.060f + knot * 0.018f)
                val knotH = height * (0.030f + knot * 0.010f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = alpha * (0.090f - k * 0.025f)),
                            Color(0xFFEAF4FF).copy(alpha = alpha * 0.040f),
                            Color.Transparent
                        ),
                        center = knotCenter,
                        radius = knotW * 0.62f
                    ),
                    topLeft = Offset(knotCenter.x - knotW * 0.50f, knotCenter.y - knotH * 0.50f),
                    size = Size(knotW, knotH)
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSunlitSmallCumulusWisps(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    val wisps = listOf(
        Offset(0.05f, 0.030f),
        Offset(0.20f, 0.070f),
        Offset(0.34f, 0.350f),
        Offset(0.55f, 0.380f),
        Offset(0.78f, 0.330f)
    )

    wisps.forEachIndexed { i, anchor ->
        val center = Offset(
            x = w * anchor.x + drift * w * (0.012f + i * 0.004f),
            y = h * anchor.y + sin(drift * 6.28f + i) * h * 0.004f
        )
        val patchWidth = w * (0.16f + (i % 2) * 0.050f)
        val patchHeight = h * (0.030f + (i % 3) * 0.008f)

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.42f),
                    Color(0xFFEAF4FF).copy(alpha = alpha * 0.18f),
                    Color.Transparent
                ),
                center = center,
                radius = patchWidth * 0.55f
            ),
            topLeft = Offset(center.x - patchWidth * 0.50f, center.y - patchHeight * 0.50f),
            size = Size(patchWidth, patchHeight)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLowHorizonWisps(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(5) { i ->
        val x = w * (0.06f + i * 0.20f) + drift * w * 0.018f
        val y = h * (0.345f + (i % 2) * 0.035f)
        val width = w * (0.20f + (i % 3) * 0.045f)
        val height = h * (0.020f + (i % 2) * 0.008f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * 0.18f),
                    Color(0xFFEAF4FF).copy(alpha = alpha * 0.14f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x, y),
            size = Size(width, height)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCirrusRichPartialSunnyClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawPhotoLikePredominantSunnyClouds(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.94f
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.18f + drift * w * 0.018f, h * 0.155f),
        width = w * 1.22f,
        height = h * 0.30f,
        alpha = alpha * 0.64f,
        sweep = -0.14f,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.06f + drift * w * 0.020f, h * 0.330f),
        width = w * 0.96f,
        height = h * 0.24f,
        alpha = alpha * 0.56f,
        sweep = -0.10f,
        detail = 0
    )

    drawSunlitCirrusVeils(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.34f
    )

    drawCirrusClouds(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.26f
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDensePartialSunnyClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawCirrusRichPartialSunnyClouds(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.98f
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.32f + drift * w * 0.018f, h * 0.060f),
        width = w * 1.34f,
        height = h * 0.22f,
        alpha = alpha * 0.42f,
        sweep = -0.09f,
        interiorGlow = 1.18f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.02f + drift * w * 0.022f, h * 0.115f),
        width = w * 1.18f,
        height = h * 0.25f,
        alpha = alpha * 0.48f,
        sweep = -0.12f,
        interiorGlow = 1.20f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.42f + drift * w * 0.017f, h * 0.165f),
        width = w * 1.46f,
        height = h * 0.28f,
        alpha = alpha * 0.50f,
        sweep = -0.12f,
        interiorGlow = 1.20f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.26f + drift * w * 0.024f, h * 0.205f),
        width = w * 1.02f,
        height = h * 0.24f,
        alpha = alpha * 0.44f,
        sweep = -0.10f,
        interiorGlow = 1.16f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.24f + drift * w * 0.016f, h * 0.245f),
        width = w * 1.26f,
        height = h * 0.26f,
        alpha = alpha * 0.40f,
        sweep = -0.08f,
        interiorGlow = 1.16f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * 0.42f + drift * w * 0.020f, h * 0.300f),
        width = w * 0.80f,
        height = h * 0.20f,
        alpha = alpha * 0.34f,
        sweep = -0.07f,
        interiorGlow = 1.14f,
        showSoftKnots = false,
        detail = 0
    )

    drawPaintedCirrusPlume(
        start = Offset(w * -0.10f + drift * w * 0.015f, h * 0.350f),
        width = w * 1.16f,
        height = h * 0.22f,
        alpha = alpha * 0.36f,
        sweep = -0.06f,
        interiorGlow = 1.12f,
        showSoftKnots = false,
        detail = 0
    )

    drawSunlitCirrusVeils(
        drift = drift + 0.28f,
        w = w,
        h = h,
        alpha = alpha * 0.38f
    )

    drawCirrusClouds(
        drift = drift + 0.18f,
        w = w,
        h = h,
        alpha = alpha * 0.34f
    )

    drawCirrusClouds(
        drift = drift + 0.42f,
        w = w,
        h = h,
        alpha = alpha * 0.24f
    )

    val smallPuffyClouds = listOf(
        CloudPuffSpec(0.52f, 0.165f, 0.82f, 0.168f, 0.78f, 0.22f),
        CloudPuffSpec(0.82f, 0.130f, 0.74f, 0.142f, 0.72f, 0.20f),
        CloudPuffSpec(0.28f, 0.255f, 0.76f, 0.178f, 0.70f, 0.22f),
        CloudPuffSpec(0.70f, 0.285f, 0.68f, 0.145f, 0.64f, 0.18f)
    )

    smallPuffyClouds.forEachIndexed { index, spec ->
        val driftX = drift * w * (0.014f + index * 0.003f)
        drawApplePuffyCloud(
            center = Offset(w * spec.x + driftX, h * spec.y),
            width = w * spec.width * 0.76f,
            height = h * spec.height * 0.42f,
            alpha = alpha * spec.alpha,
            darkness = spec.shadow
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPhotoLikePartialSunnyCumulus(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawAltocumulusSpeckles(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.62f
    )

    val clouds = listOf(
        CloudPuffSpec(0.32f, 0.30f, 0.36f, 0.14f, 0.92f, 0.12f),
        CloudPuffSpec(0.78f, 0.25f, 0.32f, 0.12f, 0.78f, 0.10f),
        CloudPuffSpec(0.18f, 0.48f, 0.28f, 0.11f, 0.82f, 0.14f),
        CloudPuffSpec(0.58f, 0.48f, 0.40f, 0.15f, 0.92f, 0.16f),
        CloudPuffSpec(0.87f, 0.52f, 0.26f, 0.10f, 0.72f, 0.12f),
        CloudPuffSpec(0.34f, 0.68f, 0.28f, 0.10f, 0.58f, 0.10f),
        CloudPuffSpec(0.68f, 0.70f, 0.32f, 0.11f, 0.54f, 0.10f)
    )

    clouds.forEachIndexed { index, spec ->
        val driftX = drift * w * (0.018f + index * 0.002f)
        drawRealisticCumulusCloud(
            center = Offset(w * spec.x + driftX, h * spec.y),
            width = w * spec.width,
            height = h * spec.height,
            alpha = alpha * spec.alpha,
            shadow = spec.shadow
        )
    }

    drawSmallSunlitCloudlet(
        center = Offset(w * 0.46f + drift * w * 0.025f, h * 0.13f),
        width = w * 0.18f,
        height = h * 0.055f,
        alpha = alpha * 0.48f
    )
}

private data class CloudPuffSpec(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val alpha: Float,
    val shadow: Float
)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAltocumulusSpeckles(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(42) { i ->
        val row = i % 6
        val col = i / 6
        val x = ((col * w * 0.13f) + row * w * 0.035f + drift * w * 0.030f) % (w * 1.15f) - w * 0.05f
        val y = h * (0.045f + row * 0.030f) + sin(i * 0.9f + drift * 6.28f) * h * 0.005f
        val radius = w * (0.010f + (i % 4) * 0.003f)

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.62f),
                    Color(0xFFEAF4FF).copy(alpha = alpha * 0.20f),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = radius * 1.9f
            ),
            topLeft = Offset(x - radius * 1.45f, y - radius * 0.82f),
            size = Size(radius * 2.9f, radius * 1.64f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRealisticCumulusCloud(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    shadow: Float
) {
    val left = center.x - width * 0.50f
    val top = center.y - height * 0.50f

    val cloudShape = Path().apply {
        moveTo(left + width * 0.04f, top + height * 0.64f)
        cubicTo(left + width * 0.00f, top + height * 0.48f, left + width * 0.12f, top + height * 0.34f, left + width * 0.25f, top + height * 0.38f)
        cubicTo(left + width * 0.31f, top + height * 0.12f, left + width * 0.48f, top + height * 0.02f, left + width * 0.60f, top + height * 0.24f)
        cubicTo(left + width * 0.75f, top + height * 0.12f, left + width * 0.96f, top + height * 0.30f, left + width * 0.94f, top + height * 0.56f)
        cubicTo(left + width * 0.99f, top + height * 0.70f, left + width * 0.82f, top + height * 0.88f, left + width * 0.64f, top + height * 0.82f)
        lineTo(left + width * 0.18f, top + height * 0.82f)
        cubicTo(left + width * 0.08f, top + height * 0.82f, left + width * 0.03f, top + height * 0.74f, left + width * 0.04f, top + height * 0.64f)
        close()
    }

    drawPath(
        path = cloudShape,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha),
                Color(0xFFF2F8FC).copy(alpha = alpha * 0.92f),
                Color(0xFF8AA6BE).copy(alpha = alpha * shadow)
            ),
            startY = top,
            endY = top + height
        )
    )

    val puffs = listOf(
        Triple(0.26f, 0.38f, 0.28f),
        Triple(0.44f, 0.25f, 0.34f),
        Triple(0.62f, 0.34f, 0.30f),
        Triple(0.78f, 0.50f, 0.24f)
    )

    puffs.forEachIndexed { i, puff ->
        val cx = left + width * puff.first
        val cy = top + height * puff.second
        val rx = width * puff.third
        val ry = height * (0.42f + (i % 2) * 0.08f)

        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.74f),
                    Color(0xFFF4FAFE).copy(alpha = alpha * 0.55f),
                    Color.Transparent
                ),
                startY = cy - ry,
                endY = cy + ry
            ),
            topLeft = Offset(cx - rx * 0.50f, cy - ry * 0.50f),
            size = Size(rx, ry)
        )
    }

    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFFA2B9CD).copy(alpha = alpha * shadow * 0.95f),
                Color(0xFF6F8FAE).copy(alpha = alpha * shadow * 0.72f)
            ),
            startY = top + height * 0.38f,
            endY = top + height
        ),
        topLeft = Offset(left + width * 0.04f, top + height * 0.46f),
        size = Size(width * 0.92f, height * 0.52f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSmallSunlitCloudlet(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float
) {
    drawRealisticCumulusCloud(
        center = center,
        width = width,
        height = height,
        alpha = alpha,
        shadow = 0.06f
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMilkyCirrusPatch(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    slant: Float
) {
    repeat(5) { i ->
        val t = i / 4f
        val localWidth = width * (0.72f + t * 0.18f)
        val localHeight = height * (0.22f + t * 0.08f)
        val x = center.x - localWidth * 0.50f + width * (t - 0.5f) * 0.16f
        val y = center.y - height * 0.36f + height * t * 0.20f + width * slant * (t - 0.5f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * (0.13f + t * 0.05f)),
                    Color(0xFFE6F0FA).copy(alpha = alpha * (0.22f + t * 0.03f)),
                    Color.White.copy(alpha = alpha * 0.10f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x, y),
            size = Size(localWidth, localHeight)
        )
    }

    repeat(7) { i ->
        val t = i / 6f
        val startX = center.x - width * 0.48f + width * t * 0.10f
        val startY = center.y - height * 0.34f + height * t * 0.13f
        val lineLength = width * (0.78f + (i % 3) * 0.06f)
        val curve = height * (0.20f + (i % 2) * 0.12f)

        val path = Path().apply {
            moveTo(startX, startY)
            cubicTo(
                startX + lineLength * 0.28f,
                startY - curve,
                startX + lineLength * 0.60f,
                startY + curve * 0.34f,
                startX + lineLength,
                startY - curve * 0.26f
            )
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = alpha * (0.12f - t * 0.035f)),
            style = Stroke(
                width = width * 0.006f,
                cap = StrokeCap.Round
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCirrusStreamer(
    start: Offset,
    length: Float,
    lift: Float,
    alpha: Float,
    strands: Int
) {
    repeat(strands) { i ->
        val t = if (strands <= 1) 0f else i / (strands - 1f)
        val offsetY = (t - 0.5f) * lift * 1.25f
        val startX = start.x + length * t * 0.045f
        val startY = start.y + offsetY
        val endX = startX + length * (0.88f + (i % 3) * 0.05f)
        val endY = startY - lift * (0.40f + t * 0.55f)
        val bow = lift * (0.85f + (i % 2) * 0.34f)

        val path = Path().apply {
            moveTo(startX, startY)
            cubicTo(
                startX + length * 0.26f,
                startY - bow,
                startX + length * 0.58f,
                startY + bow * 0.22f,
                endX,
                endY
            )
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = alpha * (0.17f - t * 0.055f)),
            style = Stroke(
                width = length * (0.0065f - t * 0.0022f),
                cap = StrokeCap.Round
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCirrusClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(6) { band ->
        val layer = band / 5f
        val baseX = ((drift * w * (0.08f + layer * 0.14f)) + band * w * 0.29f) % (w * 1.75f) - w * 0.42f
        val baseY = h * (0.052f + (band % 4) * 0.054f) + sin(drift * 6.28f + band * 1.7f) * h * 0.012f
        val length = w * (0.62f + (band % 3) * 0.16f)
        val lean = h * (0.024f + layer * 0.030f)
        val thickness = h * (0.012f + (band % 2) * 0.005f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * 0.035f),
                    Color.White.copy(alpha = alpha * 0.095f),
                    Color.White.copy(alpha = alpha * 0.040f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(baseX - w * 0.07f, baseY - thickness * 2.1f),
            size = Size(length * 1.16f, thickness * 4.8f)
        )

        val mainWisps = 2 + band % 2
        repeat(mainWisps) { strand ->
            val strandOffset = (strand - (mainWisps - 1) / 2f) * thickness * 0.95f
            val startX = baseX + strand * w * 0.014f
            val startY = baseY + strandOffset
            val mid1X = startX + length * (0.22f + strand * 0.03f)
            val mid2X = startX + length * (0.58f + band * 0.012f)
            val endX = startX + length * (0.88f + (strand % 2) * 0.08f)
            val endY = startY - lean + sin(band + strand + drift * 6.28f) * h * 0.012f
            val curl = h * (0.020f + (band + strand) % 3 * 0.007f)

            val path = Path().apply {
                moveTo(startX, startY)
                cubicTo(
                    mid1X,
                    startY - curl,
                    mid2X,
                    startY + curl * 0.42f,
                    endX,
                    endY
                )
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = alpha * (0.22f + strand * 0.055f)),
                style = Stroke(
                    width = w * (0.0026f + strand * 0.0010f),
                    cap = StrokeCap.Round
                )
            )

            repeat(4) { feather ->
                val anchor = 0.17f + feather * 0.15f + strand * 0.030f
                val fx = startX + length * anchor
                val fy = startY - curl * 0.30f + sin(feather + band + drift * 6.28f) * h * 0.006f
                val featherLength = w * (0.10f + feather * 0.032f)
                val featherLift = h * (0.012f + feather * 0.005f)

                val featherPath = Path().apply {
                    moveTo(fx, fy)
                    cubicTo(
                        fx + featherLength * 0.28f,
                        fy - featherLift,
                        fx + featherLength * 0.62f,
                        fy - featherLift * 0.35f,
                        fx + featherLength,
                        fy - featherLift * 0.74f
                    )
                }

                drawPath(
                    path = featherPath,
                    color = Color.White.copy(alpha = alpha * (0.090f - feather * 0.012f)),
                    style = Stroke(
                        width = w * 0.0019f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        repeat(2) { veil ->
            val veilX = baseX + length * (0.28f + veil * 0.25f)
            val veilY = baseY - lean * 0.35f + sin(band + veil + drift * 6.28f) * thickness

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha * 0.095f),
                        Color.White.copy(alpha = alpha * 0.030f),
                        Color.Transparent
                    ),
                    center = Offset(veilX, veilY),
                    radius = w * 0.16f
                ),
                topLeft = Offset(veilX - w * 0.18f, veilY - h * 0.026f),
                size = Size(w * 0.36f, h * 0.052f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSunlitCirrusVeils(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    val patches = listOf(
        Offset(0.12f, 0.12f),
        Offset(0.38f, 0.09f),
        Offset(0.66f, 0.16f),
        Offset(0.82f, 0.25f),
        Offset(0.26f, 0.25f)
    )

    patches.forEachIndexed { i, anchor ->
        val x = ((anchor.x * w) + drift * w * (0.035f + i * 0.012f)) % (w * 1.20f) - w * 0.10f
        val y = h * anchor.y + sin(drift * 6.28f + i * 1.4f) * h * 0.010f
        val patchWidth = w * (0.34f + (i % 3) * 0.08f)
        val patchHeight = h * (0.045f + (i % 2) * 0.016f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * 0.16f),
                    Color(0xFFEAF4FF).copy(alpha = alpha * 0.22f),
                    Color.White.copy(alpha = alpha * 0.10f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x - patchWidth * 0.50f, y - patchHeight * 0.50f),
            size = Size(patchWidth, patchHeight)
        )

        repeat(2) { streak ->
            val sx = x - patchWidth * 0.46f + streak * patchWidth * 0.20f
            val sy = y - patchHeight * (0.18f - streak * 0.18f)
            val path = Path().apply {
                moveTo(sx, sy)
                cubicTo(
                    sx + patchWidth * 0.28f,
                    sy - patchHeight * 0.55f,
                    sx + patchWidth * 0.62f,
                    sy + patchHeight * 0.38f,
                    sx + patchWidth * 0.98f,
                    sy - patchHeight * 0.22f
                )
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = alpha * (0.12f - streak * 0.035f)),
                style = Stroke(
                    width = w * 0.0030f,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPartlySunnyCloudDeck(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.24f),
                Color(0xFFD5E3EF).copy(alpha = alpha * 0.20f),
                Color(0xFF8EACCA).copy(alpha = alpha * 0.08f),
                Color.Transparent
            ),
            startY = 0f,
            endY = h * 0.46f
        )
    )

    drawReferencePartialCloudShelf(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.78f
    )

    drawSoftSunCloudMass(
        center = Offset(w * 0.50f + drift * w * 0.012f, h * 0.060f),
        width = w * 1.18f,
        height = h * 0.26f,
        alpha = alpha * 1.08f,
        coolShadow = 0.18f
    )

    drawSoftSunCloudMass(
        center = Offset(w * 0.27f + drift * w * 0.020f, h * 0.168f),
        width = w * 1.02f,
        height = h * 0.19f,
        alpha = alpha * 0.82f,
        coolShadow = 0.12f
    )

    drawSoftSunCloudMass(
        center = Offset(w * 0.77f + drift * w * 0.026f, h * 0.205f),
        width = w * 0.94f,
        height = h * 0.18f,
        alpha = alpha * 0.78f,
        coolShadow = 0.14f
    )

    drawCirrusStreamer(
        start = Offset(w * -0.14f + drift * w * 0.024f, h * 0.040f),
        length = w * 0.95f,
        lift = h * 0.060f,
        alpha = alpha * 0.60f,
        strands = 8
    )

    drawCirrusStreamer(
        start = Offset(w * 0.22f + drift * w * 0.030f, h * 0.132f),
        length = w * 1.02f,
        lift = h * 0.074f,
        alpha = alpha * 0.58f,
        strands = 9
    )

    drawCirrusStreamer(
        start = Offset(w * 0.43f + drift * w * 0.026f, h * 0.245f),
        length = w * 0.76f,
        lift = h * 0.050f,
        alpha = alpha * 0.36f,
        strands = 5
    )

    repeat(4) { i ->
        val x = w * (0.15f + i * 0.23f) + drift * w * (0.012f + i * 0.004f)
        val y = h * (0.105f + (i % 2) * 0.085f)
        val patchW = w * (0.42f + (i % 2) * 0.14f)
        val patchH = h * (0.050f + (i % 3) * 0.010f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * 0.12f),
                    Color(0xFFD9E9F7).copy(alpha = alpha * 0.22f),
                    Color(0xFFB7CADB).copy(alpha = alpha * 0.08f),
                    Color.White.copy(alpha = alpha * 0.07f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x - patchW * 0.5f, y - patchH * 0.5f),
            size = Size(patchW, patchH)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawReferencePartialCloudShelf(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    val shelves = listOf(
        Triple(0.06f, 0.055f, 0.62f),
        Triple(0.42f, 0.070f, 0.78f),
        Triple(0.72f, 0.128f, 0.58f),
        Triple(0.16f, 0.210f, 0.70f),
        Triple(0.58f, 0.255f, 0.50f)
    )

    shelves.forEachIndexed { index, shelf ->
        val x = ((shelf.first * w) + drift * w * (0.016f + index * 0.004f)) % (w * 1.20f) - w * 0.10f
        val y = h * shelf.second + sin(drift * 6.28f + index * 1.1f) * h * 0.006f
        val shelfWidth = w * shelf.third
        val shelfHeight = h * (0.078f + (index % 2) * 0.022f)

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.26f),
                    Color(0xFFE6EFF7).copy(alpha = alpha * 0.22f),
                    Color(0xFF9FB7CC).copy(alpha = alpha * 0.10f),
                    Color.Transparent
                ),
                center = Offset(x + shelfWidth * 0.44f, y + shelfHeight * 0.45f),
                radius = shelfWidth * 0.62f
            ),
            topLeft = Offset(x - shelfWidth * 0.50f, y - shelfHeight * 0.50f),
            size = Size(shelfWidth, shelfHeight)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawReferencePartialSunnyWisps(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(10) { i ->
        val group = i % 5
        val startX = ((group * w * 0.22f) + drift * w * 0.018f) % (w * 1.24f) - w * 0.14f
        val startY = h * (0.060f + group * 0.045f) + sin(i + drift * 6.28f) * h * 0.005f
        val length = w * (0.46f + (i % 3) * 0.10f)
        val lift = h * (0.024f + (i % 4) * 0.006f)

        val path = Path().apply {
            moveTo(startX, startY)
            cubicTo(
                startX + length * 0.30f,
                startY - lift,
                startX + length * 0.64f,
                startY + lift * 0.28f,
                startX + length,
                startY - lift * 0.34f
            )
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = alpha * (0.16f - (i % 5) * 0.018f)),
            style = Stroke(
                width = w * (0.0024f + (i % 3) * 0.0008f),
                cap = StrokeCap.Round
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPartlySunnyWispySheet(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(14) { i ->
        val row = i % 7
        val layer = row / 6f
        val startX = ((drift * w * (0.022f + layer * 0.018f)) + i * w * 0.105f) % (w * 1.35f) - w * 0.18f
        val startY = h * (0.030f + row * 0.032f) + sin(drift * 6.28f + i * 0.8f) * h * 0.006f
        val length = w * (0.52f + (i % 4) * 0.10f)
        val bow = h * (0.018f + (i % 3) * 0.010f)

        val path = Path().apply {
            moveTo(startX, startY)
            cubicTo(
                startX + length * 0.24f,
                startY - bow,
                startX + length * 0.58f,
                startY + bow * 0.34f,
                startX + length,
                startY - bow * 0.36f
            )
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = alpha * (0.16f - layer * 0.055f)),
            style = Stroke(
                width = w * (0.0030f + (i % 2) * 0.0012f),
                cap = StrokeCap.Round
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSoftSunCloudMass(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    coolShadow: Float
) {
    repeat(6) { i ->
        val t = i / 5f
        val localWidth = width * (0.56f + t * 0.18f)
        val localHeight = height * (0.30f + (i % 3) * 0.08f)
        val x = center.x - width * 0.44f + width * t * 0.11f
        val y = center.y - height * 0.44f + height * t * 0.16f + sin(i * 1.3f) * height * 0.07f

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = alpha * (0.16f + t * 0.04f)),
                    Color(0xFFEAF3FC).copy(alpha = alpha * (0.28f + t * 0.04f)),
                    Color(0xFFB8CDE0).copy(alpha = alpha * coolShadow),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x, y),
            size = Size(localWidth, localHeight)
        )
    }

    repeat(5) { i ->
        val t = i / 4f
        val startX = center.x - width * 0.46f + width * t * 0.09f
        val startY = center.y - height * 0.20f + height * (t - 0.5f) * 0.42f
        val lineLength = width * (0.70f + (i % 2) * 0.13f)
        val bow = height * (0.24f + (i % 3) * 0.05f)

        val path = Path().apply {
            moveTo(startX, startY)
            cubicTo(
                startX + lineLength * 0.25f,
                startY - bow,
                startX + lineLength * 0.58f,
                startY + bow * 0.28f,
                startX + lineLength,
                startY - bow * 0.22f
            )
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = alpha * (0.10f - t * 0.025f)),
            style = Stroke(
                width = width * 0.0042f,
                cap = StrokeCap.Round
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCirrocumulusClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(34) { i ->
        val row = i % 5
        val x = ((drift * w * 0.23f) + i * w * 0.075f) % (w * 1.25f) - w * 0.12f
        val y = h * (0.06f + row * 0.047f) + sin(i + drift * 6.28f) * 8f
        val radius = w * (0.035f + (i % 3) * 0.010f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha),
                    Color.White.copy(alpha = alpha * 0.40f),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = radius * 2.4f
            ),
            radius = radius * 2.4f,
            center = Offset(x, y)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSunlitCumulusField(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.04f),
                Color.Transparent,
                Color(0xFF195FA8).copy(alpha = 0.10f)
            )
        )
    )

    val cloudSpecs = listOf(
        Triple(-0.30f, 0.022f, 1.20f),
        Triple(0.16f, 0.050f, 0.96f),
        Triple(0.56f, 0.038f, 1.14f),
        Triple(0.94f, 0.074f, 0.72f),
        Triple(-0.16f, 0.138f, 0.82f),
        Triple(0.18f, 0.160f, 0.92f),
        Triple(0.52f, 0.136f, 0.68f),
        Triple(0.82f, 0.168f, 0.80f),
        Triple(0.02f, 0.238f, 0.46f),
        Triple(0.30f, 0.248f, 0.66f),
        Triple(0.58f, 0.228f, 0.42f),
        Triple(0.84f, 0.252f, 0.52f)
    )

    cloudSpecs.forEachIndexed { index, spec ->
        val x = ((spec.first * w) + drift * w * (0.026f + index * 0.004f)) % (w * 1.32f) - w * 0.16f
        val y = h * spec.second + sin(drift * 6.28f + index * 1.7f) * h * 0.006f
        val scale = spec.third

        drawOutlinedCumulusCloud(
            center = Offset(x, y),
            width = w * (0.78f + (index % 2) * 0.12f) * scale,
            height = h * (0.145f + (index % 3) * 0.018f) * scale,
            alpha = 1f,
            shadow = 0.24f + (index % 4) * 0.045f,
            rimLight = index in 0..7,
            variant = index
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOutlinedCumulusCloud(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    shadow: Float,
    rimLight: Boolean,
    variant: Int
) {
    val left = center.x - width * 0.50f
    val top = center.y - height * 0.52f
    val right = center.x + width * 0.50f
    val bottom = center.y + height * 0.36f
    val lobeA = sin(variant * 1.31f) * 0.055f

    val cloudPath = Path().apply {
        moveTo(left + width * 0.02f, bottom - height * 0.24f)

        repeat(42) { point ->
            val t = point / 41f
            val dome = sin(t * 3.14159f)
            val tornEdge =
                sin(t * 33f + variant * 1.7f) * 0.044f +
                    sin(t * 79f + variant * 0.9f) * 0.020f +
                    sin(t * 141f + variant * 2.3f) * 0.010f
            val x = left + width * (0.02f + t * 0.96f)
            val y = top + height * (0.72f - dome * 0.64f + tornEdge)
            lineTo(x, y)
        }

        repeat(30) { point ->
            val t = point / 29f
            val sag = sin(t * 3.14159f)
            val tornEdge =
                sin(t * 43f + variant * 2.1f) * 0.024f +
                    sin(t * 97f + variant * 1.3f) * 0.010f
            val x = right - width * (0.04f + t * 0.94f)
            val y = bottom - height * (0.15f + sag * 0.10f) + height * tornEdge
            lineTo(x, y)
        }

        close()
    }

    drawPath(
        path = cloudPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha),
                Color.White.copy(alpha = alpha),
                Color(0xFFF6FAFD).copy(alpha = alpha),
                Color(0xFFDDE6EE).copy(alpha = alpha * 0.96f)
            ),
            startY = top,
            endY = bottom
        )
    )

    drawPath(
        path = cloudPath,
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.62f),
                Color(0xFFF7FBFF).copy(alpha = alpha * 0.24f),
                Color.Transparent
            ),
            center = Offset(left + width * (0.34f + lobeA), top + height * 0.30f),
            radius = width * 0.62f
        )
    )

    val shadowPath = Path().apply {
        moveTo(left + width * 0.12f, bottom - height * 0.22f)
        cubicTo(left + width * 0.28f, bottom - height * 0.04f, left + width * 0.68f, bottom - height * 0.01f, right - width * 0.10f, bottom - height * 0.17f)
        cubicTo(right - width * 0.16f, bottom + height * 0.07f, left + width * 0.28f, bottom + height * 0.10f, left + width * 0.12f, bottom - height * 0.22f)
        close()
    }

    drawPath(
        path = shadowPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFFAAB5C0).copy(alpha = alpha * shadow * 0.56f),
                Color(0xFF7D8996).copy(alpha = alpha * shadow * 0.34f)
            ),
            startY = bottom - height * 0.30f,
            endY = bottom + height * 0.08f
        )
    )

    repeat(3) { layer ->
        val t = layer / 2f
        val hazePath = Path().apply {
            val y = top + height * (0.38f + t * 0.17f)
            moveTo(left + width * (0.10f + t * 0.05f), y)
            cubicTo(
                left + width * 0.28f,
                y - height * (0.10f - t * 0.02f),
                left + width * 0.58f,
                y + height * (0.08f + t * 0.02f),
                right - width * (0.12f + t * 0.03f),
                y - height * 0.02f
            )
        }

        drawPath(
            path = hazePath,
            color = Color.White.copy(alpha = alpha * (0.11f - t * 0.026f)),
            style = Stroke(
                width = width * (0.012f - t * 0.003f),
                cap = StrokeCap.Round
            )
        )
    }

    drawPath(
        path = cloudPath,
        color = Color.White.copy(alpha = alpha * (if (rimLight) 0.38f else 0.28f)),
        style = Stroke(
            width = width * 0.007f,
            cap = StrokeCap.Round
        )
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCrispCumulusCloud(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    shadow: Float,
    rimLight: Boolean
) {
    val left = center.x - width * 0.50f
    val top = center.y - height * 0.48f
    val baseY = center.y + height * 0.17f
    val shadowBlue = Color(0xFF697E93)
    val coolWhite = Color(0xFFE9F3FB)

    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.80f),
                coolWhite.copy(alpha = alpha * 0.92f),
                shadowBlue.copy(alpha = alpha * shadow)
            ),
            startY = top,
            endY = top + height * 1.15f
        ),
        topLeft = Offset(left + width * 0.03f, top + height * 0.42f),
        size = Size(width * 0.94f, height * 0.72f)
    )

    val puffs = listOf(
        Triple(0.10f, 0.58f, 0.20f),
        Triple(0.22f, 0.38f, 0.27f),
        Triple(0.36f, 0.28f, 0.32f),
        Triple(0.50f, 0.24f, 0.36f),
        Triple(0.64f, 0.32f, 0.30f),
        Triple(0.78f, 0.44f, 0.25f),
        Triple(0.90f, 0.58f, 0.18f)
    )

    puffs.forEachIndexed { index, puff ->
        val px = left + width * puff.first
        val py = top + height * puff.second + sin(index * 1.9f) * height * 0.025f
        val rx = width * puff.third * (0.72f + (index % 2) * 0.10f)
        val ry = height * puff.third * (1.02f + (index % 3) * 0.12f)

        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.80f),
                    Color.White.copy(alpha = alpha * 0.86f),
                    coolWhite.copy(alpha = alpha * 0.78f),
                    shadowBlue.copy(alpha = alpha * (shadow * 0.48f))
                ),
                startY = py - ry,
                endY = py + ry
            ),
            topLeft = Offset(px - rx, py - ry),
            size = Size(rx * 2f, ry * 2f)
        )
    }

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.30f),
                coolWhite.copy(alpha = alpha * 0.22f),
                Color.Transparent
            ),
            center = Offset(center.x, top + height * 0.55f),
            radius = width * 0.58f
        ),
        topLeft = Offset(left + width * 0.04f, top + height * 0.24f),
        size = Size(width * 0.92f, height * 0.84f)
    )

    repeat(12) { edge ->
        val t = edge / 11f
        val wave = sin(edge * 1.45f) * height * 0.055f
        val edgeX = left + width * (0.02f + t * 0.96f)
        val edgeY = when {
            t < 0.22f -> baseY - height * (0.08f + t * 0.72f) + wave
            t > 0.78f -> baseY - height * (0.64f - (t - 0.78f) * 1.88f) + wave
            else -> top + height * (0.15f + sin(t * 6.28f) * 0.10f) + wave
        }
        val radius = width * (0.026f + (edge % 4) * 0.006f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.42f),
                    Color.White.copy(alpha = alpha * 0.26f),
                    Color.Transparent
                ),
                center = Offset(edgeX, edgeY),
                radius = radius * 2.4f
            ),
            radius = radius * 1.7f,
            center = Offset(edgeX, edgeY)
        )
    }

    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                shadowBlue.copy(alpha = alpha * shadow * 0.70f),
                Color(0xFF405B76).copy(alpha = alpha * shadow * 0.44f)
            ),
            startY = baseY - height * 0.10f,
            endY = baseY + height * 0.50f
        ),
        topLeft = Offset(left + width * 0.06f, baseY - height * 0.12f),
        size = Size(width * 0.88f, height * 0.42f)
    )

    if (rimLight) {
        repeat(7) { i ->
            val t = i / 6f
            val rimX = left + width * (0.12f + t * 0.72f)
            val rimY = top + height * (0.18f + sin(i * 1.2f) * 0.10f)
            val rimPath = Path().apply {
                moveTo(rimX - width * 0.035f, rimY)
                cubicTo(
                    rimX + width * 0.030f,
                    rimY - height * 0.060f,
                    rimX + width * 0.105f,
                    rimY - height * 0.045f,
                    rimX + width * 0.150f,
                    rimY + height * 0.010f
                )
            }

            drawPath(
                path = rimPath,
                color = Color.White.copy(alpha = alpha * 0.42f),
                style = Stroke(
                    width = width * 0.006f,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCumulusClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float,
    darken: Float
) {
    repeat(5) { i ->
        val x = ((drift * w * 0.28f) + i * w * 0.34f) % (w * 1.55f) - w * 0.35f
        val y = h * (0.08f + (i % 3) * 0.065f)
        val scale = 0.88f + (i % 3) * 0.18f
        drawApplePuffyCloud(
            center = Offset(x, y),
            width = w * 0.72f * scale,
            height = h * 0.16f * scale,
            alpha = alpha,
            darkness = darken
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStratocumulusClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(8) { i ->
        val x = ((drift * w * 0.20f) + i * w * 0.22f) % (w * 1.45f) - w * 0.28f
        val y = h * (0.035f + (i % 4) * 0.070f)
        drawApplePuffyCloud(
            center = Offset(x, y),
            width = w * (0.58f + (i % 3) * 0.14f),
            height = h * (0.13f + (i % 2) * 0.035f),
            alpha = alpha * (0.88f + (i % 3) * 0.06f),
            darkness = 0.16f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPredominantlyCloudyPuffyField(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.16f),
                Color(0xFFE0EDF7).copy(alpha = alpha * 0.10f),
                Color.Transparent
            ),
            startY = 0f,
            endY = h * 0.46f
        )
    )

    val cloudSpecs = listOf(
        CloudPuffSpec(-0.52f, 0.050f, 0.66f, 0.118f, 0.82f, 0.20f),
        CloudPuffSpec(0.82f, 0.062f, 0.74f, 0.142f, 0.90f, 0.24f),
        CloudPuffSpec(1.10f, 0.092f, 0.66f, 0.132f, 0.84f, 0.22f),

        CloudPuffSpec(-0.48f, 0.170f, 0.62f, 0.122f, 0.78f, 0.22f),
        CloudPuffSpec(0.66f, 0.164f, 0.82f, 0.168f, 0.94f, 0.28f),
        CloudPuffSpec(0.94f, 0.154f, 0.78f, 0.148f, 0.88f, 0.26f),

        CloudPuffSpec(-0.26f, 0.245f, 0.66f, 0.155f, 0.84f, 0.28f),
        CloudPuffSpec(0.06f, 0.270f, 0.76f, 0.178f, 0.92f, 0.30f),
        CloudPuffSpec(0.40f, 0.250f, 0.92f, 0.210f, 0.98f, 0.33f),
        CloudPuffSpec(0.78f, 0.275f, 0.82f, 0.190f, 0.92f, 0.31f),
        CloudPuffSpec(1.14f, 0.255f, 0.66f, 0.160f, 0.82f, 0.28f),

        CloudPuffSpec(0.14f, 0.365f, 0.68f, 0.145f, 0.70f, 0.24f),
        CloudPuffSpec(0.52f, 0.385f, 0.84f, 0.165f, 0.76f, 0.26f),
        CloudPuffSpec(0.92f, 0.365f, 0.62f, 0.138f, 0.66f, 0.22f)
    )

    cloudSpecs.forEachIndexed { index, spec ->
        val driftX = drift * w * (0.020f + (index % 5) * 0.004f)
        val wrapWidth = w * 1.42f
        val x = ((spec.x * w) + driftX) % wrapWidth - w * 0.16f
        val y = h * spec.y + sin(drift * 6.28f + index * 1.35f) * h * 0.008f

        drawApplePuffyCloud(
            center = Offset(x, y),
            width = w * spec.width * 1.28f,
            height = h * spec.height * 0.54f,
            alpha = alpha * spec.alpha,
            darkness = spec.shadow
        )
    }

    repeat(5) { layer ->
        val t = layer / 4f
        val x = ((drift * w * (0.035f + t * 0.018f)) + layer * w * 0.31f) % (w * 1.40f) - w * 0.22f
        val y = h * (0.225f + t * 0.052f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFF7FBFF).copy(alpha = alpha * (0.16f - t * 0.020f)),
                    Color(0xFFD5E2EC).copy(alpha = alpha * (0.20f - t * 0.018f)),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x, y),
            size = Size(w * (0.84f + t * 0.18f), h * (0.070f + t * 0.018f))
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOvercastPuffyField(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    drawPredominantlyCloudyPuffyField(
        drift = drift,
        w = w,
        h = h,
        alpha = alpha * 0.96f
    )

    val extraClouds = listOf(
        CloudPuffSpec(0.10f, 0.035f, 0.74f, 0.120f, 0.88f, 0.24f),
        CloudPuffSpec(0.42f, 0.060f, 0.88f, 0.135f, 0.94f, 0.28f),
        CloudPuffSpec(0.86f, 0.045f, 0.82f, 0.128f, 0.90f, 0.26f),

        CloudPuffSpec(-0.18f, 0.190f, 0.78f, 0.132f, 0.86f, 0.28f),
        CloudPuffSpec(0.28f, 0.205f, 0.96f, 0.150f, 0.96f, 0.32f),
        CloudPuffSpec(0.72f, 0.198f, 0.92f, 0.145f, 0.94f, 0.31f),
        CloudPuffSpec(1.12f, 0.212f, 0.72f, 0.124f, 0.82f, 0.27f),

        CloudPuffSpec(-0.08f, 0.328f, 0.82f, 0.126f, 0.72f, 0.25f),
        CloudPuffSpec(0.34f, 0.345f, 0.94f, 0.138f, 0.78f, 0.28f),
        CloudPuffSpec(0.78f, 0.330f, 0.86f, 0.130f, 0.74f, 0.26f)
    )

    extraClouds.forEachIndexed { index, spec ->
        val driftX = drift * w * (0.017f + (index % 4) * 0.004f)
        val wrapWidth = w * 1.42f
        val x = ((spec.x * w) + driftX) % wrapWidth - w * 0.16f
        val y = h * spec.y + sin(drift * 6.28f + index * 1.12f) * h * 0.006f

        drawApplePuffyCloud(
            center = Offset(x, y),
            width = w * spec.width * 1.34f,
            height = h * spec.height * 0.52f,
            alpha = alpha * spec.alpha,
            darkness = spec.shadow
        )
    }

    repeat(6) { layer ->
        val t = layer / 5f
        val x = ((drift * w * (0.028f + t * 0.016f)) + layer * w * 0.25f) % (w * 1.36f) - w * 0.18f
        val y = h * (0.080f + t * 0.052f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFF6FAFF).copy(alpha = alpha * (0.13f - t * 0.014f)),
                    Color(0xFFC7D5DF).copy(alpha = alpha * (0.16f - t * 0.012f)),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x, y),
            size = Size(w * (0.92f + t * 0.20f), h * (0.055f + t * 0.014f))
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStormCloudShadow(
    w: Float,
    h: Float,
    alpha: Float
) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF3A3D3F).copy(alpha = alpha * 0.80f),
                Color(0xFF505456).copy(alpha = alpha * 0.54f),
                Color.Transparent
            ),
            startY = 0f,
            endY = h * 0.46f
        )
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStormSunBreak(
    w: Float,
    h: Float,
    alpha: Float
) {
    val sunCenter = Offset(w * 0.21f, h * 0.14f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.70f),
                Color(0xFFFFF2D6).copy(alpha = alpha * 0.38f),
                Color.White.copy(alpha = alpha * 0.16f),
                Color.Transparent
            ),
            center = sunCenter,
            radius = w * 0.30f
        ),
        radius = w * 0.30f,
        center = sunCenter
    )

    repeat(8) { ray ->
        val angle = (ray * 45f + 8f) * 0.017453292f
        drawSoftSunRay(
            center = sunCenter,
            angle = angle,
            inner = w * 0.052f,
            outer = w * 0.190f,
            baseHalfWidth = w * 0.013f,
            alpha = alpha * 0.070f,
            warm = true
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha),
                Color.White.copy(alpha = alpha * 0.74f),
                Color.White.copy(alpha = alpha * 0.22f),
                Color.Transparent
            ),
            center = sunCenter,
            radius = w * 0.112f
        ),
        radius = w * 0.112f,
        center = sunCenter
    )

    drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = w * 0.036f,
        center = sunCenter
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStratusClouds(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(10) { i ->
        val layer = i / 9f
        val x = ((drift * w * (0.10f + layer * 0.10f)) + i * w * 0.19f) % (w * 1.55f) - w * 0.30f
        val y = h * (0.015f + layer * 0.070f)

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFE8F0F7).copy(alpha = alpha * (0.52f + layer * 0.20f)),
                    Color(0xFFB8C6D2).copy(alpha = alpha * 0.58f),
                    Color.Transparent
                )
            ),
            topLeft = Offset(x, y + sin(drift * 6.28f + i) * 8f),
            size = Size(w * (0.72f + layer * 0.36f), h * (0.080f + layer * 0.030f))
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRainCeiling(
    drift: Float,
    w: Float,
    h: Float,
    alpha: Float
) {
    repeat(7) { i ->
        val x = ((drift * w * 0.14f) + i * w * 0.25f) % (w * 1.45f) - w * 0.22f
        val y = h * (0.02f + (i % 3) * 0.055f)

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE4ECF2).copy(alpha = alpha * 0.64f),
                    Color(0xFF6C7E8C).copy(alpha = alpha),
                    Color.Transparent
                ),
                center = Offset(x + w * 0.28f, y + h * 0.07f),
                radius = w * 0.45f
            ),
            topLeft = Offset(x, y),
            size = Size(w * 0.72f, h * 0.18f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawApplePuffyCloud(
    center: Offset,
    width: Float,
    height: Float,
    alpha: Float,
    darkness: Float
) {
    val base = Color(0xFFF0F6FB)
    val shadow = Color(0xFF9EAFBE)
    val topY = center.y - height * 0.42f
    val leftX = center.x - width * 0.50f

    repeat(6) { i ->
        val t = i / 5f
        val cx = leftX + width * (0.12f + t * 0.17f)
        val cy = topY + height * (0.46f + sin(i * 1.3f) * 0.10f)
        val rx = width * (0.15f + (i % 3) * 0.025f)
        val ry = height * (0.42f + (i % 2) * 0.10f)

        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha),
                    base.copy(alpha = alpha * 0.92f),
                    shadow.copy(alpha = alpha * (0.26f + darkness))
                ),
                startY = cy - ry,
                endY = cy + ry
            ),
            topLeft = Offset(cx - rx, cy - ry),
            size = Size(rx * 2f, ry * 2f)
        )
    }

    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.78f),
                base.copy(alpha = alpha * 0.82f),
                shadow.copy(alpha = alpha * (0.20f + darkness))
            ),
            startY = topY,
            endY = topY + height
        ),
        topLeft = Offset(leftX, topY + height * 0.32f),
        size = Size(width, height * 0.76f)
    )
}

@Composable
fun LightningLayer(
    isStormy: Boolean
) {
    if (!isStormy) return

    var flashVisible by remember { mutableStateOf(false) }
    var boltSeed by remember { mutableStateOf(0) }

    LaunchedEffect(isStormy) {
        while (isStormy) {
            delay((1500L..4500L).random())

            boltSeed = (0..100000).random()
            flashVisible = true
            delay(140)
            flashVisible = false

            if ((0..100).random() < 35) {
                delay(90)
                boltSeed = (0..100000).random()
                flashVisible = true
                delay(90)
                flashVisible = false
            }
        }
    }

    if (flashVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.18f))
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val startX = width * (0.24f + (boltSeed % 34) / 100f)
            val forkBias = if (boltSeed % 2 == 0) 1f else -1f
            val boltPoints = mutableListOf(Offset(startX, 0f))
            var currentX = startX
            var currentY = 0f

            repeat(9) { index ->
                val jag = when (index % 4) {
                    0 -> -0.060f
                    1 -> 0.040f
                    2 -> -0.026f
                    else -> 0.070f
                }
                val seedShift = ((boltSeed / (index + 3)) % 19 - 9) / 1000f
                currentX += width * (jag + seedShift) * forkBias
                currentY += height * (0.055f + (index % 3) * 0.014f)
                boltPoints.add(Offset(currentX, currentY))
            }

            val mainPath = Path().apply {
                moveTo(boltPoints.first().x, boltPoints.first().y)
                boltPoints.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }

            val branchPaths = listOf(2, 4, 6).map { branchIndex ->
                val anchor = boltPoints[branchIndex]
                val branchLength = width * (0.12f + (branchIndex % 2) * 0.035f)
                val branchDrop = height * (0.050f + branchIndex * 0.006f)
                val direction = if ((boltSeed + branchIndex) % 2 == 0) 1f else -1f

                Path().apply {
                    moveTo(anchor.x, anchor.y)
                    lineTo(
                        anchor.x + branchLength * direction,
                        anchor.y + branchDrop * 0.45f
                    )
                    lineTo(
                        anchor.x + branchLength * direction * 0.62f,
                        anchor.y + branchDrop
                    )
                }
            }

            drawPath(
                path = mainPath,
                color = Color(0xFFB7D7FF).copy(alpha = 0.34f),
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )

            branchPaths.forEach { branch ->
                drawPath(
                    path = branch,
                    color = Color(0xFFB7D7FF).copy(alpha = 0.22f),
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }

            drawPath(
                path = mainPath,
                color = Color.White.copy(alpha = 0.96f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            branchPaths.forEach { branch ->
                drawPath(
                    path = branch,
                    color = Color.White.copy(alpha = 0.82f),
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun RainLayer(
    weatherDescription: String,
    windSpeed: Float
) {
    val isStormy = isStormWeatherDescription(weatherDescription)
    val shouldRain = isStormy || isRainWeatherDescription(weatherDescription)
    if (!shouldRain) return

    val infinite = rememberInfiniteTransition(label = "rain")

    val rainProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isStormy) 700 else 950,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val density = if (isStormy) 180 else 115
        val slant = (windSpeed / 120f) * 22f

        repeat(density) { i ->
            val xBase = (i * 37f) % width
            val yBase = (i * 83f) % height

            val layerFactor = when (i % 3) {
                0 -> 0.85f
                1 -> 1.0f
                else -> 1.2f
            }

            val dropLength = when (i % 4) {
                0 -> 16f
                1 -> 22f
                2 -> 28f
                else -> 34f
            }

            val alpha = when (i % 4) {
                0 -> 0.18f
                1 -> 0.24f
                2 -> 0.32f
                else -> if (isStormy) 0.48f else 0.36f
            }

            val y = (yBase + rainProgress * height * 1.3f * layerFactor) % height
            val x = (xBase + rainProgress * slant * 40f * layerFactor) % width

            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(x, y),
                end = Offset(
                    x - slant * layerFactor,
                    y + dropLength + (windSpeed / 12f)
                ),
                strokeWidth = if (i % 3 == 0) 1.4f else 2f
            )
        }
    }
}

@Composable
fun SnowLayer(
    weatherDescription: String,
    windSpeed: Float
) {
    val shouldSnow = isSnowWeatherDescription(weatherDescription)
    if (!shouldSnow) return

    val infinite = rememberInfiniteTransition(label = "snow")

    val snowProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "snowProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val drift = (windSpeed / 120f) * 55f

        val flakes = 95

        repeat(flakes) { i ->
            val xBase = (i * 53f) % width
            val yBase = (i * 97f) % height

            val depthFactor = when (i % 3) {
                0 -> 0.75f
                1 -> 1.0f
                else -> 1.2f
            }

            val localOffset = ((i % 5) - 2) * 6f
            val wave = kotlin.math.sin((snowProgress * 2f * Math.PI + i).toFloat()) * (6f + i % 4)

            val y = (yBase + snowProgress * height * 1.05f * depthFactor) % height
            val x = (xBase + drift * snowProgress * depthFactor + wave + localOffset) % width

            val radius = when (i % 4) {
                0 -> 2.2f
                1 -> 3.2f
                2 -> 4.2f
                else -> 5f
            }

            val alpha = when (i % 4) {
                0 -> 0.45f
                1 -> 0.62f
                2 -> 0.78f
                else -> 0.92f
            }

            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}
