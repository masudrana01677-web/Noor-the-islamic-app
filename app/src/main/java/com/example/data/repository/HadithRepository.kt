package com.example.data.repository

import com.example.data.model.Hadith
import java.util.Calendar

object HadithRepository {

    val verifiedHadithList: List<Hadith> = listOf(
        Hadith(
            id = 1,
            textAr = "إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا، فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ.",
            textEn = "Actions are judged by intentions, and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.",
            textBn = "সকল কাজের ফলাফল নিয়তের উপর নির্ভরশীল। প্রত্যেক ব্যক্তি কেবল তাই পাবে যা সে নিয়ত করেছে।",
            textMs = "Sesungguhnya setiap amalan itu dinilai berdasarkan niat, dan sesungguhnya bagi setiap orang apa yang dia niatkan.",
            book = "Sahih al-Bukhari",
            hadithNumber = "Hadith 1",
            narrator = "Umar ibn al-Khattab (RA)",
            chapter = "Revelation",
            grading = "Sahih (Authentic)",
            commentary = "This foundational hadith is considered by scholars to be a third of Islam. It reminds us that our secret intentions purify every daily act."
        ),
        Hadith(
            id = 2,
            textAr = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ، وَالْمُهَاجِرُ مَنْ هَجَرَ مَا نَهَى اللَّهُ عَنْهُ.",
            textEn = "A Muslim is the one from whose tongue and hands the Muslims are safe; and a Muhajir (emigrant) is the one who refrains from what Allah has forbidden.",
            textBn = "প্রকৃত মুসলিম সেই ব্যক্তি যার জিহ্বা ও হাত থেকে অন্যান্য মুসলিমরা নিরাপদ থাকে।",
            textMs = "Seorang Muslim adalah mereka yang menyelamatkan kaum Muslimin daripada kejahatan lidah dan tangannya.",
            book = "Sahih al-Bukhari",
            hadithNumber = "Hadith 10",
            narrator = "Abdullah ibn Amr (RA)",
            chapter = "Belief",
            grading = "Sahih (Authentic)",
            commentary = "True faith manifests in non-harm, protecting others from gossip, insults, and oppression, while actively emigrating away from sins."
        ),
        Hadith(
            id = 3,
            textAr = "لا يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ.",
            textEn = "None of you truly believes until he loves for his brother what he loves for himself.",
            textBn = "তোমাদের কেউ ততক্ষণ পর্যন্ত পূর্ণ ঈমানদার হতে পারবে না, যতক্ষণ না সে তার ভাইয়ের জন্য তা পছন্দ করবে যা সে নিজের জন্য পছন্দ করে।",
            textMs = "Tidak sempurna iman seseorang daripada kamu sehingga dia mencintai untuk saudaranya apa yang dia cintai untuk dirinya sendiri.",
            book = "Sahih al-Bukhari & Muslim",
            hadithNumber = "Hadith 13",
            narrator = "Anas ibn Malik (RA)",
            chapter = "Faith",
            grading = "Sahih (Authentic)",
            commentary = "Cultivates empathy and sincere brotherhood. The heart freed from jealousy rejoices in the blessings bestowed on others."
        ),
        Hadith(
            id = 4,
            textAr = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ.",
            textEn = "The best among you are those who learn the Quran and teach it to others.",
            textBn = "তোমাদের মধ্যে সর্বোত্তম ব্যক্তি সে, যে কুরআন শিখে এবং তা শিক্ষা দেয়।",
            textMs = "Sebaik-baik kamu adalah orang yang mempelajari al-Quran dan mengajarkannya.",
            book = "Sahih al-Bukhari",
            hadithNumber = "Hadith 5027",
            narrator = "Uthman ibn Affan (RA)",
            chapter = "Virtues of the Quran",
            grading = "Sahih (Authentic)",
            commentary = "The Quran is Allah's direct speech. Both the student seeking understanding and the generous teacher carry the highest rank."
        ),
        Hadith(
            id = 5,
            textAr = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ.",
            textEn = "Whoever believes in Allah and the Last Day, let him speak good or remain silent.",
            textBn = "যে ব্যক্তি আল্লাহ ও পরকালের প্রতি ঈমান রাখে, সে যেন ভালো কথা বলে অথবা চুপ থাকে।",
            textMs = "Barangsiapa yang beriman kepada Allah dan hari akhirat, maka hendaklah dia berkata baik atau diam.",
            book = "Sahih al-Bukhari & Muslim",
            hadithNumber = "Hadith 6018",
            narrator = "Abu Hurairah (RA)",
            chapter = "Good Manners",
            grading = "Sahih (Authentic)",
            commentary = "Mindful speech is a direct sign of true faith. When words bring no benefit, silence preserves heart and peace."
        ),
        Hadith(
            id = 6,
            textAr = "إِنَّ اللَّهَ رَفِيقٌ يُحِبُّ الرِّفْقَ فِي الأَمْرِ كُلِّهِ.",
            textEn = "Indeed, Allah is Gentle and He loves gentleness in all matters.",
            textBn = "নিশ্চয়ই আল্লাহ কোমল এবং তিনি সকল বিষয়ে কোমলতা পছন্দ করেন।",
            textMs = "Sesungguhnya Allah itu Maha Lembut dan menyukai kelembutan dalam setiap perkara.",
            book = "Sahih al-Bukhari",
            hadithNumber = "Hadith 6927",
            narrator = "Aishah (RA)",
            chapter = "Manners and Softening of Hearts",
            grading = "Sahih (Authentic)",
            commentary = "Gentleness adorns everything it touches. Reacting with patience and softness dissolves conflict and draws divine mercy."
        ),
        Hadith(
            id = 7,
            textAr = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ.",
            textEn = "Your smiling in the face of your brother is an act of charity for you.",
            textBn = "তোমার ভাইয়ের মুখের দিকে তাকিয়ে তোমার মুচকি হাসিও একটি সদকা।",
            textMs = "Senyumanmu di hadapan saudaramu adalah sedekah bagimu.",
            book = "Jami` at-Tirmidhi",
            hadithNumber = "Hadith 1956",
            narrator = "Abu Dharr (RA)",
            chapter = "Righteousness and Kindness",
            grading = "Sahih (Hasan Gharib)",
            commentary = "Charity in Islam is not restricted to wealth; an uplifting smile, kind gesture, or friendly glance carries heavenly reward."
        )
    )

    fun getDailyHadith(calendar: Calendar = Calendar.getInstance()): Hadith {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % verifiedHadithList.size
        return verifiedHadithList[index]
    }

    fun getHadithById(id: Int): Hadith {
        return verifiedHadithList.firstOrNull { it.id == id } ?: verifiedHadithList.first()
    }
}
