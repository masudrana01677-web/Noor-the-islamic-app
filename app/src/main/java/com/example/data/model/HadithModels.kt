package com.example.data.model

data class Hadith(
    val id: Int,
    val textAr: String,
    val textEn: String,
    val textBn: String = "",
    val textMs: String = "",
    val book: String,
    val hadithNumber: String,
    val narrator: String,
    val chapter: String,
    val grading: String,
    val commentary: String
) {
    fun getTranslation(languageCode: String): String {
        return when (languageCode.lowercase()) {
            "bn" -> textBn.ifEmpty { textEn }
            "ms" -> textMs.ifEmpty { textEn }
            else -> textEn
        }
    }
}
