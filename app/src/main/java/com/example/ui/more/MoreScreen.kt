package com.example.ui.more

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.calculator.HijriCalendarHelper
import com.example.data.calculator.PrayerSchedule
import com.example.data.calculator.PrayerTimeCalculator
import com.example.data.local.TasbihEntity
import com.example.data.model.Hadith
import com.example.ui.FastingInfo
import com.example.ui.NoorViewModel
import com.example.ui.components.NoorTopAppBar
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

enum class MoreSubScreen {
    MENU,
    FASTING,
    HADITH,
    TASBIH,
    SETTINGS,
    ABOUT
}

@Composable
fun MoreScreen(
    viewModel: NoorViewModel,
    initialSubScreen: MoreSubScreen = MoreSubScreen.MENU
) {
    var currentSubScreen by remember { mutableStateOf(initialSubScreen) }

    when (currentSubScreen) {
        MoreSubScreen.MENU -> {
            MoreMenu(onNavigate = { currentSubScreen = it })
        }
        MoreSubScreen.FASTING -> {
            FastingScreen(viewModel = viewModel, onBack = { currentSubScreen = MoreSubScreen.MENU })
        }
        MoreSubScreen.HADITH -> {
            HadithDetailScreen(viewModel = viewModel, onBack = { currentSubScreen = MoreSubScreen.MENU })
        }
        MoreSubScreen.TASBIH -> {
            TasbihScreen(viewModel = viewModel, onBack = { currentSubScreen = MoreSubScreen.MENU })
        }
        MoreSubScreen.SETTINGS -> {
            SettingsScreen(viewModel = viewModel, onBack = { currentSubScreen = MoreSubScreen.MENU })
        }
        MoreSubScreen.ABOUT -> {
            AboutScreen(onBack = { currentSubScreen = MoreSubScreen.MENU })
        }
    }
}

