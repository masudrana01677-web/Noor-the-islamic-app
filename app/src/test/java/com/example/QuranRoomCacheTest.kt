package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CachedAyahEntity
import com.example.data.local.NoorDao
import com.example.data.repository.QuranRepository
import com.example.data.repository.toAyah
import com.example.data.repository.toCachedEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class QuranRoomCacheTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: NoorDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.noorDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveCachedSurah() = runBlocking {
        val sampleAyahs = listOf(
            CachedAyahEntity(
                surahNumber = 1,
                ayahNumber = 1,
                textAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                translationEn = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                translationBn = "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে শুরু করছি।",
                pronunciationBn = "বিসমিল্লাহির রাহমানির রাহিম"
            ),
            CachedAyahEntity(
                surahNumber = 1,
                ayahNumber = 2,
                textAr = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                translationEn = "[All] praise is [due] to Allah, Lord of the worlds -",
                translationBn = "সমস্ত প্রশংসা আল্লাহ তাআলার যিনি সমগ্র সৃষ্টির পালনকর্তা।",
                pronunciationBn = "আলহামদু লিল্লাহি রাব্বিল আলামিন"
            )
        )

        dao.insertCachedAyahs(sampleAyahs)

        val retrieved = dao.getCachedAyahs(1)
        assertEquals(2, retrieved.size)
        assertEquals("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", retrieved[0].textAr)
        assertEquals("বিসমিল্লাহির রাহমানির রাহিম", retrieved[0].pronunciationBn)

        val cachedSurahs = dao.getCachedSurahNumbers()
        assertTrue(cachedSurahs.contains(1))
    }

    @Test
    fun testRepositoryServesFromRoomCache() = runBlocking {
        // Pre-insert a test Surah in Room
        val ayahEntity = CachedAyahEntity(
            surahNumber = 112,
            ayahNumber = 1,
            textAr = "قُلْ هُوَ اللَّهُ أَحَدٌ",
            translationEn = "Say, He is Allah, [who is] One,",
            translationBn = "বলুন, তিনি আল্লাহ, এক,",
            pronunciationBn = "কুল হুওয়াল্লাহু আহাদ"
        )
        dao.insertCachedAyahs(listOf(ayahEntity))

        // Request through QuranRepository with dao
        val verses = QuranRepository.getSurahVerses(112, dao)
        assertNotNull(verses)
        assertTrue(verses.isNotEmpty())
        assertEquals(112, verses[0].surahNumber)
        assertEquals("قُلْ هُوَ اللَّهُ أَحَدٌ", verses[0].textAr)
    }

    @Test
    fun testSeedBundledSurahsPopulatesRoom() = runBlocking {
        QuranRepository.seedBundledSurahs(dao)

        val cachedSurahNumbers = dao.getCachedSurahNumbersFlow().first()
        assertTrue(cachedSurahNumbers.contains(1))
        assertTrue(cachedSurahNumbers.contains(112))
        assertTrue(cachedSurahNumbers.contains(114))

        val fatihah = dao.getCachedAyahs(1)
        assertEquals(7, fatihah.size)
    }
}
