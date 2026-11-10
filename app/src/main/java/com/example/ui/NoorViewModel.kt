package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.QuranAudioPlayer
import com.example.data.calculator.*
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.HadithRepository
import com.example.data.repository.QuranRepository
import com.example.notification.NotificationHelper
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class CityPreset(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val recommendedMethod: CalculationMethod = CalculationMethod.JAKIM
)

data class NextPrayerInfo(
    val nameEn: String,
    val nameAr: String,
    val timeFormatted: String,
    val targetMillis: Long,
    val secondsRemaining: Long,
    val countdownFormatted: String,
    val progress: Float // 0f to 1f
)

data class FastingInfo(
    val todaySuhoorTime: String,
    val todayIftarTime: String,
    val isCurrentlyFasting: Boolean, // Between Fajr and Maghrib
    val countdownLabel: String, // "Until Iftar" or "Until Suhoor"
    val countdownFormatted: String,
    val durationFormatted: String,
    val tomorrowSuhoorTime: String,
    val tomorrowIftarTime: String,
    val hijriDate: HijriDate
)

data class QiblaUiState(
    val qiblaBearing: Double = 0.0,
    val userHeading: Float = 0.0f,
    val distanceKm: Double = 0.0,
    val isAligned: Boolean = false,
    val hasCompassSensor: Boolean = true
)

class NoorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val context = application.applicationContext
    val preferencesManager = UserPreferencesManager(context)
    val database = AppDatabase.getDatabase(context)
    val dao = database.noorDao()
    val audioPlayer = QuranAudioPlayer(context)

    // Fused Location Provider
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Sensor Manager for Qibla
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    // Text To Speech for Hadith
    private var tts: TextToSpeech? = null
    val isTtsReady = MutableStateFlow(false)

    // Settings State
    val userSettings = preferencesManager.userSettingsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserSettings()
    )

    // Prayer Schedule State
    private val _currentSchedule = MutableStateFlow<PrayerSchedule?>(null)
    val currentSchedule: StateFlow<PrayerSchedule?> = _currentSchedule.asStateFlow()

    // Next Prayer State
    private val _nextPrayer = MutableStateFlow<NextPrayerInfo?>(null)
    val nextPrayer: StateFlow<NextPrayerInfo?> = _nextPrayer.asStateFlow()

    // Fasting Info State
    private val _fastingInfo = MutableStateFlow<FastingInfo?>(null)
    val fastingInfo: StateFlow<FastingInfo?> = _fastingInfo.asStateFlow()

    // Qibla State
    private val _qiblaState = MutableStateFlow(QiblaUiState())
    val qiblaState: StateFlow<QiblaUiState> = _qiblaState.asStateFlow()

    // Quran State
    val surahList = QuranRepository.allSurahs
    val quranSearchQuery = MutableStateFlow("")
    val activeSurahNumber = MutableStateFlow(1)
    val activeSurahVerses = MutableStateFlow<List<Ayah>>(emptyList())
    val isSurahLoading = MutableStateFlow(false)
    val cachedSurahNumbers: StateFlow<Set<Int>> = dao.getCachedSurahNumbersFlow()
        .map { it.toSet() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptySet()
        )

    // Bookmarks & Progress
    val bookmarks = dao.getAllBookmarks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val readingProgress = dao.getReadingProgress().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // Tasbih Counters
    val tasbihList = dao.getAllTasbih().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val activeTasbih = MutableStateFlow<TasbihEntity?>(null)

    // Daily Hadith
    val dailyHadith = MutableStateFlow(HadithRepository.getDailyHadith())

    // Preset Cities
    val presetCities = listOf(
        CityPreset("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, CalculationMethod.JAKIM),
        CityPreset("Singapore", "Singapore", 1.3521, 103.8198, CalculationMethod.MUIS),
        CityPreset("Jakarta", "Indonesia", -6.2088, 106.8456, CalculationMethod.MUIS),
        CityPreset("Dhaka", "Bangladesh", 23.8103, 90.4125, CalculationMethod.KARACHI),
        CityPreset("Makkah", "Saudi Arabia", 21.4225, 39.8262, CalculationMethod.MAKKAH),
        CityPreset("Medina", "Saudi Arabia", 24.5247, 39.5692, CalculationMethod.MAKKAH),
        CityPreset("Cairo", "Egypt", 30.0444, 31.2357, CalculationMethod.EGYPT),
        CityPreset("Istanbul", "Turkey", 41.0082, 28.9784, CalculationMethod.DIYANET),
        CityPreset("London", "United Kingdom", 51.5074, -0.1278, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("New York", "United States", 40.7128, -74.0060, CalculationMethod.ISNA),
        CityPreset("Toronto", "Canada", 43.6532, -79.3832, CalculationMethod.ISNA),
        CityPreset("Karachi", "Pakistan", 24.8607, 67.0011, CalculationMethod.KARACHI),
        CityPreset("Dubai", "UAE", 25.2048, 55.2708, CalculationMethod.MUSLIM_WORLD_LEAGUE)
    )

    init {
        NotificationHelper.createNotificationChannels(context)

        // Initialize TTS
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                isTtsReady.value = true
            }
        }

        // Initialize default Tasbih if empty
        viewModelScope.launch {
            val defaultTasbih = listOf(
                TasbihEntity("subhanallah", "سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", 0, 33),
                TasbihEntity("alhamdulillah", "الْحَمْدُ لِلَّهِ", "Alhamdulillah", "All praise is for Allah", 0, 33),
                TasbihEntity("allahuakbar", "اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 0, 34),
                TasbihEntity("astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "I seek forgiveness from Allah", 0, 100),
                TasbihEntity("lailahaillallah", "لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "There is no deity except Allah", 0, 100),
                TasbihEntity("salawat", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ", "Allahumma Salli 'Ala Muhammad", "O Allah, send blessings upon Muhammad", 0, 100)
            )
            dao.insertDefaultTasbih(defaultTasbih)
        }

        // Observe settings changes and recalculate prayer times
        viewModelScope.launch {
            userSettings.collectLatest { settings ->
                recalculateSchedule(settings)
            }
        }

        // Live countdown timer (runs every second)
        viewModelScope.launch {
            while (true) {
                updateLiveCountdowns()
                delay(1000)
            }
        }

        // Pre-seed bundled Surahs into Room database for instant offline access
        viewModelScope.launch {
            QuranRepository.seedBundledSurahs(dao)
        }

        // Load initial Surah with Room persistence
        loadSurah(1)

        // Register sensors for Qibla
        registerCompass()
    }

    private fun registerCompass() {
        rotationSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?: run {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }
        _qiblaState.value = _qiblaState.value.copy(
            hasCompassSensor = rotationSensor != null || (accelerometer != null && magnetometer != null)
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        var headingDeg = 0f

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            headingDeg = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, 3)
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
            hasGeomagnetic = true
        }

        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR && hasGravity && hasGeomagnetic) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                headingDeg = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
            }
        }

        val bearing = _qiblaState.value.qiblaBearing
        val aligned = QiblaCalculator.isAligned(headingDeg, bearing, tolerance = 5.0)

        // Haptic feedback when newly aligned
        if (aligned && !_qiblaState.value.isAligned) {
            vibrateDevice(50)
        }

        _qiblaState.value = _qiblaState.value.copy(
            userHeading = headingDeg,
            isAligned = aligned
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun recalculateSchedule(settings: UserSettings) {
        val now = Calendar.getInstance()
        val schedule = PrayerTimeCalculator.calculate(
            calendar = now,
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
        _currentSchedule.value = schedule

        // Qibla calculation
        val qBearing = QiblaCalculator.calculateBearing(settings.latitude, settings.longitude)
        val qDistance = QiblaCalculator.calculateDistanceKm(settings.latitude, settings.longitude)
        _qiblaState.value = _qiblaState.value.copy(
            qiblaBearing = qBearing,
            distanceKm = qDistance
        )

        // Schedule notification alarms
        NotificationHelper.schedulePrayerAlarms(context, schedule, settings)

        updateLiveCountdowns()
    }

    private fun updateLiveCountdowns() {
        val schedule = _currentSchedule.value ?: return
        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val prayers = listOf(
            Triple("Fajr", "الفجر", schedule.fajr),
            Triple("Sunrise", "الشروق", schedule.sunrise),
            Triple("Dhuhr", "الظهر", schedule.dhuhr),
            Triple("Asr", "العصر", schedule.asr),
            Triple("Maghrib", "المغرب", schedule.maghrib),
            Triple("Isha", "العشاء", schedule.isha)
        )

        // Find next prayer
        var nextItem: Triple<String, String, Calendar>? = null
        var prevMillis = schedule.fajr.timeInMillis

        for (i in prayers.indices) {
            val p = prayers[i]
            if (p.third.timeInMillis > now) {
                nextItem = p
                prevMillis = if (i > 0) prayers[i - 1].third.timeInMillis else (schedule.fajr.timeInMillis - 4 * 3600 * 1000)
                break
            }
        }

        // If all prayers passed today, next is tomorrow's Fajr
        val (nextNameEn, nextNameAr, nextCal, targetMillis) = if (nextItem != null) {
            arrayOf(nextItem.first, nextItem.second, nextItem.third, nextItem.third.timeInMillis)
        } else {
            val tomFajr = (schedule.fajr.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            arrayOf("Fajr", "الفجر", tomFajr, tomFajr.timeInMillis)
        }

        val target = targetMillis as Long
        val diffSec = maxOf(0L, (target - now) / 1000)
        val hours = diffSec / 3600
        val mins = (diffSec % 3600) / 60
        val secs = diffSec % 60
        val countdownStr = if (hours > 0) {
            String.format("%02dh %02dm %02ds", hours, mins, secs)
        } else {
            String.format("%02dm %02ds", mins, secs)
        }

        val totalInterval = maxOf(1L, target - prevMillis)
        val elapsed = maxOf(0L, now - prevMillis)
        val progress = (elapsed.toFloat() / totalInterval.toFloat()).coerceIn(0f, 1f)

        _nextPrayer.value = NextPrayerInfo(
            nameEn = nextNameEn as String,
            nameAr = nextNameAr as String,
            timeFormatted = timeFormat.format((nextCal as Calendar).time),
            targetMillis = target,
            secondsRemaining = diffSec,
            countdownFormatted = countdownStr,
            progress = progress
        )

        // Update Fasting Info
        val fajrTime = schedule.fajr.timeInMillis
        val maghribTime = schedule.maghrib.timeInMillis
        val isCurrentlyFasting = now in fajrTime..maghribTime
        val fastingTarget = if (isCurrentlyFasting) maghribTime else if (now < fajrTime) fajrTime else {
            val tomFajr = (schedule.fajr.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            tomFajr.timeInMillis
        }
        val fastingDiff = maxOf(0L, (fastingTarget - now) / 1000)
        val fHours = fastingDiff / 3600
        val fMins = (fastingDiff % 3600) / 60
        val fSecs = fastingDiff % 60
        val fCountdownStr = String.format("%02dh %02dm %02ds", fHours, fMins, fSecs)

        val fastingDurationTotalSec = (maghribTime - fajrTime) / 1000
        val fdHours = fastingDurationTotalSec / 3600
        val fdMins = (fastingDurationTotalSec % 3600) / 60

        val tomorrowCal = (Calendar.getInstance()).apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowSchedule = PrayerTimeCalculator.calculate(
            tomorrowCal,
            userSettings.value.latitude,
            userSettings.value.longitude,
            userSettings.value.calculationMethod,
            userSettings.value.asrMethod
        )

        _fastingInfo.value = FastingInfo(
            todaySuhoorTime = timeFormat.format(schedule.imsak.time),
            todayIftarTime = timeFormat.format(schedule.maghrib.time),
            isCurrentlyFasting = isCurrentlyFasting,
            countdownLabel = if (isCurrentlyFasting) "Until Iftar (Maghrib)" else "Until Suhoor Ends (Fajr)",
            countdownFormatted = fCountdownStr,
            durationFormatted = "${fdHours}h ${fdMins}m",
            tomorrowSuhoorTime = timeFormat.format(tomorrowSchedule.imsak.time),
            tomorrowIftarTime = timeFormat.format(tomorrowSchedule.maghrib.time),
            hijriDate = HijriCalendarHelper.getHijriDate(Calendar.getInstance())
        )
    }

    fun requestGpsLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        var cityName = "Detected City"
                        var countryName = "Detected Country"
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { addresses ->
                                    val addr = addresses.firstOrNull()
                                    if (addr != null) {
                                        cityName = addr.locality ?: addr.subAdminArea ?: "Current Location"
                                        countryName = addr.countryName ?: ""
                                        viewModelScope.launch {
                                            preferencesManager.updateLocation(cityName, countryName, loc.latitude, loc.longitude, true)
                                        }
                                    }
                                }
                            } else {
                                @Suppress("DEPRECATION")
                                val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                                val addr = addresses?.firstOrNull()
                                if (addr != null) {
                                    cityName = addr.locality ?: addr.subAdminArea ?: "Current Location"
                                    countryName = addr.countryName ?: ""
                                }
                                preferencesManager.updateLocation(cityName, countryName, loc.latitude, loc.longitude, true)
                            }
                        } catch (e: Exception) {
                            preferencesManager.updateLocation("GPS Location", "", loc.latitude, loc.longitude, true)
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            // Location permission not granted
        }
    }

    fun selectPresetCity(city: CityPreset) {
        viewModelScope.launch {
            preferencesManager.updateLocation(city.name, city.country, city.lat, city.lon, false)
            preferencesManager.updateCalculationMethod(city.recommendedMethod)
        }
    }

    fun setCalculationMethod(method: CalculationMethod) {
        viewModelScope.launch {
            preferencesManager.updateCalculationMethod(method)
        }
    }

    fun setAsrMethod(method: AsrJuristicMethod) {
        viewModelScope.launch {
            preferencesManager.updateAsrMethod(method)
        }
    }

    fun setAdjustments(fajr: Int, dhuhr: Int, asr: Int, maghrib: Int, isha: Int) {
        viewModelScope.launch {
            preferencesManager.updateAdjustments(fajr, dhuhr, asr, maghrib, isha)
        }
    }

    fun togglePrayerNotification(prayerKey: String, enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.togglePrayerNotification(prayerKey, enabled)
        }
    }

    // Quran functions
    fun loadSurah(surahNumber: Int) {
        activeSurahNumber.value = surahNumber
        isSurahLoading.value = true
        viewModelScope.launch {
            val verses = QuranRepository.getSurahVerses(surahNumber, dao)
            activeSurahVerses.value = verses
            isSurahLoading.value = false

            // Save reading progress
            val surah = surahList.firstOrNull { it.number == surahNumber }
            if (surah != null) {
                dao.saveReadingProgress(
                    ReadingProgressEntity(
                        surahNumber = surahNumber,
                        ayahNumber = 1,
                        surahNameEn = surah.nameEn,
                        surahNameAr = surah.nameAr
                    )
                )
            }
        }
    }

    fun cacheSurahOffline(surahNumber: Int) {
        viewModelScope.launch {
            QuranRepository.getSurahVerses(surahNumber, dao)
        }
    }

    fun toggleBookmark(ayah: Ayah, surah: Surah) {
        viewModelScope.launch {
            val existing = dao.findBookmark(ayah.surahNumber, ayah.ayahNumber)
            if (existing != null) {
                dao.deleteBookmark(existing.id)
            } else {
                dao.insertBookmark(
                    BookmarkEntity(
                        surahNumber = ayah.surahNumber,
                        ayahNumber = ayah.ayahNumber,
                        surahNameEn = surah.nameEn,
                        surahNameAr = surah.nameAr,
                        ayahTextAr = ayah.textAr,
                        ayahTranslation = ayah.getTranslation(userSettings.value.quranLanguage)
                    )
                )
            }
        }
    }

    fun setQuranLanguage(lang: String) {
        viewModelScope.launch {
            preferencesManager.updateQuranSettings(
                lang,
                userSettings.value.arabicFontSizeSp,
                userSettings.value.activeReciterId
            )
        }
    }

    fun setArabicFontSize(sizeSp: Float) {
        viewModelScope.launch {
            preferencesManager.updateQuranSettings(
                userSettings.value.quranLanguage,
                sizeSp,
                userSettings.value.activeReciterId
            )
        }
    }

    fun setReciter(reciterId: String) {
        viewModelScope.launch {
            preferencesManager.updateQuranSettings(
                userSettings.value.quranLanguage,
                userSettings.value.arabicFontSizeSp,
                reciterId
            )
        }
    }

    fun toggleBengaliPronunciation(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.toggleBengaliPronunciation(enabled)
        }
    }

    // Audio controls
    fun playAyahAudio(surahNumber: Int, ayahNumber: Int, totalAyahs: Int) {
        audioPlayer.playAyah(surahNumber, ayahNumber, totalAyahs, userSettings.value.activeReciterId)
    }

    // Tasbih functions
    fun tapTasbih(dhikrId: String) {
        viewModelScope.launch {
            val currentList = tasbihList.value
            val entity = currentList.firstOrNull { it.dhikrId == dhikrId } ?: return@launch
            var newCount = entity.currentCount + 1
            var newLaps = entity.totalLaps
            var newLifetime = entity.lifetimeCount + 1

            if (newCount >= entity.targetCount) {
                newCount = 0
                newLaps += 1
                vibrateDevice(100) // Distinct lap vibration
            } else {
                vibrateDevice(25) // Light tap feedback
            }

            val updated = entity.copy(
                currentCount = newCount,
                totalLaps = newLaps,
                lifetimeCount = newLifetime
            )
            dao.saveTasbih(updated)
            activeTasbih.value = updated
        }
    }

    fun resetTasbih(dhikrId: String) {
        viewModelScope.launch {
            val currentList = tasbihList.value
            val entity = currentList.firstOrNull { it.dhikrId == dhikrId } ?: return@launch
            val updated = entity.copy(currentCount = 0, totalLaps = 0)
            dao.saveTasbih(updated)
            activeTasbih.value = updated
        }
    }

    fun updateTasbihTarget(dhikrId: String, newTarget: Int) {
        viewModelScope.launch {
            val currentList = tasbihList.value
            val entity = currentList.firstOrNull { it.dhikrId == dhikrId } ?: return@launch
            val updated = entity.copy(targetCount = newTarget, currentCount = 0)
            dao.saveTasbih(updated)
            activeTasbih.value = updated
        }
    }

    // Hadith audio TTS
    fun playHadithTts(text: String) {
        if (isTtsReady.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hadith_tts")
        }
    }

    fun stopHadithTts() {
        tts?.stop()
    }

    private fun vibrateDevice(durationMs: Long) {
        if (!userSettings.value.vibrationEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (e: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(this)
        audioPlayer.stop()
        tts?.stop()
        tts?.shutdown()
    }
}
