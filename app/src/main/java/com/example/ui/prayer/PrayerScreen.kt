package com.example.ui.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.calculator.*
import com.example.ui.NoorViewModel
import com.example.ui.components.CitySelectionModal
import com.example.ui.components.NoorTopAppBar
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.IslamicGold
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PrayerScreen(
    viewModel: NoorViewModel,
    onBack: (() -> Unit)? = null
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val schedule by viewModel.currentSchedule.collectAsStateWithLifecycle()
    val nextPrayer by viewModel.nextPrayer.collectAsStateWithLifecycle()

    var showLocationModal by remember { mutableStateOf(false) }
    var showMethodModal by remember { mutableStateOf(false) }
    var showAdjustModal by remember { mutableStateOf(false) }
    var showMonthlyModal by remember { mutableStateOf(false) }

    if (showLocationModal) {
        CitySelectionModal(
            cities = viewModel.presetCities,
            onSelectCity = { viewModel.selectPresetCity(it) },
            onUseGps = { viewModel.requestGpsLocation() },
            onDismiss = { showLocationModal = false }
        )
    }

    if (showMethodModal) {
        CalculationMethodDialog(
            currentMethod = settings.calculationMethod,
            currentAsr = settings.asrMethod,
            onSelectMethod = { viewModel.setCalculationMethod(it) },
            onSelectAsr = { viewModel.setAsrMethod(it) },
            onDismiss = { showMethodModal = false }
        )
    }

    if (showAdjustModal) {
        ManualAdjustmentDialog(
            fajr = settings.fajrAdjust,
            dhuhr = settings.dhuhrAdjust,
            asr = settings.asrAdjust,
            maghrib = settings.maghribAdjust,
            isha = settings.ishaAdjust,
            onSave = { f, d, a, m, i ->
                viewModel.setAdjustments(f, d, a, m, i)
            },
            onDismiss = { showAdjustModal = false }
        )
    }

    if (showMonthlyModal) {
        MonthlyPrayerScheduleDialog(
            settings = settings,
            onDismiss = { showMonthlyModal = false }
        )
    }

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = "Prayer Times",
                subtitle = "${settings.cityName}, ${settings.countryName}",
                actions = {
                    IconButton(onClick = { showLocationModal = true }) {
                        Icon(
                            imageVector = Icons.Default.EditLocation,
                            contentDescription = "Change location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showMonthlyModal = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Monthly schedule",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                .testTag("prayer_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Next Prayer Banner
            item {
                nextPrayer?.let { next ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "NEXT UPCOMING PRAYER",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = next.nameEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = next.nameAr,
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = next.timeFormatted,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Remaining",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = next.countdownFormatted,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Timetable Card
            item {
                schedule?.let { sched ->
                    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Today's Timetable",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val prayers = listOf(
                                Triple("Imsak", "الإمساك", sched.imsak) to false,
                                Triple("Fajr", "الفجر", sched.fajr) to settings.fajrNotification,
                                Triple("Sunrise", "الشروق", sched.sunrise) to false,
                                Triple("Dhuhr", "الظهر", sched.dhuhr) to settings.dhuhrNotification,
                                Triple("Asr", "العصر", sched.asr) to settings.asrNotification,
                                Triple("Maghrib", "المغرب", sched.maghrib) to settings.maghribNotification,
                                Triple("Isha", "العشاء", sched.isha) to settings.ishaNotification,
                                Triple("Midnight", "منتصف الليل", sched.midnight) to false
                            )

                            prayers.forEach { (info, isNotifOn) ->
                                val (nameEn, nameAr, cal) = info
                                val isNext = nameEn.equals(nextPrayer?.nameEn ?: "", ignoreCase = true)
                                val isObligatory = nameEn in listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isNext) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                            else Color.Transparent
                                        )
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
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
                                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = nameEn,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                                                color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (!isObligatory) {
                                                Text(
                                                    text = if (nameEn == "Imsak") "Suhoor Cut-off" else if (nameEn == "Sunrise") "Shurooq" else "Tahajjud / Qiyam",
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
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Text(
                                            text = timeFormat.format(cal.time),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )

                                        if (isObligatory) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    viewModel.togglePrayerNotification(nameEn, !isNotifOn)
                                                },
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
            }

            // Calculation Method Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column {
                                Text(
                                    text = "Calculation Settings",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mathematical solar astronomy method",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { showMethodModal = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Change")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SettingSummaryRow(
                            title = "Authority Method",
                            value = settings.calculationMethod.title
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                        SettingSummaryRow(
                            title = "Asr Juristic Method",
                            value = settings.asrMethod.title
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showAdjustModal = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Manual Time Adjustments (+/- min)")
                        }
                    }
                }
            }

            // System Notification & Battery Advisory
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Notice on Alarm & Notification Delivery",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Exact alarm scheduling requires system notification permission and exemption from device aggressive battery-saving modes for on-time delivery.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSummaryRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CalculationMethodDialog(
    currentMethod: CalculationMethod,
    currentAsr: AsrJuristicMethod,
    onSelectMethod: (CalculationMethod) -> Unit,
    onSelectAsr: (AsrJuristicMethod) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 560.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Prayer Calculation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select authoritative method for your region",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Calculation Authority",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(CalculationMethod.entries) { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectMethod(method) }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = method == currentMethod,
                                onClick = { onSelectMethod(method) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = method.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (method == currentMethod) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "Fajr: ${method.fajrAngle}° | Isha: ${if (method.ishaAngle > 0) "${method.ishaAngle}°" else "${method.ishaMinutesAfterMaghrib}m"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Asr Juristic Method",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                AsrJuristicMethod.entries.forEach { asr ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAsr(asr) }
                            .padding(vertical = 6.dp)
                    ) {
                        RadioButton(
                            selected = asr == currentAsr,
                            onClick = { onSelectAsr(asr) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = asr.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (asr == currentAsr) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun ManualAdjustmentDialog(
    fajr: Int,
    dhuhr: Int,
    asr: Int,
    maghrib: Int,
    isha: Int,
    onSave: (Int, Int, Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var adjFajr by remember { mutableIntStateOf(fajr) }
    var adjDhuhr by remember { mutableIntStateOf(dhuhr) }
    var adjAsr by remember { mutableIntStateOf(asr) }
    var adjMaghrib by remember { mutableIntStateOf(maghrib) }
    var adjIsha by remember { mutableIntStateOf(isha) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Manual Minute Adjustments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fine-tune local prayer times (+/- minutes)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                AdjustmentCounterRow("Fajr", adjFajr) { adjFajr = it }
                AdjustmentCounterRow("Dhuhr", adjDhuhr) { adjDhuhr = it }
                AdjustmentCounterRow("Asr", adjAsr) { adjAsr = it }
                AdjustmentCounterRow("Maghrib", adjMaghrib) { adjMaghrib = it }
                AdjustmentCounterRow("Isha", adjIsha) { adjIsha = it }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(adjFajr, adjDhuhr, adjAsr, adjMaghrib, adjIsha)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Adjustments")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdjustmentCounterRow(name: String, value: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onChange((value - 1).coerceAtLeast(-30)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = "${if (value > 0) "+$value" else "$value"} m",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(48.dp),
                color = if (value != 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { onChange((value + 1).coerceAtMost(30)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
fun MonthlyPrayerScheduleDialog(
    settings: com.example.data.local.UserSettings,
    onDismiss: () -> Unit
) {
    val currentMonthCalendar = remember { Calendar.getInstance() }
    val daysInMonth = currentMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthName = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonthCalendar.time) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    val monthlyList = remember(settings) {
        val list = mutableListOf<PrayerSchedule>()
        for (day in 1..daysInMonth) {
            val cal = (currentMonthCalendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, day)
            }
            val sched = PrayerTimeCalculator.calculate(
                calendar = cal,
                latitude = settings.latitude,
                longitude = settings.longitude,
                method = settings.calculationMethod,
                asrMethod = settings.asrMethod,
                fajrAdjustmentMin = settings.fajrAdjust,
                dhuhrAdjustmentMin = settings.dhuhrAdjust,
                asrAdjustmentMin = settings.asrAdjust,
                maghribAdjustmentMin = settings.maghribAdjust,
                ishaAdjustmentMin = settings.ishaAdjust
            )
            list.add(sched)
        }
        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Prayer Calendar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$monthName • ${settings.cityName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Day", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                    Text("Fajr", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Dhuhr", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Asr", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Maghrib", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Isha", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(monthlyList) { sched ->
                        val day = sched.date.get(Calendar.DAY_OF_MONTH)
                        val isToday = day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$day",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(0.8f)
                            )
                            Text(text = timeFormat.format(sched.fajr.time), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f))
                            Text(text = timeFormat.format(sched.dhuhr.time), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f))
                            Text(text = timeFormat.format(sched.asr.time), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f))
                            Text(text = timeFormat.format(sched.maghrib.time), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f))
                            Text(text = timeFormat.format(sched.isha.time), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f))
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close Calendar")
                }
            }
        }
    }
}
