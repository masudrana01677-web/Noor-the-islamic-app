package com.example.ui.quran

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Ayah
import com.example.data.model.ReciterCatalog
import com.example.data.model.Surah
import com.example.ui.NoorViewModel
import com.example.ui.components.ArabicTextView
import com.example.ui.components.NoorTopAppBar
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright
import kotlinx.coroutines.launch

@Composable
fun SurahDetailScreen(
    surahNumber: Int,
    viewModel: NoorViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val verses by viewModel.activeSurahVerses.collectAsStateWithLifecycle()
    val isLoading by viewModel.isSurahLoading.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val playerState by viewModel.audioPlayer.playerState.collectAsStateWithLifecycle()
    val cachedSurahs by viewModel.cachedSurahNumbers.collectAsStateWithLifecycle()
    val isCached = cachedSurahs.contains(surahNumber)

    val surah = remember(surahNumber) {
        viewModel.surahList.firstOrNull { it.number == surahNumber } ?: viewModel.surahList[0]
    }

    var showLanguageModal by remember { mutableStateOf(false) }
    var showReciterModal by remember { mutableStateOf(false) }

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    // Auto-scroll to active playing Ayah
    LaunchedEffect(playerState.currentAyahNumber, playerState.currentSurahNumber) {
        if (playerState.isPlaying && playerState.currentSurahNumber == surahNumber) {
            val targetIdx = (playerState.currentAyahNumber - 1).coerceAtLeast(0)
            if (targetIdx < verses.size) {
                listState.animateScrollToItem(targetIdx)
            }
        }
    }

    if (showLanguageModal) {
        TranslationLanguageDialog(
            currentLang = settings.quranLanguage,
            onSelectLang = { viewModel.setQuranLanguage(it) },
            onDismiss = { showLanguageModal = false }
        )
    }

    if (showReciterModal) {
        ReciterSelectionDialog(
            currentReciterId = settings.activeReciterId,
            onSelectReciter = { viewModel.setReciter(it) },
            onDismiss = { showReciterModal = false }
        )
    }

    Scaffold(
        topBar = {
            NoorTopAppBar(
                title = surah.nameEn,
                subtitle = "${surah.revelationType.label} • ${surah.totalVerses} Verses",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                actions = {
                    // Font Size - / +
                    IconButton(
                        onClick = {
                            viewModel.setArabicFontSize((settings.arabicFontSizeSp - 2f).coerceAtLeast(20f))
                        }
                    ) {
                        Text("A-", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = {
                            viewModel.setArabicFontSize((settings.arabicFontSizeSp + 2f).coerceAtMost(40f))
                        }
                    ) {
                        Text("A+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    // Bengali Pronunciation Toggle
                    IconButton(
                        onClick = {
                            viewModel.toggleBengaliPronunciation(!settings.showBengaliPronunciation)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "বাংলা উচ্চারণ (Bengali Pronunciation)",
                            tint = if (settings.showBengaliPronunciation) IslamicGold else MaterialTheme.colorScheme.outline
                        )
                    }
                    // Language Switcher
                    IconButton(onClick = { showLanguageModal = true }) {
                        Icon(Icons.Default.Translate, contentDescription = "Translation Language", tint = MaterialTheme.colorScheme.primary)
                    }
                    // Reciter Picker
                    IconButton(onClick = { showReciterModal = true }) {
                        Icon(Icons.Default.Person, contentDescription = "Select Reciter", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            QuranAudioBottomBar(
                playerState = playerState,
                surah = surah,
                onTogglePlay = { viewModel.audioPlayer.togglePlayPause() },
                onNext = { viewModel.audioPlayer.nextAyah() },
                onPrev = { viewModel.audioPlayer.prevAyah() },
                onToggleRepeatAyah = { viewModel.audioPlayer.toggleRepeatAyah() },
                onToggleRepeatSurah = { viewModel.audioPlayer.toggleRepeatSurah() },
                onSeek = { viewModel.audioPlayer.seekTo(it) },
                onSetSpeed = { viewModel.audioPlayer.setPlaybackSpeed(it) },
                onOpenReciters = { showReciterModal = true }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .testTag("surah_verses_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Banner
                item {
                    SurahHeaderBanner(surah = surah, isCached = isCached)
                }

                // Bengali Pronunciation Recitation Switch Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (settings.showBengaliPronunciation)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (settings.showBengaliPronunciation) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (settings.showBengaliPronunciation) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = if (settings.showBengaliPronunciation) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "বাংলা উচ্চারণ সহ তিলাওয়াত",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (settings.showBengaliPronunciation) "উচ্চারণ দৃশ্যমান (Recitation enabled)" else "উচ্চারণ দেখতে টগল করুন (Tap to enable)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = settings.showBengaliPronunciation,
                                onCheckedChange = { viewModel.toggleBengaliPronunciation(it) },
                                thumbContent = if (settings.showBengaliPronunciation) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // Bismillah (except for Surah 9 At-Tawbah)
                if (surah.number != 9) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontFamily = AmiriFontFamily,
                                fontSize = (settings.arabicFontSizeSp * 1.05f).sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            if (settings.showBengaliPronunciation) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "বিসমিল্লাহির রাহমানির রাহিম",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Ayahs
                items(verses, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                    val isPlaying = playerState.isPlaying &&
                            playerState.currentSurahNumber == surah.number &&
                            playerState.currentAyahNumber == ayah.ayahNumber
                    val isBookmarked = bookmarks.any { it.surahNumber == surah.number && it.ayahNumber == ayah.ayahNumber }

                    AyahCard(
                        ayah = ayah,
                        surah = surah,
                        languageCode = settings.quranLanguage,
                        fontSizeSp = settings.arabicFontSizeSp,
                        showBengaliPronunciation = settings.showBengaliPronunciation,
                        isPlaying = isPlaying,
                        isBookmarked = isBookmarked,
                        onPlayAudio = {
                            viewModel.playAyahAudio(surah.number, ayah.ayahNumber, surah.totalVerses)
                        },
                        onToggleBookmark = {
                            viewModel.toggleBookmark(ayah, surah)
                        },
                        onShare = {
                            val shareText = buildString {
                                append(ayah.textAr)
                                if (settings.showBengaliPronunciation) {
                                    append("\n\nবাংলা উচ্চারণ: ")
                                    append(ayah.getBengaliPronunciation())
                                }
                                append("\n\nঅনুবাদ: \"")
                                append(ayah.getTranslation(settings.quranLanguage))
                                append("\"\n\n— Quran ${surah.nameEn} [${surah.number}:${ayah.ayahNumber}]")
                            }
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Ayah"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SurahHeaderBanner(
    surah: Surah,
    isCached: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = surah.nameAr,
                fontFamily = AmiriFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${surah.number}. ${surah.nameEn} (${surah.translationEn})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${surah.revelationType.label} • ${surah.totalVerses} Ayahs • Juz ${surah.juzStart}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            if (isCached) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Saved in Room Database",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saved Offline (Room Database)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AyahCard(
    ayah: Ayah,
    surah: Surah,
    languageCode: String,
    fontSizeSp: Float,
    showBengaliPronunciation: Boolean,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isPlaying) Modifier.border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                else Modifier
            )
            .testTag("ayah_card_${ayah.ayahNumber}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 2.dp else 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Ayah Top Header (Ayah Badge & Actions)
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
                        text = "${surah.number}:${ayah.ayahNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPlayAudio, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                            contentDescription = "Play Ayah Audio",
                            tint = if (isPlaying) IslamicGold else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Ayah",
                            tint = if (isBookmarked) IslamicGold else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Ayah",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text
            Text(
                text = ayah.textAr,
                fontFamily = AmiriFontFamily,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 1.8f).sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Bengali Pronunciation Card (বাংলা উচ্চারণ)
            if (showBengaliPronunciation) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "বাংলা উচ্চারণ (Recitation)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ayah.getBengaliPronunciation(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Translation Header & Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "অনুবাদ / Translation:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = ayah.getTranslation(languageCode),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuranAudioBottomBar(
    playerState: com.example.audio.AudioPlayerState,
    surah: Surah,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onToggleRepeatAyah: () -> Unit,
    onToggleRepeatSurah: () -> Unit,
    onSeek: (Int) -> Unit,
    onSetSpeed: (Float) -> Unit,
    onOpenReciters: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Seekbar
            if (playerState.durationMs > 0) {
                Slider(
                    value = playerState.currentPositionMs.toFloat(),
                    onValueChange = { onSeek(it.toInt()) },
                    valueRange = 0f..playerState.durationMs.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Surah & Verse info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Surah ${surah.nameEn} • Ayah ${playerState.currentAyahNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = ReciterCatalog.reciters.firstOrNull { it.id == playerState.reciterId }?.nameEn ?: "Mishary Alafasy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleRepeatAyah, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.RepeatOne,
                            contentDescription = "Repeat Ayah",
                            tint = if (playerState.isRepeatAyah) IslamicGold else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onPrev, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Ayah", modifier = Modifier.size(22.dp))
                    }

                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        if (playerState.isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    IconButton(onClick = onNext, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Ayah", modifier = Modifier.size(22.dp))
                    }
                }
            }

            playerState.errorMessage?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TranslationLanguageDialog(
    currentLang: String,
    onSelectLang: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        Pair("en", "English (Sahih International)"),
        Pair("ms", "Bahasa Melayu (Abdullah Muhammad Basmeih)"),
        Pair("bn", "বাংলা (মুহিউদ্দীন খান)"),
        Pair("id", "Bahasa Indonesia (Kemenag)"),
        Pair("ur", "اردو (مولانا فتح محمد جالندھری)"),
        Pair("fr", "Français (Muhammad Hamidullah)")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Quran Translation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select verified authentic translation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                languages.forEach { (code, name) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectLang(code)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        RadioButton(
                            selected = code == currentLang,
                            onClick = {
                                onSelectLang(code)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (code == currentLang) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
fun ReciterSelectionDialog(
    currentReciterId: String,
    onSelectReciter: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Select Qari (Reciter)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Renowned Qurra with high quality recitation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                ReciterCatalog.reciters.forEach { reciter ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectReciter(reciter.id)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        RadioButton(
                            selected = reciter.id == currentReciterId,
                            onClick = {
                                onSelectReciter(reciter.id)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = reciter.nameEn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (reciter.id == currentReciterId) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${reciter.nameAr} • ${reciter.style}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
