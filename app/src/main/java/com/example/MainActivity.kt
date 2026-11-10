package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.NoorViewModel
import com.example.ui.components.NoorBottomNavigationBar
import com.example.ui.components.NoorNavDestination
import com.example.ui.home.HomeScreen
import com.example.ui.more.MoreScreen
import com.example.ui.more.MoreSubScreen
import com.example.ui.prayer.PrayerScreen
import com.example.ui.qibla.QiblaScreen
import com.example.ui.quran.QuranScreen
import com.example.ui.quran.SurahDetailScreen
import com.example.ui.theme.NoorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NoorViewModel = viewModel()
            val settings by viewModel.userSettings.collectAsStateWithLifecycle()

            val isDark = when (settings.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            // Runtime Permissions Request
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                              permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (granted) {
                    viewModel.requestGpsLocation()
                }
            }

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }

            LaunchedEffect(Unit) {
                // Request location if auto GPS enabled
                val hasFine = ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasFine) {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    viewModel.requestGpsLocation()
                }

                // Request notification permission for Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasNotif = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasNotif) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            NoorTheme(darkTheme = isDark) {
                NoorAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NoorAppContent(viewModel: NoorViewModel) {
    var currentDestination by remember { mutableStateOf(NoorNavDestination.HOME) }
    var activeSurahReading by remember { mutableStateOf<Int?>(null) }
    var moreSubScreen by remember { mutableStateOf(MoreSubScreen.MENU) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (activeSurahReading == null) {
                NoorBottomNavigationBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { dest ->
                        currentDestination = dest
                        if (dest == NoorNavDestination.MORE) {
                            moreSubScreen = MoreSubScreen.MENU
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (activeSurahReading == null) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp.Hairline)
        ) {
            if (activeSurahReading != null) {
                SurahDetailScreen(
                    surahNumber = activeSurahReading!!,
                    viewModel = viewModel,
                    onBack = { activeSurahReading = null }
                )
            } else {
                when (currentDestination) {
                    NoorNavDestination.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToPrayer = { currentDestination = NoorNavDestination.PRAYER },
                            onNavigateToQuran = { surahNum ->
                                activeSurahReading = surahNum
                            },
                            onNavigateToQibla = { currentDestination = NoorNavDestination.QIBLA },
                            onNavigateToFasting = {
                                moreSubScreen = MoreSubScreen.FASTING
                                currentDestination = NoorNavDestination.MORE
                            },
                            onNavigateToHadith = {
                                moreSubScreen = MoreSubScreen.HADITH
                                currentDestination = NoorNavDestination.MORE
                            },
                            onNavigateToSettings = {
                                moreSubScreen = MoreSubScreen.SETTINGS
                                currentDestination = NoorNavDestination.MORE
                            }
                        )
                    }
                    NoorNavDestination.PRAYER -> {
                        PrayerScreen(viewModel = viewModel)
                    }
                    NoorNavDestination.QURAN -> {
                        QuranScreen(
                            viewModel = viewModel,
                            onSelectSurah = { surahNum ->
                                activeSurahReading = surahNum
                            }
                        )
                    }
                    NoorNavDestination.QIBLA -> {
                        QiblaScreen(viewModel = viewModel)
                    }
                    NoorNavDestination.MORE -> {
                        MoreScreen(
                            viewModel = viewModel,
                            initialSubScreen = moreSubScreen
                        )
                    }
                }
            }
        }
    }
}
