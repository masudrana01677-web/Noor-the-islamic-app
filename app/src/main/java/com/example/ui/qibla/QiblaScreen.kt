package com.example.ui.qibla

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.NoorViewModel
import com.example.ui.components.NoorTopAppBar
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    viewModel: NoorViewModel
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val qiblaState by viewModel.qiblaState.collectAsStateWithLifecycle()

    // Smooth compass rotation animation
    val animatedHeading by animateFloatAsState(
        targetValue = qiblaState.userHeading,
        animationSpec = tween(durationMillis = 150),
        label = "compassHeading"
    )

    // Needle points towards Qibla relative to device heading:
    // angle = qiblaBearing - userHeading
    val needleAngle = ((qiblaState.qiblaBearing - animatedHeading + 360) % 360).toFloat()

    val ringColor by animateColorAsState(
        targetValue = if (qiblaState.isAligned) IslamicGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        label = "ringColor"
    )

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "Qibla Direction",
                subtitle = "${settings.cityName} • ${String.format("%.1f", qiblaState.distanceKm)} km to Kaaba"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("qibla_screen_content"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (qiblaState.isAligned) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (qiblaState.isAligned) Icons.Default.CheckCircle else Icons.Default.Explore,
                                contentDescription = null,
                                tint = if (qiblaState.isAligned) IslamicGold else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (qiblaState.isAligned) "Aligned with Qibla!" else "Align with Qibla",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (qiblaState.isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (qiblaState.isAligned) "You are facing directly towards the Kaaba"
                            else "Rotate your phone until the golden needle points up",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${qiblaState.qiblaBearing.toInt()}°",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Interactive Compass Dial
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .testTag("qibla_compass_dial"),
                contentAlignment = Alignment.Center
            ) {
                // Background dial canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2 - 12.dp.toPx()

                    // Outer circular background
                    drawCircle(
                        color = Color(0xFF162E25),
                        radius = radius,
                        center = center
                    )

                    // Outer decorative ring
                    drawCircle(
                        color = ringColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Degree ticks
                    for (deg in 0 until 360 step 30) {
                        val angleRad = Math.toRadians((deg - animatedHeading).toDouble())
                        val isCardinal = deg % 90 == 0
                        val tickLength = if (isCardinal) 16.dp.toPx() else 8.dp.toPx()
                        val startX = center.x + (radius - tickLength) * sin(angleRad).toFloat()
                        val startY = center.y - (radius - tickLength) * cos(angleRad).toFloat()
                        val endX = center.x + radius * sin(angleRad).toFloat()
                        val endY = center.y - radius * cos(angleRad).toFloat()

                        drawLine(
                            color = if (isCardinal) IslamicGoldBright else Color.White.copy(alpha = 0.4f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isCardinal) 3.dp.toPx() else 1.5.dp.toPx()
                        )
                    }
                }

                // Rotating Needle Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(needleAngle)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val length = size.minDimension / 2 - 28.dp.toPx()

                    // Golden Qibla Pointer Needle
                    val needlePath = Path().apply {
                        moveTo(center.x, center.y - length) // Point tip
                        lineTo(center.x + 14.dp.toPx(), center.y - 10.dp.toPx())
                        lineTo(center.x, center.y + 24.dp.toPx())
                        lineTo(center.x - 14.dp.toPx(), center.y - 10.dp.toPx())
                        close()
                    }

                    drawPath(
                        path = needlePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(IslamicGoldBright, IslamicGold, Color(0xFF7A5812)),
                            startY = center.y - length,
                            endY = center.y + 24.dp.toPx()
                        )
                    )

                    // Center Hub
                    drawCircle(
                        color = EmeraldDeep,
                        radius = 16.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = IslamicGold,
                        radius = 8.dp.toPx(),
                        center = center
                    )
                }

                // Center Kaaba Emblem
                Surface(
                    shape = CircleShape,
                    color = if (qiblaState.isAligned) IslamicGold else EmeraldDeep,
                    modifier = Modifier.size(52.dp),
                    tonalElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Kaaba Direction",
                            tint = if (qiblaState.isAligned) EmeraldDeep else IslamicGoldBright,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calibration & Magnetic Interference Notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calibration & Accuracy Guidance",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Wave your phone gently in a figure-8 motion to calibrate the magnetometer.\n" +
                               "2. Keep the phone flat and away from magnets, metal laptop covers, or wireless chargers for reliable accuracy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
