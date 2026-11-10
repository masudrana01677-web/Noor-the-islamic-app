package com.example.data.calculator

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

enum class CalculationMethod(
    val title: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaMinutesAfterMaghrib: Int = 0
) {
    MUSLIM_WORLD_LEAGUE("Muslim World League", 18.0, 17.0),
    ISNA("ISNA (North America)", 15.0, 15.0),
    EGYPT("Egyptian General Authority", 19.5, 17.5),
    MAKKAH("Umm Al-Qura (Makkah)", 18.5, 0.0, ishaMinutesAfterMaghrib = 90),
    KARACHI("University of Islamic Sciences, Karachi", 18.0, 18.0),
    JAKIM("JAKIM (Malaysia)", 20.0, 18.0),
    MUIS("MUIS (Singapore)", 20.0, 18.0),
    DIYANET("Diyanet (Turkey)", 18.0, 17.0);

    companion object {
        fun fromName(name: String): CalculationMethod {
            return entries.firstOrNull { it.name == name } ?: JAKIM
        }
    }
}

enum class AsrJuristicMethod(val title: String, val shadowFactor: Double) {
    STANDARD("Standard (Shafi'i, Maliki, Hanbali)", 1.0),
    HANAFI("Hanafi", 2.0);

    companion object {
        fun fromName(name: String): AsrJuristicMethod {
            return entries.firstOrNull { it.name == name } ?: STANDARD
        }
    }
}

data class PrayerSchedule(
    val date: Calendar,
    val imsak: Calendar,
    val fajr: Calendar,
    val sunrise: Calendar,
    val dhuhr: Calendar,
    val asr: Calendar,
    val maghrib: Calendar,
    val isha: Calendar,
    val midnight: Calendar
)

data class PrayerTimeItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val time: Calendar,
    val isPassed: Boolean = false,
    val isNext: Boolean = false
)

object PrayerTimeCalculator {

    /**
     * Calculates prayer times for a specific date, latitude, longitude, and elevation.
     */
    fun calculate(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        method: CalculationMethod = CalculationMethod.JAKIM,
        asrMethod: AsrJuristicMethod = AsrJuristicMethod.STANDARD,
        fajrAdjustmentMin: Int = 0,
        dhuhrAdjustmentMin: Int = 0,
        asrAdjustmentMin: Int = 0,
        maghribAdjustmentMin: Int = 0,
        ishaAdjustmentMin: Int = 0
    ): PrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val timeZone = calendar.timeZone

        // Julian Date
        val jd = julianDate(year, month, day) - (longitude / (15.0 * 24.0))

        // Solar parameters
        val (declination, eqTime) = sunPosition(jd)

        // Midday (solar noon) in UTC hours
        val noonUtcHours = 12.0 - eqTime - (longitude / 15.0)

        // Sun angle helper
        fun hourAngle(angle: Double): Double {
            val sinAngle = sin(Math.toRadians(-angle))
            val sinLat = sin(Math.toRadians(latitude))
            val sinDec = sin(Math.toRadians(declination))
            val cosLat = cos(Math.toRadians(latitude))
            val cosDec = cos(Math.toRadians(declination))

            val cosHa = (sinAngle - sinLat * sinDec) / (cosLat * cosDec)
            val clamped = cosHa.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) / 15.0
        }

        // Asr hour angle based on shadow length
        fun asrHourAngle(factor: Double): Double {
            val d = Math.toRadians(abs(latitude - declination))
            val alt = Math.toDegrees(atan(1.0 / (factor + tan(d))))
            val sinAlt = sin(Math.toRadians(alt))
            val sinLat = sin(Math.toRadians(latitude))
            val sinDec = sin(Math.toRadians(declination))
            val cosLat = cos(Math.toRadians(latitude))
            val cosDec = cos(Math.toRadians(declination))

            val cosHa = (sinAlt - sinLat * sinDec) / (cosLat * cosDec)
            val clamped = cosHa.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) / 15.0
        }

        // Sunrise and sunset (standard 0.833 degrees for refraction & diameter)
        val sunriseHa = hourAngle(0.833)

        val fajrHa = hourAngle(method.fajrAngle)
        val ishaHa = if (method.ishaAngle > 0) hourAngle(method.ishaAngle) else 0.0

        val asrHa = asrHourAngle(asrMethod.shadowFactor)

        // UTC times in hours
        val fajrUtc = noonUtcHours - fajrHa
        val sunriseUtc = noonUtcHours - sunriseHa
        val dhuhrUtc = noonUtcHours + (2.0 / 60.0) // 2 min buffer after zawal
        val asrUtc = noonUtcHours + asrHa
        val maghribUtc = noonUtcHours + sunriseHa + (2.0 / 60.0) // 2 min buffer
        val ishaUtc = if (method.ishaMinutesAfterMaghrib > 0) {
            maghribUtc + (method.ishaMinutesAfterMaghrib / 60.0)
        } else {
            noonUtcHours + ishaHa
        }

        // Convert UTC hours to local Calendar objects
        fun toCalendar(utcHours: Double, adjustmentMinutes: Int): Calendar {
            val cal = Calendar.getInstance(timeZone).apply {
                timeInMillis = calendar.timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Offset to local
            val tzOffsetHours = timeZone.getOffset(cal.timeInMillis) / 3600000.0
            var localHours = utcHours + tzOffsetHours
            var dayOffset = 0
            while (localHours < 0) {
                localHours += 24.0
                dayOffset -= 1
            }
            while (localHours >= 24.0) {
                localHours -= 24.0
                dayOffset += 1
            }

            if (dayOffset != 0) {
                cal.add(Calendar.DAY_OF_YEAR, dayOffset)
            }

            val hour = localHours.toInt()
            val totalSeconds = ((localHours - hour) * 3600).roundToInt()
            val minute = totalSeconds / 60
            val second = totalSeconds % 60

            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, second)
            cal.add(Calendar.MINUTE, adjustmentMinutes)
            return cal
        }

        val fajrCal = toCalendar(fajrUtc, fajrAdjustmentMin)
        val sunriseCal = toCalendar(sunriseUtc, 0)
        val dhuhrCal = toCalendar(dhuhrUtc, dhuhrAdjustmentMin)
        val asrCal = toCalendar(asrUtc, asrAdjustmentMin)
        val maghribCal = toCalendar(maghribUtc, maghribAdjustmentMin)
        val ishaCal = toCalendar(ishaUtc, ishaAdjustmentMin)

        // Imsak: 10 minutes before Fajr
        val imsakCal = (fajrCal.clone() as Calendar).apply {
            add(Calendar.MINUTE, -10)
        }

        // Islamic midnight: halfway between Maghrib and next Fajr
        val nextFajrCal = (fajrCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val diffMillis = nextFajrCal.timeInMillis - maghribCal.timeInMillis
        val midnightCal = (maghribCal.clone() as Calendar).apply {
            add(Calendar.MILLISECOND, (diffMillis / 2).toInt())
        }

        return PrayerSchedule(
            date = calendar,
            imsak = imsakCal,
            fajr = fajrCal,
            sunrise = sunriseCal,
            dhuhr = dhuhrCal,
            asr = asrCal,
            maghrib = maghribCal,
            isha = ishaCal,
            midnight = midnightCal
        )
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0

        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val eqTime = (q / 15.0) - ra
        return Pair(declination, eqTime)
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0) a += 360.0
        return a
    }
}
