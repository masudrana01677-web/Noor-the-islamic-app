package com.example.data.calculator

import java.util.Calendar
import kotlin.math.floor

data class HijriDate(
    val day: Int,
    val monthIndex: Int, // 1 to 12
    val monthNameEn: String,
    val monthNameAr: String,
    val year: Int,
    val isRamadan: Boolean = false,
    val isWhiteDay: Boolean = false, // 13, 14, 15
    val specialOccasion: String? = null
)

object HijriCalendarHelper {

    private val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhul-Qi'dah", "Dhul-Hijjah"
    )

    private val HIJRI_MONTHS_AR = listOf(
        "محرّم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    /**
     * Converts a Gregorian calendar date to a calculated Hijri date using tabular astronomical algorithm.
     */
    fun getHijriDate(calendar: Calendar, adjustmentDays: Int = 0): HijriDate {
        val cal = calendar.clone() as Calendar
        if (adjustmentDays != 0) {
            cal.add(Calendar.DAY_OF_YEAR, adjustmentDays)
        }

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Julian Day Number
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524

        // Hijri calculation based on epoch jd = 1948439.5 (1 Muharram 1 AH)
        val l = jd - 1948440 + 10632
        val n = floor((l - 1) / 10631.0)
        val lRemainder = l - 10631 * n + 354
        val j = (floor((10985 - lRemainder) / 5316.0)) * (floor((50 * lRemainder) / 17719.0)) +
                (floor(lRemainder / 5670.0)) * (floor((43 * lRemainder) / 15238.0))
        val l2 = lRemainder - (floor((30 - j) / 15.0)) * (floor((17719 * j) / 50.0)) -
                (floor(j / 16.0)) * (floor((15238 * j) / 43.0)) + 29
        val hMonth = floor((24 * l2) / 709.0).toInt()
        val hDay = (l2 - floor((709 * hMonth) / 24.0)).toInt()
        val hYear = (30 * n + j - 30).toInt()

        val clampedMonth = hMonth.coerceIn(1, 12)
        val isRamadan = clampedMonth == 9
        val isWhiteDay = hDay in 13..15

        var specialOccasion: String? = null
        if (clampedMonth == 9) specialOccasion = "Blessed Month of Ramadan"
        else if (clampedMonth == 10 && hDay == 1) specialOccasion = "Eid al-Fitr"
        else if (clampedMonth == 12 && hDay == 9) specialOccasion = "Day of Arafah"
        else if (clampedMonth == 12 && hDay in 10..13) specialOccasion = "Eid al-Adha"
        else if (clampedMonth == 1 && hDay == 10) specialOccasion = "Day of Ashura"
        else if (isWhiteDay) specialOccasion = "Ayyam al-Beed (Sunnah Fasting)"

        return HijriDate(
            day = hDay,
            monthIndex = clampedMonth,
            monthNameEn = HIJRI_MONTHS_EN[clampedMonth - 1],
            monthNameAr = HIJRI_MONTHS_AR[clampedMonth - 1],
            year = hYear,
            isRamadan = isRamadan,
            isWhiteDay = isWhiteDay,
            specialOccasion = specialOccasion
        )
    }

    /**
     * Contextual Islamic greeting based on local hour of day.
     */
    fun getGreeting(hourOfDay: Int): Pair<String, String> {
        return when (hourOfDay) {
            in 4..11 -> Pair(
                "Subah al-Khayr",
                "May your morning be filled with peace, light, and barakah."
            )
            in 12..16 -> Pair(
                "Salam Alaykum",
                "Take a peaceful breath and remember Allah in your day."
            )
            in 17..20 -> Pair(
                "Masa' al-Khayr",
                "May Allah accept your prayers and bless your evening."
            )
            else -> Pair(
                "Layla Sa'idah",
                "End your day in quiet reflection, istighfar, and gratitude."
            )
        }
    }
}
