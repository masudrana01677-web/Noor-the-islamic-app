package com.example.data.model

data class Surah(
    val number: Int,
    val nameEn: String,
    val nameAr: String,
    val translationEn: String,
    val totalVerses: Int,
    val revelationType: RevelationType,
    val juzStart: Int
)

enum class RevelationType(val label: String) {
    MECCAN("Meccan"),
    MEDINAN("Medinan")
}

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textAr: String,
    val translationEn: String,
    val translationBn: String = "",
    val translationMs: String = "",
    val translationId: String = "",
    val translationUr: String = "",
    val translationFr: String = "",
    val juz: Int = 1,
    val pronunciationBn: String = ""
) {
    fun getTranslation(languageCode: String): String {
        return when (languageCode.lowercase()) {
            "bn" -> translationBn.ifEmpty { translationEn }
            "ms" -> translationMs.ifEmpty { translationEn }
            "id" -> translationId.ifEmpty { translationEn }
            "ur" -> translationUr.ifEmpty { translationEn }
            "fr" -> translationFr.ifEmpty { translationEn }
            else -> translationEn
        }
    }

    fun getBengaliPronunciation(): String {
        return pronunciationBn.ifEmpty {
            com.example.data.repository.BengaliQuranPronunciationHelper.getPronunciation(surahNumber, ayahNumber, textAr)
        }
    }

    fun getAudioUrl(reciterId: String = "alafasy"): String {
        val s = String.format("%03d", surahNumber)
        val a = String.format("%03d", ayahNumber)
        val baseUrl = when (reciterId) {
            "abdulbasit" -> "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps"
            "husary" -> "https://everyayah.com/data/Husary_128kbps"
            "ghamdi" -> "https://everyayah.com/data/Ghamadi_40kbps"
            "shatri" -> "https://everyayah.com/data/Abu_Bakr_Ash-Shaatree_128kbps"
            else -> "https://everyayah.com/data/Alafasy_128kbps"
        }
        return "$baseUrl/$s$a.mp3"
    }
}

data class Reciter(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val style: String
)

object ReciterCatalog {
    val reciters = listOf(
        Reciter("alafasy", "Mishary Rashid Alafasy", "مشاري راشد العفاسي", "Murattal"),
        Reciter("abdulbasit", "Abdul Basit Abdul Samad", "عبد الباسط عبد الصمد", "Murattal"),
        Reciter("husary", "Mahmoud Khalil Al-Husary", "محمود خليل الحصري", "Murattal"),
        Reciter("ghamdi", "Saad Al-Ghamdi", "سعد الغامدي", "Murattal"),
        Reciter("shatri", "Abu Bakr Al-Shatri", "أبو بكر الشاطري", "Murattal")
    )
}
