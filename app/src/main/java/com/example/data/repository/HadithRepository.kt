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
        ),
        Hadith(
            id = 8,
            textAr = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ.",
            textEn = "Whoever travels on a path in search of knowledge, Allah will make easy for him a path to Paradise.",
            textBn = "যে ব্যক্তি জ্ঞানের সন্ধানে কোনো পথ অবলম্বন করে, আল্লাহ তার জন্য জান্নাতের পথ সহজ করে দেন।",
            textMs = "Barangsiapa yang menempuh satu jalan untuk menuntut ilmu, maka Allah akan memudahkan baginya jalan ke syurga.",
            book = "Sahih Muslim",
            hadithNumber = "Hadith 2699",
            narrator = "Abu Hurairah (RA)",
            chapter = "Knowledge",
            grading = "Sahih (Authentic)",
            commentary = "Seeking beneficial knowledge, whether sacred or worldly knowledge used for the benefit of humanity, is among the highest virtues in Islam."
        ),
        Hadith(
            id = 9,
            textAr = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ.",
            textEn = "Purity is half of faith, and saying 'Alhamdulillah' (Praise be to Allah) fills the scale.",
            textBn = "পবিত্রতা ঈমানের অর্ধেক, আর 'আলহামদুলিল্লাহ' (সমস্ত প্রশংসা আল্লাহর) মিযানের পাল্লা পূর্ণ করে দেয়।",
            textMs = "Kebersihan dan kesucian itu adalah sebahagian daripada iman, dan 'Alhamdulillah' memenuhi timbangan.",
            book = "Sahih Muslim",
            hadithNumber = "Hadith 223",
            narrator = "Abu Malik al-Ash'ari (RA)",
            chapter = "Purification",
            grading = "Sahih (Authentic)",
            commentary = "Outer cleanliness through Wudu and inner purity from arrogance and jealousy are foundational pillars of a believer's spiritual state."
        ),
        Hadith(
            id = 10,
            textAr = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ، ارْحَمُوا مَنْ فِي الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ.",
            textEn = "The merciful are shown mercy by the Most Merciful. Show mercy to those on earth, and the One in the heavens will show mercy to you.",
            textBn = "দয়াশীলদের প্রতি পরম দয়ালু আল্লাহ দয়া প্রদর্শন করেন। তোমরা জমিনবাসীর প্রতি দয়া কর, তবে আসমানবাসী তোমাদের প্রতি দয়া করবেন।",
            textMs = "Orang-orang yang penyayang akan disayangi oleh Yang Maha Pengasih. Sayangilah siapa yang ada di bumi, nescaya kamu akan disayangi oleh yang di langit.",
            book = "Sunan Abi Dawud & Jami` at-Tirmidhi",
            hadithNumber = "Hadith 4941",
            narrator = "Abdullah ibn Amr (RA)",
            chapter = "Manners and Compassion",
            grading = "Sahih (Authentic)",
            commentary = "Mercy is all-encompassing, extending to family, orphans, strangers, animals, and the natural environment."
        ),
        Hadith(
            id = 11,
            textAr = "عَلَيْكُمْ بِالصِّدْقِ، فَإِنَّ الصِّدْقَ يَهْدِي إِلَى الْبِرِّ، وَإِنَّ الْبِرَّ يَهْدِي إِلَى الْجَنَّةِ.",
            textEn = "Adhere to truthfulness, for truthfulness leads to righteousness, and righteousness leads to Paradise.",
            textBn = "তোমরা সর্বদা সত্য অবলম্বন কর। কারণ সত্যবাদিতা পুণ্যের দিকে নিয়ে যায়, আর পুণ্য জান্নাতের দিকে পরিচালিত করে।",
            textMs = "Hendaklah kamu sentiasa bercakap benar, kerana kebenaran itu membawa kepada kebaikan dan kebaikan itu membawa ke syurga.",
            book = "Sahih al-Bukhari & Muslim",
            hadithNumber = "Hadith 6094",
            narrator = "Abdullah ibn Mas'ud (RA)",
            chapter = "Good Manners and Sincerity",
            grading = "Sahih (Authentic)",
            commentary = "Truthfulness builds unshakeable peace of mind and trustworthy relationships, whereas deception erodes spiritual integrity."
        ),
        Hadith(
            id = 12,
            textAr = "الدُّعَاءُ هُوَ الْعِبَادَةُ.",
            textEn = "Supplication (Du'a) is the essence of worship.",
            textBn = "দোয়া (আল্লাহর কাছে প্রার্থনা) নিজেই একটি পূর্ণাঙ্গ ইবাদত।",
            textMs = "Doa itu adalah intipati ibadah.",
            book = "Sunan Abi Dawud & Jami` at-Tirmidhi",
            hadithNumber = "Hadith 1479",
            narrator = "An-Nu'man ibn Bashir (RA)",
            chapter = "Supplications",
            grading = "Sahih (Authentic)",
            commentary = "When a servant raises hands in sincere prayer, they acknowledge their ultimate dependence upon the Creator."
        ),
        Hadith(
            id = 13,
            textAr = "مَنْ لا يَشْكُرُ النَّاسَ لا يَشْكُرُ اللَّهَ.",
            textEn = "Whoever does not express gratitude to people does not show gratitude to Allah.",
            textBn = "যে ব্যক্তি মানুষের প্রতি কৃতজ্ঞতা প্রকাশ করে না, সে আল্লাহর প্রতিও কৃতজ্ঞতা জ্ঞাপন করে না।",
            textMs = "Sesiapa yang tidak berterima kasih kepada manusia, dia tidak bersyukur kepada Allah.",
            book = "Sunan Abi Dawud & Jami` at-Tirmidhi",
            hadithNumber = "Hadith 4811",
            narrator = "Abu Hurairah (RA)",
            chapter = "Virtues of Gratitude",
            grading = "Sahih (Authentic)",
            commentary = "Expressing heartfelt appreciation to those who help us is an integral part of thanking Allah Almighty."
        ),
        Hadith(
            id = 14,
            textAr = "وَمَنْ يَتَصَبَّرْ يُصَبِّرْهُ اللَّهُ، وَمَا أُعْطِيَ أَحَدٌ عَطَاءً خَيْرًا وَأَوْسَعَ مِنَ الصَّبْرِ.",
            textEn = "Whoever practices patience, Allah will grant him patience. No one has been given a gift better and more comprehensive than patience.",
            textBn = "যে ব্যক্তি ধৈর্য ধারণের চেষ্টা করে, আল্লাহ তাকে ধৈর্যশীল করেন। ধৈর্যের চেয়ে উত্তম ও প্রশস্ত আর কোনো দান কাউকে দেওয়া হয়নি।",
            textMs = "Barangsiapa berusaha untuk sabar, maka Allah akan menjadikannya sabar. Dan tiada anugerah yang lebih baik dan lebih luas daripada kesabaran.",
            book = "Sahih al-Bukhari & Muslim",
            hadithNumber = "Hadith 1469",
            narrator = "Abu Sa'id al-Khudri (RA)",
            chapter = "Patience and Endurance",
            grading = "Sahih (Authentic)",
            commentary = "Patience (Sabr) is an active spiritual resilience that strengthens the soul through hardship and steady devotion."
        ),
        Hadith(
            id = 15,
            textAr = "رِضَى الرَّبِّ فِي رِضَى الْوَالِدِ، وَسَخَطُ الرَّبِّ فِي سَخَطِ الْوَالِدِ.",
            textEn = "The pleasure of the Lord lies in the pleasure of the parent, and the displeasure of the Lord lies in the displeasure of the parent.",
            textBn = "পিতার সন্তুষ্টির মধ্যেই রবের সন্তুষ্টি এবং পিতার অসন্তুষ্টির মধ্যেই রবের অসন্তুষ্টি নিহিত।",
            textMs = "Keredaan Tuhan terletak pada keredaan ibu bapa, dan kemurkaan Tuhan terletak pada kemurkaan ibu bapa.",
            book = "Jami` at-Tirmidhi",
            hadithNumber = "Hadith 1899",
            narrator = "Abdullah ibn Amr (RA)",
            chapter = "Kindness to Parents",
            grading = "Sahih (Authentic)",
            commentary = "Honoring, respecting, and serving one's parents is the most direct avenue to obtaining divine pleasure and mercy."
        ),
        Hadith(
            id = 16,
            textAr = "مَنْ نَفَّسَ عَنْ مُؤْمِنٍ كُرْبَةً مِنْ كُرَبِ الدُّنْيَا، نَفَّسَ اللَّهُ عَنْهُ كُرْبَةً مِنْ كُرَبِ يَوْمِ الْقِيَامَةِ.",
            textEn = "Whoever relieves a believer of a distress from the distresses of this world, Allah will relieve him of a distress from the distresses of the Day of Resurrection.",
            textBn = "যে ব্যক্তি কোনো মুমিনের দুনিয়ার কোনো কষ্ট দূর করবে, আল্লাহ কিয়ামতের দিন তার কষ্টসমূহ থেকে একটি কষ্ট দূর করবেন।",
            textMs = "Barangsiapa melepaskan kesusahan seorang mukmin di dunia, Allah akan melepaskan kesusahannya pada hari kiamat.",
            book = "Sahih Muslim",
            hadithNumber = "Hadith 2699",
            narrator = "Abu Hurairah (RA)",
            chapter = "Cooperation in Righteousness",
            grading = "Sahih (Authentic)",
            commentary = "Extending a helping hand to fellow human beings invites divine protection when one is most in need on the Day of Judgment."
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

    fun getRandomHadith(excludeId: Int? = null): Hadith {
        val candidates = if (excludeId != null && verifiedHadithList.size > 1) {
            verifiedHadithList.filter { it.id != excludeId }
        } else {
            verifiedHadithList
        }
        return candidates.random()
    }

    fun getNextHadith(currentId: Int): Hadith {
        val currentIndex = verifiedHadithList.indexOfFirst { it.id == currentId }
        val nextIndex = if (currentIndex != -1) (currentIndex + 1) % verifiedHadithList.size else 0
        return verifiedHadithList[nextIndex]
    }
}
