package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.calculator.AsrJuristicMethod
import com.example.data.calculator.CalculationMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "noor_settings")

data class UserSettings(
    val cityName: String = "Kuala Lumpur",
    val countryName: String = "Malaysia",
    val latitude: Double = 3.1390,
    val longitude: Double = 101.6869,
    val isAutoGps: Boolean = true,
    val calculationMethod: CalculationMethod = CalculationMethod.JAKIM,
    val asrMethod: AsrJuristicMethod = AsrJuristicMethod.STANDARD,
    val fajrAdjust: Int = 0,
    val dhuhrAdjust: Int = 0,
    val asrAdjust: Int = 0,
    val maghribAdjust: Int = 0,
    val ishaAdjust: Int = 0,
    val notificationsEnabled: Boolean = true,
    val fajrNotification: Boolean = true,
    val dhuhrNotification: Boolean = true,
    val asrNotification: Boolean = true,
    val maghribNotification: Boolean = true,
    val ishaNotification: Boolean = true,
    val notifyMinutesBefore: Int = 0,
    val adhanSoundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailyHadithNotification: Boolean = true,
    val dailyHadithHour: Int = 9,
    val dailyHadithMinute: Int = 0,
    val quranLanguage: String = "en",
    val arabicFontSizeSp: Float = 28f,
    val activeReciterId: String = "alafasy",
    val showBengaliPronunciation: Boolean = true,
    val themeMode: String = "system" // "system", "light", "dark"
)

class UserPreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val CITY_NAME = stringPreferencesKey("city_name")
        val COUNTRY_NAME = stringPreferencesKey("country_name")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val IS_AUTO_GPS = booleanPreferencesKey("is_auto_gps")
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val ASR_METHOD = stringPreferencesKey("asr_method")
        val FAJR_ADJUST = intPreferencesKey("fajr_adjust")
        val DHUHR_ADJUST = intPreferencesKey("dhuhr_adjust")
        val ASR_ADJUST = intPreferencesKey("asr_adjust")
        val MAGHRIB_ADJUST = intPreferencesKey("maghrib_adjust")
        val ISHA_ADJUST = intPreferencesKey("isha_adjust")
        val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
        val FAJR_NOTIF = booleanPreferencesKey("fajr_notif")
        val DHUHR_NOTIF = booleanPreferencesKey("dhuhr_notif")
        val ASR_NOTIF = booleanPreferencesKey("asr_notif")
        val MAGHRIB_NOTIF = booleanPreferencesKey("maghrib_notif")
        val ISHA_NOTIF = booleanPreferencesKey("isha_notif")
        val NOTIF_MIN_BEFORE = intPreferencesKey("notif_min_before")
        val ADHAN_SOUND = booleanPreferencesKey("adhan_sound")
        val VIBRATION = booleanPreferencesKey("vibration")
        val HADITH_NOTIF = booleanPreferencesKey("hadith_notif")
        val HADITH_HOUR = intPreferencesKey("hadith_hour")
        val HADITH_MIN = intPreferencesKey("hadith_min")
        val QURAN_LANG = stringPreferencesKey("quran_lang")
        val ARABIC_FONT_SIZE = floatPreferencesKey("arabic_font_size")
        val RECITER_ID = stringPreferencesKey("reciter_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SHOW_BENGALI_PRONUNCIATION = booleanPreferencesKey("show_bengali_pronunciation")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { pref ->
        UserSettings(
            cityName = pref[PreferencesKeys.CITY_NAME] ?: "Kuala Lumpur",
            countryName = pref[PreferencesKeys.COUNTRY_NAME] ?: "Malaysia",
            latitude = pref[PreferencesKeys.LATITUDE] ?: 3.1390,
            longitude = pref[PreferencesKeys.LONGITUDE] ?: 101.6869,
            isAutoGps = pref[PreferencesKeys.IS_AUTO_GPS] ?: true,
            calculationMethod = CalculationMethod.fromName(pref[PreferencesKeys.CALC_METHOD] ?: "JAKIM"),
            asrMethod = AsrJuristicMethod.fromName(pref[PreferencesKeys.ASR_METHOD] ?: "STANDARD"),
            fajrAdjust = pref[PreferencesKeys.FAJR_ADJUST] ?: 0,
            dhuhrAdjust = pref[PreferencesKeys.DHUHR_ADJUST] ?: 0,
            asrAdjust = pref[PreferencesKeys.ASR_ADJUST] ?: 0,
            maghribAdjust = pref[PreferencesKeys.MAGHRIB_ADJUST] ?: 0,
            ishaAdjust = pref[PreferencesKeys.ISHA_ADJUST] ?: 0,
            notificationsEnabled = pref[PreferencesKeys.NOTIF_ENABLED] ?: true,
            fajrNotification = pref[PreferencesKeys.FAJR_NOTIF] ?: true,
            dhuhrNotification = pref[PreferencesKeys.DHUHR_NOTIF] ?: true,
            asrNotification = pref[PreferencesKeys.ASR_NOTIF] ?: true,
            maghribNotification = pref[PreferencesKeys.MAGHRIB_NOTIF] ?: true,
            ishaNotification = pref[PreferencesKeys.ISHA_NOTIF] ?: true,
            notifyMinutesBefore = pref[PreferencesKeys.NOTIF_MIN_BEFORE] ?: 0,
            adhanSoundEnabled = pref[PreferencesKeys.ADHAN_SOUND] ?: true,
            vibrationEnabled = pref[PreferencesKeys.VIBRATION] ?: true,
            dailyHadithNotification = pref[PreferencesKeys.HADITH_NOTIF] ?: true,
            dailyHadithHour = pref[PreferencesKeys.HADITH_HOUR] ?: 9,
            dailyHadithMinute = pref[PreferencesKeys.HADITH_MIN] ?: 0,
            quranLanguage = pref[PreferencesKeys.QURAN_LANG] ?: "en",
            arabicFontSizeSp = pref[PreferencesKeys.ARABIC_FONT_SIZE] ?: 28f,
            activeReciterId = pref[PreferencesKeys.RECITER_ID] ?: "alafasy",
            showBengaliPronunciation = pref[PreferencesKeys.SHOW_BENGALI_PRONUNCIATION] ?: true,
            themeMode = pref[PreferencesKeys.THEME_MODE] ?: "system"
        )
    }

    suspend fun updateLocation(cityName: String, countryName: String, lat: Double, lon: Double, autoGps: Boolean) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.CITY_NAME] = cityName
            pref[PreferencesKeys.COUNTRY_NAME] = countryName
            pref[PreferencesKeys.LATITUDE] = lat
            pref[PreferencesKeys.LONGITUDE] = lon
            pref[PreferencesKeys.IS_AUTO_GPS] = autoGps
        }
    }

    suspend fun updateCalculationMethod(method: CalculationMethod) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.CALC_METHOD] = method.name
        }
    }

    suspend fun updateAsrMethod(method: AsrJuristicMethod) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.ASR_METHOD] = method.name
        }
    }

    suspend fun updateAdjustments(fajr: Int, dhuhr: Int, asr: Int, maghrib: Int, isha: Int) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.FAJR_ADJUST] = fajr
            pref[PreferencesKeys.DHUHR_ADJUST] = dhuhr
            pref[PreferencesKeys.ASR_ADJUST] = asr
            pref[PreferencesKeys.MAGHRIB_ADJUST] = maghrib
            pref[PreferencesKeys.ISHA_ADJUST] = isha
        }
    }

    suspend fun togglePrayerNotification(prayerKey: String, enabled: Boolean) {
        context.dataStore.edit { pref ->
            when (prayerKey.lowercase()) {
                "all" -> pref[PreferencesKeys.NOTIF_ENABLED] = enabled
                "fajr" -> pref[PreferencesKeys.FAJR_NOTIF] = enabled
                "dhuhr" -> pref[PreferencesKeys.DHUHR_NOTIF] = enabled
                "asr" -> pref[PreferencesKeys.ASR_NOTIF] = enabled
                "maghrib" -> pref[PreferencesKeys.MAGHRIB_NOTIF] = enabled
                "isha" -> pref[PreferencesKeys.ISHA_NOTIF] = enabled
            }
        }
    }

    suspend fun updateQuranSettings(lang: String, fontSize: Float, reciterId: String) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.QURAN_LANG] = lang
            pref[PreferencesKeys.ARABIC_FONT_SIZE] = fontSize
            pref[PreferencesKeys.RECITER_ID] = reciterId
        }
    }

    suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun toggleBengaliPronunciation(show: Boolean) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.SHOW_BENGALI_PRONUNCIATION] = show
        }
    }

    suspend fun updateHadithNotification(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.HADITH_NOTIF] = enabled
            pref[PreferencesKeys.HADITH_HOUR] = hour
            pref[PreferencesKeys.HADITH_MIN] = minute
        }
    }
}
