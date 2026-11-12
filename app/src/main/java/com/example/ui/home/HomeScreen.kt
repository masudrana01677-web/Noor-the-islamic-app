package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.calculator.HijriCalendarHelper
import com.example.data.calculator.PrayerSchedule
import com.example.data.local.ReadingProgressEntity
import com.example.data.model.Hadith
import com.example.ui.FastingInfo
import com.example.ui.NextPrayerInfo
import com.example.ui.NoorViewModel
import com.example.ui.components.ArabicTextView
import com.example.ui.components.CitySelectionModal
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: NoorViewModel,
    onNavigateToPrayer: () -> Unit,
    onNavigateToQuran: (surahNumber: Int) -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToFasting: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val schedule by viewModel.currentSchedule.collectAsStateWithLifecycle()
    val nextPrayer by viewModel.nextPrayer.collectAsStateWithLifecycle()
    val fastingInfo by viewModel.fastingInfo.collectAsStateWithLifecycle()
    val dailyHadith by viewModel.dailyHadith.collectAsStateWithLifecycle()
    val lastRead by viewModel.readingProgress.collectAsStateWithLifecycle()

    var showLocationDialog by remember { mutableStateOf(false) }

    val todayCalendar = remember { Calendar.getInstance() }
    val gregorianDateStr = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(todayCalendar.time)
    }
    val hijriDate = remember {
        HijriCalendarHelper.getHijriDate(todayCalendar)
    }
    val greeting = remember {
        HijriCalendarHelper.getGreeting(todayCalendar.get(Calendar.HOUR_OF_DAY))
    }

    if (showLocationDialog) {
        CitySelectionModal(
            cities = viewModel.presetCities,
            onSelectCity = { viewModel.selectPresetCity(it) },
            onUseGps = { viewModel.requestGpsLocation() },
            onDismiss = { showLocationDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Header Bar
        item {
            HomeHeader(
                cityName = settings.cityName,
                countryName = settings.countryName,
                isAutoGps = settings.isAutoGps,
                onLocationClick = { showLocationDialog = true },
                onSettingsClick = onNavigateToSettings
            )
        }

        // 2. Islamic Date & Greeting Card
        item {
            DateAndGreetingCard(
                gregorianDate = gregorianDateStr,
                hijriDateStr = "${hijriDate.day} ${hijriDate.monthNameEn} ${hijriDate.year} AH",
                hijriMonthAr = hijriDate.monthNameAr,
                greetingTitle = greeting.first,
                greetingSubtitle = greeting.second,
                specialOccasion = hijriDate.specialOccasion
            )
        }

        // 3. Next Prayer Card (Hero Card)
        item {
            nextPrayer?.let { next ->
                NextPrayerHeroCard(
                    nextPrayer = next,
                    notificationsEnabled = settings.notificationsEnabled,
                    onToggleAlarm = {
                        viewModel.togglePrayerNotification("all", !settings.notificationsEnabled)
                    },
                    onViewFullSchedule = onNavigateToPrayer
                )
            }
        }

        // 4. Daily Prayer Timetable
        item {
            schedule?.let { sched ->
                PrayerTimetableCard(
                    schedule = sched,
                    nextPrayerName = nextPrayer?.nameEn ?: "",
                    settings = settings,
                    onTogglePrayerAlarm = { prayerKey, enabled ->
                        viewModel.togglePrayerNotification(prayerKey, enabled)
                    },
                    onViewFullTimetable = onNavigateToPrayer
                )
            }
        }

        // 5. Fasting Quick Snapshot
        item {
            fastingInfo?.let { fasting ->
                FastingSnapshotCard(
                    fastingInfo = fasting,
                    onOpenFasting = onNavigateToFasting
                )
            }
        }

        // 6. Last Read Quran Card
        item {
            LastReadQuranCard(
                lastRead = lastRead,
                onContinueReading = { surahNum ->
                    onNavigateToQuran(surahNum)
                }
            )
        }

        // 7. Daily Hadith Card
        item {
            DailyHadithCard(
                hadith = dailyHadith,
                onPlayAudio = { viewModel.playHadithTts(dailyHadith.textEn) },
                onOpenHadith = onNavigateToHadith,
                onRefreshHadith = { viewModel.refreshDailyHadith() }
            )
        }
    }
}

@Composable
private fun HomeHeader(
    cityName: String,
    countryName: String,
    isAutoGps: Boolean,
    onLocationClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Noor",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(IslamicGold)
                )
            }
            Text(
                text = "Your Daily Islamic Companion",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onLocationClick() }
                    .testTag("home_location_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isAutoGps) Icons.Default.MyLocation else Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cityName.take(14),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .testTag("home_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DateAndGreetingCard(
    gregorianDate: String,
    hijriDateStr: String,
    hijriMonthAr: String,
    greetingTitle: String,
    greetingSubtitle: String,
    specialOccasion: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = gregorianDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hijriDateStr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = hijriMonthAr,
                    fontFamily = AmiriFontFamily,
                    fontSize = 24.sp,
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                )
            }

            specialOccasion?.let { occasion ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = occasion,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = greetingTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = greetingSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NextPrayerHeroCard(
    nextPrayer: NextPrayerInfo,
    notificationsEnabled: Boolean,
    onToggleAlarm: () -> Unit,
    onViewFullSchedule: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("next_prayer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldDeep)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Background art
            Image(
                painter = painterResource(id = R.drawable.img_islamic_hero),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp)),
                alpha = 0.28f
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EmeraldDeep.copy(alpha = 0.75f),
                                EmeraldDeep.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "UPCOMING PRAYER",
                            style = MaterialTheme.typography.labelSmall,
                            color = IslamicGoldBright,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onToggleAlarm,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("toggle_prayer_alarm_button")
                    ) {
                        Icon(
                            imageVector = if (notificationsEnabled) Icons.Outlined.Notifications else Icons.Outlined.NotificationsOff,
                            contentDescription = "Toggle Alarm",
                            tint = if (notificationsEnabled) IslamicGoldBright else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = nextPrayer.nameEn,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = nextPrayer.nameAr,
                                fontFamily = AmiriFontFamily,
                                fontSize = 28.sp,
                                color = IslamicGoldBright
                            )
                        }

                        Text(
                            text = nextPrayer.timeFormatted,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Starts in",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = nextPrayer.countdownFormatted,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = IslamicGoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar until next prayer
                LinearProgressIndicator(
                    progress = { nextPrayer.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = IslamicGold,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewFullSchedule() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View complete daily timetable",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = IslamicGoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerTimetableCard(
    schedule: PrayerSchedule,
    nextPrayerName: String,
    settings: com.example.data.local.UserSettings,
    onTogglePrayerAlarm: (String, Boolean) -> Unit,
    onViewFullTimetable: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    val prayers = listOf(
        Triple("Fajr", "الفجر", schedule.fajr) to settings.fajrNotification,
        Triple("Sunrise", "الشروق", schedule.sunrise) to false,
        Triple("Dhuhr", "الظهر", schedule.dhuhr) to settings.dhuhrNotification,
        Triple("Asr", "العصر", schedule.asr) to settings.asrNotification,
        Triple("Maghrib", "المغرب", schedule.maghrib) to settings.maghribNotification,
        Triple("Isha", "العشاء", schedule.isha) to settings.ishaNotification
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Prayer Timetable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = onViewFullTimetable,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Details", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            prayers.forEach { (prayerInfo, isNotifOn) ->
                val (nameEn, nameAr, cal) = prayerInfo
                val isNext = nameEn.equals(nextPrayerName, ignoreCase = true)
                val isSunrise = nameEn == "Sunrise"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isNext) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else Color.Transparent
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isNext) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = nameEn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSunrise) {
                                Text(
                                    text = "Shurooq",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = nameAr,
                            fontFamily = AmiriFontFamily,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = timeFormat.format(cal.time),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )

                        if (!isSunrise) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onTogglePrayerAlarm(nameEn, !isNotifOn) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isNotifOn) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                    contentDescription = "Toggle alarm for $nameEn",
                                    tint = if (isNotifOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FastingSnapshotCard(
    fastingInfo: FastingInfo,
    onOpenFasting: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onOpenFasting() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
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
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = "Fasting",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Fasting Timetable",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Suhoor ${fastingInfo.todaySuhoorTime} • Iftar ${fastingInfo.todayIftarTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = fastingInfo.countdownFormatted,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = fastingInfo.countdownLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LastReadQuranCard(
    lastRead: ReadingProgressEntity?,
    onContinueReading: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onContinueReading(lastRead?.surahNumber ?: 1) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    color = IslamicGold.copy(alpha = 0.12f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Quran",
                        tint = IslamicGold,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Quran Reader",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (lastRead != null) "Surah ${lastRead.surahNameEn} • Ayah ${lastRead.ayahNumber}" else "Read Holy Quran (114 Surahs)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { onContinueReading(lastRead?.surahNumber ?: 1) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (lastRead != null) "Resume" else "Open",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun DailyHadithCard(
    hadith: Hadith,
    onPlayAudio: () -> Unit,
    onOpenHadith: () -> Unit,
    onRefreshHadith: () -> Unit
) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 400),
        label = "hadith_refresh_spin"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onOpenHadith() }
            .testTag("daily_hadith_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hadith of the Day",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = hadith.grading,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            rotationAngle += 360f
                            onRefreshHadith()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("refresh_hadith_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh for New Hadith",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(animatedRotation)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = hadith.textAr,
                fontFamily = AmiriFontFamily,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${hadith.textEn}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hadith.textBn.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = hadith.textBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${hadith.book} • ${hadith.hadithNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = {
                            rotationAngle += 360f
                            onRefreshHadith()
                        },
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("new_hadith_chip_button"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier
                                .size(13.dp)
                                .rotate(animatedRotation),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Hadith",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen to Hadith",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenHadith,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Hadith",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