@Composable
private fun MoreMenu(onNavigate: (MoreSubScreen) -> Unit) {
    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "More Features",
                subtitle = "Islamic Tools & Essentials"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("more_menu_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MoreMenuItem(
                    icon = Icons.Default.NightsStay,
                    title = "Fasting Timetable",
                    subtitle = "Suhoor, Iftar, Ramadan & Sunnah fasts",
                    badge = "Daily & Monthly",
                    onClick = { onNavigate(MoreSubScreen.FASTING) }
                )
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.FormatQuote,
                    title = "Daily Hadith",
                    subtitle = "Authentic Prophetic traditions with audio",
                    badge = "Daily Rotation",
                    onClick = { onNavigate(MoreSubScreen.HADITH) }
                )
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.TouchApp,
                    title = "Digital Tasbih",
                    subtitle = "Dhikr counter with haptic feedback & presets",
                    badge = "SubhanAllah",
                    onClick = { onNavigate(MoreSubScreen.TASBIH) }
                )
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Application Settings",
                    subtitle = "Calculation methods, notifications & language",
                    onClick = { onNavigate(MoreSubScreen.SETTINGS) }
                )
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.Info,
                    title = "About Noor",
                    subtitle = "App dedication, sources & privacy policy",
                    onClick = { onNavigate(MoreSubScreen.ABOUT) }
                )
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(11.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. FASTING TIMETABLE SCREEN
// -------------------------------------------------------------
@Composable
fun FastingScreen(viewModel: NoorViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val fastingInfo by viewModel.fastingInfo.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "Fasting Timetable",
                subtitle = "${settings.cityName} • Suhoor & Iftar",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Fasting Status Card
            item {
                fastingInfo?.let { fasting ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldDeep)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (fasting.isCurrentlyFasting) "CURRENTLY FASTING" else "FASTING COUNTDOWN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IslamicGoldBright,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = fasting.countdownFormatted,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )

                            Text(
                                text = fasting.countdownLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Today's Suhoor", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text(fasting.todaySuhoorTime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                VerticalDivider(modifier = Modifier.height(30.dp), color = Color.White.copy(alpha = 0.2f))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Today's Iftar", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text(fasting.todayIftarTime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = IslamicGoldBright)
                                }
                                VerticalDivider(modifier = Modifier.height(30.dp), color = Color.White.copy(alpha = 0.2f))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Duration", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text(fasting.durationFormatted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Tomorrow's Schedule
            item {
                fastingInfo?.let { fasting ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Tomorrow's Fasting Schedule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Suhoor ends at ${fasting.tomorrowSuhoorTime} • Iftar at ${fasting.tomorrowIftarTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Authentic Fasting Du'as
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Du'a for Fasting (Niyyah & Iftar)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Intention
                        Text("Intention for Fasting (Suhoor):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "وَبِصَوْمِ غَدٍ نَّوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
                            fontFamily = AmiriFontFamily,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "\"I intend to keep the fast tomorrow for the month of Ramadan.\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Iftar Du'a
                        Text("Du'a at Breaking the Fast (Iftar):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                            fontFamily = AmiriFontFamily,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "\"The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.\" (Abu Dawud)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Recommended Sunnah Fasts
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sunnah Fasting Days",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Mondays & Thursdays every week.\n" +
                                   "• White Days (Ayyam al-Beed): 13th, 14th, and 15th of every Hijri month.\n" +
                                   "• Day of Arafah (9th Dhul-Hijjah) & Ashura (10th Muharram).\n" +
                                   "• Six days of Shawwal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. DAILY HADITH FULL SCREEN
// -------------------------------------------------------------
@Composable
fun HadithDetailScreen(viewModel: NoorViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val hadith by viewModel.dailyHadith.collectAsStateWithLifecycle()
    val isTtsReady by viewModel.isTtsReady.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "Daily Hadith",
                subtitle = "${hadith.book} • ${hadith.hadithNumber}",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshDailyHadith() },
                        modifier = Modifier.testTag("more_refresh_hadith_top_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Hadith", tint = MaterialTheme.colorScheme.primary)
                    }

                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, "${hadith.textAr}\n\n\"${hadith.textEn}\"\n\n— ${hadith.book} (${hadith.hadithNumber}) [${hadith.grading}]")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Hadith"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = hadith.grading,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "Narrated by ${hadith.narrator}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Arabic Original
                        Text(
                            text = hadith.textAr,
                            fontFamily = AmiriFontFamily,
                            fontSize = 24.sp,
                            lineHeight = 40.sp,
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))

                        // English Translation
                        Text(
                            text = "English Translation:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${hadith.textEn}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Malay translation if available
                        if (hadith.textMs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Bahasa Melayu:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${hadith.textMs}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Bengali translation if available
                        if (hadith.textBn.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "বাংলা অনুবাদ:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${hadith.textBn}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Commentary & Reflection
                        Text(
                            text = "Scholarly Reflection:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = hadith.commentary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Audio Listen Button
                        Button(
                            onClick = { viewModel.playHadithTts(hadith.textEn) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Listen to Translation Audio")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Refresh / Discover Another Hadith Button
                        OutlinedButton(
                            onClick = { viewModel.refreshDailyHadith() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("more_discover_hadith_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Discover Another Hadith")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. DIGITAL TASBIH SCREEN
// -------------------------------------------------------------
@Composable
fun TasbihScreen(viewModel: NoorViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val tasbihList by viewModel.tasbihList.collectAsStateWithLifecycle()
    var selectedIndex by remember { mutableIntStateOf(0) }

    val activeItem = remember(tasbihList, selectedIndex) {
        if (tasbihList.isNotEmpty()) tasbihList[selectedIndex.coerceIn(0, tasbihList.lastIndex)]
        else null
    }

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "Digital Tasbih",
                subtitle = "Touch counter for Dhikr & Remembrance",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Dhikr Selector Chips
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 140.dp)
            ) {
                items(tasbihList.indices.toList()) { idx ->
                    val item = tasbihList[idx]
                    val isSelected = idx == selectedIndex

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedIndex = idx }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.phraseEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = item.meaningEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = item.phraseAr,
                                fontFamily = AmiriFontFamily,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Big Interactive Bead Tap Circle
            activeItem?.let { dhikr ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = dhikr.phraseAr,
                        fontFamily = AmiriFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dhikr.phraseEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = dhikr.meaningEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large Tap Button
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        EmeraldDeep
                                    )
                                )
                            )
                            .clickable { viewModel.tapTasbih(dhikr.dhikrId) }
                            .testTag("tasbih_tap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dhikr.currentCount}",
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "/ ${dhikr.targetCount}",
                                fontSize = 16.sp,
                                color = IslamicGoldBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Completed Laps: ${dhikr.totalLaps} • Lifetime: ${dhikr.lifetimeCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bottom Action Buttons (Reset, Target Change)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetTasbih(dhikr.dhikrId) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Count")
                    }

                    Button(
                        onClick = {
                            val nextTarget = if (dhikr.targetCount == 33) 99 else if (dhikr.targetCount == 99) 100 else 33
                            viewModel.updateTasbihTarget(dhikr.dhikrId, nextTarget)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Adjust, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Target: ${dhikr.targetCount}")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. SETTINGS SCREEN
// -------------------------------------------------------------
@Composable
fun SettingsScreen(viewModel: NoorViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "Settings",
                subtitle = "Preferences & Configurations",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Notifications Group
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Notifications & Alarms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("All Prayer Notifications", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = settings.notificationsEnabled,
                                onCheckedChange = { viewModel.togglePrayerNotification("all", it) }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Daily Hadith Notification", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = settings.dailyHadithNotification,
                                onCheckedChange = {
                                    scope.launch {
                                        viewModel.preferencesManager.updateHadithNotification(it, settings.dailyHadithHour, settings.dailyHadithMinute)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Theme Setting
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))

                        listOf("system" to "System Default", "light" to "Warm Ivory (Light)", "dark" to "Islamic Night (Dark)").forEach { (key, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch { viewModel.preferencesManager.updateThemeMode(key) }
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = settings.themeMode == key, onClick = {
                                    scope.launch { viewModel.preferencesManager.updateThemeMode(key) }
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ABOUT SCREEN & PRIVACY POLICY
// -------------------------------------------------------------
@Composable
fun AboutScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "About Noor",
                subtitle = "Purpose, Authenticity & Privacy",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Noor – Your Daily Islamic Companion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Noor is built with love, reverence, and devotion to provide every Muslim worldwide with a serene, modern, distraction-free companion for their daily ibadah.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Verified Sources & Integrity:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Prayer Times: Exact solar trigonometric algorithms following verified worldwide standards (JAKIM, MUIS, MWL, ISNA, Umm Al-Qura, Karachi).\n" +
                                   "• Quran: Uthmani script with authentic translations (Sahih International, Abdullah Basmeih, Muhiuddin Khan, Kemenag).\n" +
                                   "• Hadith: Authentic collections from Sahih al-Bukhari, Sahih Muslim, and recognized Sunan.\n" +
                                   "• Qibla: Great Circle spherical trigonometry referencing the Holy Kaaba (21.4225° N, 39.8262° E).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Privacy Policy & Respect", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Location Privacy: Your GPS location is processed purely on-device to calculate prayer times and Qibla bearing. Noor never shares or sells your location.\n" +
                                   "• Offline Usability: Core prayer calculations, tasbih counts, and Quran bookmarks work completely offline.\n" +
                                   "• No Forced Account: Noor does not require account registration or tracking cookies.\n" +
                                   "• Peace of Mind: Ad-free, peaceful, and respectful of your sacred time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
