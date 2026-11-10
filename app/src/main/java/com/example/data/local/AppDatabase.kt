package com.example.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameEn: String,
    val surahNameAr: String,
    val ayahTextAr: String,
    val ayahTranslation: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val id: Int = 1,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameEn: String,
    val surahNameAr: String,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbih_counters")
data class TasbihEntity(
    @PrimaryKey val dhikrId: String,
    val phraseAr: String,
    val phraseEn: String,
    val meaningEn: String,
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val totalLaps: Int = 0,
    val lifetimeCount: Int = 0
)

@Dao
interface NoorDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: Long)

    @Query("SELECT * FROM bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun findBookmark(surahNumber: Int, ayahNumber: Int): BookmarkEntity?

    @Query("SELECT * FROM reading_progress WHERE id = 1")
    fun getReadingProgress(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    @Query("SELECT * FROM tasbih_counters")
    fun getAllTasbih(): Flow<List<TasbihEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbih(tasbih: TasbihEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultTasbih(tasbih: List<TasbihEntity>)
}

@Database(
    entities = [BookmarkEntity::class, ReadingProgressEntity::class, TasbihEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noorDao(): NoorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
