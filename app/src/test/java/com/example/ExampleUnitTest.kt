package com.example

import com.example.data.calculator.AsrJuristicMethod
import com.example.data.calculator.CalculationMethod
import com.example.data.calculator.HijriCalendarHelper
import com.example.data.calculator.PrayerTimeCalculator
import com.example.data.calculator.QiblaCalculator
import com.example.data.repository.HadithRepository
import com.example.data.repository.QuranRepository
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun testPrayerCalculation_KualaLumpur() {
        val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kuala_Lumpur")).apply {
            set(2026, Calendar.NOVEMBER, 10, 12, 0, 0)
        }
        val schedule = PrayerTimeCalculator.calculate(
            calendar = calendar,
            latitude = 3.1390,
            longitude = 101.6869,
            method = CalculationMethod.JAKIM,
            asrMethod = AsrJuristicMethod.STANDARD
        )

        assertNotNull(schedule)
        assertTrue("Fajr must be before Sunrise", schedule.fajr.before(schedule.sunrise))
        assertTrue("Sunrise must be before Dhuhr", schedule.sunrise.before(schedule.dhuhr))
        assertTrue("Dhuhr must be before Asr", schedule.dhuhr.before(schedule.asr))
        assertTrue("Asr must be before Maghrib", schedule.asr.before(schedule.maghrib))
        assertTrue("Maghrib must be before Isha", schedule.maghrib.before(schedule.isha))
        assertTrue("Imsak must be before Fajr", schedule.imsak.before(schedule.fajr))
    }

    @Test
    fun testQiblaBearing_FromKualaLumpur() {
        // Qibla from KL is roughly 292° WNW
        val bearing = QiblaCalculator.calculateBearing(3.1390, 101.6869)
        assertTrue("Qibla bearing should be around 292 degrees", bearing in 290.0..295.0)

        val distance = QiblaCalculator.calculateDistanceKm(3.1390, 101.6869)
        assertTrue("Distance to Kaaba should be around ~7000 km", distance in 6500.0..7500.0)
    }

    @Test
    fun testHijriConversion() {
        val calendar = Calendar.getInstance()
        val hijri = HijriCalendarHelper.getHijriDate(calendar)

        assertTrue(hijri.day in 1..30)
        assertTrue(hijri.monthIndex in 1..12)
        assertTrue(hijri.year >= 1445)
        assertNotNull(hijri.monthNameEn)
        assertNotNull(hijri.monthNameAr)
    }

    @Test
    fun testQuranSurahCatalog() {
        val surahs = QuranRepository.allSurahs
        assertEquals("There must be 114 Surahs in Quran", 114, surahs.size)
        assertEquals("Surah 1 must be Al-Fatihah", "Al-Fatihah", surahs[0].nameEn)
        assertEquals("Surah 114 must be An-Nas", "An-Nas", surahs[113].nameEn)
    }

    @Test
    fun testHadithDailyRotation() {
        val hadith = HadithRepository.getDailyHadith()
        assertNotNull(hadith)
        assertTrue(hadith.textAr.isNotEmpty())
        assertTrue(hadith.textEn.isNotEmpty())
        assertTrue(hadith.book.isNotEmpty())
        assertEquals("Sahih (Authentic)", hadith.grading)
    }

    @Test
    fun testBengaliPronunciation() {
        val fatihah1 = com.example.data.repository.BengaliQuranPronunciationHelper.getPronunciation(
            1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        )
        assertEquals("বিসমিল্লাহির রাহমানির রাহিম", fatihah1)

        val ikhlas1 = com.example.data.repository.BengaliQuranPronunciationHelper.getPronunciation(
            112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ"
        )
        assertEquals("কুল হুওয়াল্লাহু আহাদ", ikhlas1)
    }
}
