package com.example.data.repository

import android.util.Log
import com.example.data.model.Ayah
import com.example.data.model.RevelationType
import com.example.data.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object QuranRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val cache = ConcurrentHashMap<Int, List<Ayah>>()

    val allSurahs: List<Surah> = listOf(
        Surah(1, "Al-Fatihah", "الفاتحة", "The Opening", 7, RevelationType.MECCAN, 1),
        Surah(2, "Al-Baqarah", "البقرة", "The Cow", 286, RevelationType.MEDINAN, 1),
        Surah(3, "Ali 'Imran", "آل عمران", "Family of Imran", 200, RevelationType.MEDINAN, 3),
        Surah(4, "An-Nisa", "النساء", "The Women", 176, RevelationType.MEDINAN, 4),
        Surah(5, "Al-Ma'idah", "المائدة", "The Table Spread", 120, RevelationType.MEDINAN, 6),
        Surah(6, "Al-An'am", "الأنعام", "The Cattle", 165, RevelationType.MECCAN, 7),
        Surah(7, "Al-A'raf", "الأعراف", "The Heights", 206, RevelationType.MECCAN, 8),
        Surah(8, "Al-Anfal", "الأنفال", "The Spoils of War", 75, RevelationType.MEDINAN, 9),
        Surah(9, "At-Tawbah", "التوبة", "The Repentance", 129, RevelationType.MEDINAN, 10),
        Surah(10, "Yunus", "يونس", "Jonah", 109, RevelationType.MECCAN, 11),
        Surah(11, "Hud", "هود", "Hud", 123, RevelationType.MECCAN, 11),
        Surah(12, "Yusuf", "يوسف", "Joseph", 111, RevelationType.MECCAN, 12),
        Surah(13, "Ar-Ra'd", "الرعد", "The Thunder", 43, RevelationType.MEDINAN, 13),
        Surah(14, "Ibrahim", "إبراهيم", "Abraham", 52, RevelationType.MECCAN, 13),
        Surah(15, "Al-Hijr", "الحجر", "The Rocky Tract", 99, RevelationType.MECCAN, 14),
        Surah(16, "An-Nahl", "النحل", "The Bee", 128, RevelationType.MECCAN, 14),
        Surah(17, "Al-Isra", "الإسراء", "The Night Journey", 111, RevelationType.MECCAN, 15),
        Surah(18, "Al-Kahf", "الكهف", "The Cave", 110, RevelationType.MECCAN, 15),
        Surah(19, "Maryam", "مريم", "Mary", 98, RevelationType.MECCAN, 16),
        Surah(20, "Ta-Ha", "طه", "Ta-Ha", 135, RevelationType.MECCAN, 16),
        Surah(21, "Al-Anbiya", "الأنبياء", "The Prophets", 112, RevelationType.MECCAN, 17),
        Surah(22, "Al-Hajj", "الحج", "The Pilgrimage", 78, RevelationType.MEDINAN, 17),
        Surah(23, "Al-Mu'minun", "المؤمنون", "The Believers", 118, RevelationType.MECCAN, 18),
        Surah(24, "An-Nur", "النور", "The Light", 64, RevelationType.MEDINAN, 18),
        Surah(25, "Al-Furqan", "الفرقان", "The Criterion", 77, RevelationType.MECCAN, 18),
        Surah(26, "Ash-Shu'ara", "الشعراء", "The Poets", 227, RevelationType.MECCAN, 19),
        Surah(27, "An-Naml", "النمل", "The Ant", 93, RevelationType.MECCAN, 19),
        Surah(28, "Al-Qasas", "القصص", "The Stories", 88, RevelationType.MECCAN, 20),
        Surah(29, "Al-'Ankabut", "العنكبوت", "The Spider", 69, RevelationType.MECCAN, 20),
        Surah(30, "Ar-Rum", "الروم", "The Romans", 60, RevelationType.MECCAN, 21),
        Surah(31, "Luqman", "لقمان", "Luqman", 34, RevelationType.MECCAN, 21),
        Surah(32, "As-Sajdah", "السجدة", "The Prostration", 30, RevelationType.MECCAN, 21),
        Surah(33, "Al-Ahzab", "الأحزاب", "The Combined Forces", 73, RevelationType.MEDINAN, 21),
        Surah(34, "Saba", "سبأ", "Sheba", 54, RevelationType.MECCAN, 22),
        Surah(35, "Fatir", "فاطر", "Originator", 45, RevelationType.MECCAN, 22),
        Surah(36, "Ya-Sin", "يس", "Ya-Sin", 83, RevelationType.MECCAN, 22),
        Surah(37, "As-Saffat", "الصافات", "Those Who Set The Ranks", 182, RevelationType.MECCAN, 23),
        Surah(38, "Sad", "ص", "The Letter Sad", 88, RevelationType.MECCAN, 23),
        Surah(39, "Az-Zumar", "الزمر", "The Troops", 75, RevelationType.MECCAN, 23),
        Surah(40, "Ghafir", "غافر", "The Forgiver", 85, RevelationType.MECCAN, 24),
        Surah(41, "Fussilat", "فصلت", "Explained In Detail", 54, RevelationType.MECCAN, 24),
        Surah(42, "Ash-Shura", "الشورى", "The Consultation", 53, RevelationType.MECCAN, 25),
        Surah(43, "Az-Zukhruf", "الزخرف", "The Ornaments of Gold", 89, RevelationType.MECCAN, 25),
        Surah(44, "Ad-Dukhan", "الدخان", "The Smoke", 59, RevelationType.MECCAN, 25),
        Surah(45, "Al-Jathiyah", "الجاثية", "The Crouching", 37, RevelationType.MECCAN, 25),
        Surah(46, "Al-Ahqaf", "الأحقاف", "The Wind-Curved Sandhills", 35, RevelationType.MECCAN, 26),
        Surah(47, "Muhammad", "محمد", "Muhammad", 38, RevelationType.MEDINAN, 26),
        Surah(48, "Al-Fath", "الفتح", "The Victory", 29, RevelationType.MEDINAN, 26),
        Surah(49, "Al-Hujurat", "الحجرات", "The Rooms", 18, RevelationType.MEDINAN, 26),
        Surah(50, "Qaf", "ق", "The Letter Qaf", 45, RevelationType.MECCAN, 26),
        Surah(51, "Adh-Dhariyat", "الذاريات", "The Winnowing Winds", 60, RevelationType.MECCAN, 26),
        Surah(52, "At-Tur", "الطور", "The Mount", 49, RevelationType.MECCAN, 27),
        Surah(53, "An-Najm", "النجم", "The Star", 62, RevelationType.MECCAN, 27),
        Surah(54, "Al-Qamar", "القمر", "The Moon", 55, RevelationType.MECCAN, 27),
        Surah(55, "Ar-Rahman", "الرحمن", "The Beneficent", 78, RevelationType.MEDINAN, 27),
        Surah(56, "Al-Waqi'ah", "الواقعة", "The Inevitable", 96, RevelationType.MECCAN, 27),
        Surah(57, "Al-Hadid", "الحديد", "The Iron", 29, RevelationType.MEDINAN, 27),
        Surah(58, "Al-Mujadila", "المجادلة", "The Pleading Woman", 22, RevelationType.MEDINAN, 28),
        Surah(59, "Al-Hashr", "الحشر", "The Exile", 24, RevelationType.MEDINAN, 28),
        Surah(60, "Al-Mumtahanah", "الممتحنة", "She That Is To Be Examined", 13, RevelationType.MEDINAN, 28),
        Surah(61, "As-Saff", "الصف", "The Ranks", 14, RevelationType.MEDINAN, 28),
        Surah(62, "Al-Jumu'ah", "الجمعة", "The Congregation, Friday", 11, RevelationType.MEDINAN, 28),
        Surah(63, "Al-Munafiqun", "المنافقون", "The Hypocrites", 11, RevelationType.MEDINAN, 28),
        Surah(64, "At-Taghabun", "التغابن", "The Mutual Disillusion", 18, RevelationType.MEDINAN, 28),
        Surah(65, "At-Talaq", "الطلاق", "The Divorce", 12, RevelationType.MEDINAN, 28),
        Surah(66, "At-Tahrim", "التحريم", "The Prohibition", 12, RevelationType.MEDINAN, 28),
        Surah(67, "Al-Mulk", "الملك", "The Sovereignty", 30, RevelationType.MECCAN, 29),
        Surah(68, "Al-Qalam", "القلم", "The Pen", 52, RevelationType.MECCAN, 29),
        Surah(69, "Al-Haqqah", "الحاقة", "The Inevitable Reality", 52, RevelationType.MECCAN, 29),
        Surah(70, "Al-Ma'arij", "المعارج", "The Ascending Stairways", 44, RevelationType.MECCAN, 29),
        Surah(71, "Nuh", "نوح", "Noah", 28, RevelationType.MECCAN, 29),
        Surah(72, "Al-Jinn", "الجن", "The Jinn", 28, RevelationType.MECCAN, 29),
        Surah(73, "Al-Muzzammil", "المزمل", "The Enshrouded One", 20, RevelationType.MECCAN, 29),
        Surah(74, "Al-Muddaththir", "المدثر", "The Cloaked One", 56, RevelationType.MECCAN, 29),
        Surah(75, "Al-Qiyamah", "القيامة", "The Resurrection", 40, RevelationType.MECCAN, 29),
        Surah(76, "Al-Insan", "الإنسان", "Man", 31, RevelationType.MEDINAN, 29),
        Surah(77, "Al-Mursalat", "المرسلات", "The Emissaries", 50, RevelationType.MECCAN, 29),
        Surah(78, "An-Naba", "النبأ", "The Tidings", 40, RevelationType.MECCAN, 30),
        Surah(79, "An-Nazi'at", "النازعات", "Those Who Drag Forth", 46, RevelationType.MECCAN, 30),
        Surah(80, "'Abasa", "عبس", "He Frowned", 42, RevelationType.MECCAN, 30),
        Surah(81, "At-Takwir", "التكوير", "The Overthrowing", 29, RevelationType.MECCAN, 30),
        Surah(82, "Al-Infitar", "الانفطار", "The Cleaving", 19, RevelationType.MECCAN, 30),
        Surah(83, "Al-Mutaffifin", "المطففين", "Defrauding", 36, RevelationType.MECCAN, 30),
        Surah(84, "Al-Inshiqaq", "الانشقاق", "The Splitting Open", 25, RevelationType.MECCAN, 30),
        Surah(85, "Al-Buruj", "البروج", "The Mansions of the Stars", 22, RevelationType.MECCAN, 30),
        Surah(86, "At-Tariq", "الطارق", "The Morning Star", 17, RevelationType.MECCAN, 30),
        Surah(87, "Al-A'la", "الأعلى", "The Most High", 19, RevelationType.MECCAN, 30),
        Surah(88, "Al-Ghashiyah", "الغاشية", "The Overwhelming", 26, RevelationType.MECCAN, 30),
        Surah(89, "Al-Fajr", "الفجر", "The Dawn", 30, RevelationType.MECCAN, 30),
        Surah(90, "Al-Balad", "البلد", "The City", 20, RevelationType.MECCAN, 30),
        Surah(91, "Ash-Shams", "الشمس", "The Sun", 15, RevelationType.MECCAN, 30),
        Surah(92, "Al-Layl", "الليل", "The Night", 21, RevelationType.MECCAN, 30),
        Surah(93, "Ad-Duha", "الضحى", "The Morning Hours", 11, RevelationType.MECCAN, 30),
        Surah(94, "Ash-Sharh", "الشرح", "The Relief", 8, RevelationType.MECCAN, 30),
        Surah(95, "At-Tin", "التين", "The Fig", 8, RevelationType.MECCAN, 30),
        Surah(96, "Al-'Alaq", "العلق", "The Clot", 19, RevelationType.MECCAN, 30),
        Surah(97, "Al-Qadr", "القدر", "The Power, Fate", 5, RevelationType.MECCAN, 30),
        Surah(98, "Al-Bayyinah", "البينة", "The Clear Proof", 8, RevelationType.MEDINAN, 30),
        Surah(99, "Az-Zalzalah", "الزلزلة", "The Earthquake", 8, RevelationType.MEDINAN, 30),
        Surah(100, "Al-'Adiyat", "العاديات", "The Courser", 11, RevelationType.MECCAN, 30),
        Surah(101, "Al-Qari'ah", "القارعة", "The Calamity", 11, RevelationType.MECCAN, 30),
        Surah(102, "At-Takathur", "التكاثر", "The Rivalry in World Increase", 8, RevelationType.MECCAN, 30),
        Surah(103, "Al-'Asr", "العصر", "The Declining Day", 3, RevelationType.MECCAN, 30),
        Surah(104, "Al-Humazah", "الهمزة", "The Traducer", 9, RevelationType.MECCAN, 30),
        Surah(105, "Al-Fil", "الفيل", "The Elephant", 5, RevelationType.MECCAN, 30),
        Surah(106, "Quraysh", "قريش", "Quraysh", 4, RevelationType.MECCAN, 30),
        Surah(107, "Al-Ma'un", "الماعون", "Small Kindnesses", 7, RevelationType.MECCAN, 30),
        Surah(108, "Al-Kawthar", "الكوثر", "Abundance", 3, RevelationType.MECCAN, 30),
        Surah(109, "Al-Kafirun", "الكافرون", "The Disbelievers", 6, RevelationType.MECCAN, 30),
        Surah(110, "An-Nasr", "النصر", "The Divine Support", 3, RevelationType.MEDINAN, 30),
        Surah(111, "Al-Masad", "المسد", "The Palm Fiber", 5, RevelationType.MECCAN, 30),
        Surah(112, "Al-Ikhlas", "الإخلاص", "The Sincerity", 4, RevelationType.MECCAN, 30),
        Surah(113, "Al-Falaq", "الفلق", "The Daybreak", 5, RevelationType.MECCAN, 30),
        Surah(114, "An-Nas", "الناس", "Mankind", 6, RevelationType.MECCAN, 30)
    )

    // Pre-bundled authentic verses for immediate, offline, beautiful reading
    private val bundledSurahs: Map<Int, List<Ayah>> = mapOf(
        1 to listOf(
            Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে শুরু করছি।", "Dengan nama Allah, Yang Maha Pemurah, lagi Maha Mengasihani.", "Dengan menyebut nama Allah Yang Maha Pengasih lagi Maha Penyayang.", "اللہ کے نام سے جو رحمان و رحیم ہے", "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux.", 1),
            Ayah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "সমস্ত প্রশংসা আল্লাহ তাআলার যিনি সমগ্র সৃষ্টির পালনকর্তা।", "Segala puji tertentu bagi Allah, Tuhan yang memelihara dan mentadbirkan sekalian alam.", "Segala puji bagi Allah, Tuhan seluruh alam,", "سب تعریف اللہ ہی کے لیے ہے جو تمام جہانوں کا رب ہے", "Louange à Allah, Seigneur de l'univers.", 1),
            Ayah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "যিনি অত্যন্ত মেহেরবান ও পরম দয়ালু।", "Yang Maha Pemurah, lagi Maha Mengasihani.", "Yang Maha Pengasih lagi Maha Penyayang,", "نہایت مہربان اور رحم فرمانے والا ہے", "Le Tout Miséricordieux, le Très Miséricordieux,", 1),
            Ayah(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "যিনি বিচার দিনের মালিক।", "Yang Menguasai pemerintahan hari Pembalasan (hari Akhirat).", "Pemilik hari pembalasan.", "روز جزا کا مالک ہے", "Maître du Jour de la rétribution.", 1),
            Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "আমরা কেবল আপনারই ইবাদত করি এবং আপনারই সাহায্য প্রার্থনা করি।", "Engkaulah sahaja yang kami sembah, dan kepada Engkaulah sahaja kami memohon pertolongan.", "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami memohon pertolongan.", "ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں", "C'est Toi [Seul] que nous adorons, et c'est Toi [Seul] dont nous implorons secours.", 1),
            Ayah(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "আমাদের সরল সঠিক পথ প্রদর্শন করুন।", "Tunjukilah kami jalan yang lurus.", "Tunjukilah kami jalan yang lurus,", "ہمیں سیدھا راستہ دکھا", "Guide-nous dans le droit chemin,", 1),
            Ayah(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "তাদের পথ যাদেরকে আপনি অনুগ্রহ করেছেন, তাদের পথ নয় যারা ক্রোধের শিকার হয়েছে অথবা যারা পথভ্রষ্ট হয়েছে।", "Iaitu jalan orang-orang yang Engkau telah kurniakan nikmat kepada mereka, bukan (jalan) orang-orang yang dimurkai ke atas mereka, dan bukan pula (jalan) orang-orang yang sesat.", "(yaitu) jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan (jalan) mereka yang dimurkai, dan bukan (pula jalan) mereka yang sesat.", "ان لوگوں کا راستہ جن پر تو نے انعام فرمایا، جو معتوب نہیں ہوئے، جو بھٹکے ہوئے نہیں ہیں", "Le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés.", 1)
        ),
        112 to listOf(
            Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, \"He is Allah, [who is] One,", "বলুন, তিনি আল্লাহ, এক,", "Katakanlah (wahai Muhammad): (Tuhanku) ialah Allah Yang Maha Esa;", "Katakanlah (Muhammad), \"Dialah Allah, Yang Maha Esa.", "کہو، وہ اللہ ہے، یکتا", "Dis: «Il est Allah, Unique.", 30),
            Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "আল্লাহ কারও মুখাপেক্ষী নন, সকলেই তাঁর মুখাপেক্ষী।", "Allah Yang menjadi tumpuan sekalian makhluk untuk memohon sebarang hajat;", "Allah tempat meminta segala sesuatu.", "اللہ سب کا سہارا اور بے نیاز ہے", "Allah, Le Seul à être imploré pour ce que nous désirons.", 30),
            Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "তিনি কাউকে জন্ম দেননি এবং তাঁকেও কেউ জন্ম দেয়নি।", "Ia tiada beranak, dan Ia pula tidak diperanakkan;", "(Allah) tidak beranak dan tidak pula diperanakkan,", "نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد", "Il n'a jamais engendré, n'a jamais été engendré non plus.", 30),
            Ayah(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.\"", "এবং তাঁর সমতুল্য কেউই নেই।", "Dan tidak ada sesiapapun yang serupa dengan-Nya.", "Dan tidak ada sesuatu yang setara dengan Dia.\"", "اور کوئی اس کا ہمسر نہیں ہے", "Et nul n'est égal à Lui».", 30)
        ),
        113 to listOf(
            Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, \"I seek refuge in the Lord of daybreak", "বলুন, আমি আশ্রয় প্রার্থনা করছি প্রভাতের পালনকর্তার,", "Katakanlah (wahai Muhammad): Aku berlindung kepada Tuhan yang menciptakan sekalian makhluk,", "Katakanlah, \"Aku berlindung kepada Tuhan yang menguasai subuh (fajar),", "کہو، میں پناہ مانگتا ہوں صبح کے رب کی", "Dis: «Je cherche protection auprès du Seigneur de l'aube naissante,", 30),
            Ayah(113, 2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "তিনি যা সৃষ্টি করেছেন তার অনিষ্ট থেকে,", "Dari bahaya makhluk-makhluk yang Ia ciptakan;", "dari kejahatan (makhluk yang) Dia ciptakan,", "ہر اس چیز کے شر سے جو اس نے پیدا کی ہے", "contre le mal des êtres qu'Il a créés,", 30),
            Ayah(113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "এবং অন্ধকার রাত্রির অনিষ্ট থেকে যখন তা সমাগত হয়,", "Dan dari bahaya gelap apabila ia masuk;", "dan dari kejahatan malam apabila telah gelap gulita,", "اور رات کی تاریکی کے شر سے جب کہ وہ چھا جائے", "contre le mal de l'obscurité quand elle s'approfondit,", 30),
            Ayah(113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "এবং গ্রন্থিতে ফুৎকারকারিণীদের অনিষ্ট থেকে,", "Dan dari bahaya sekalian wanita yang menghembus pada simpulan-simpulan;", "dan dari kejahatan (perempuan-perempuan) penyihir yang meniup pada buhul-buhul (talinya),", "اور گرہوں میں پھونکنے والوں کے شر سے", "contre le mal de celles qui soufflent sur les nœuds,", 30),
            Ayah(113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.\"", "এবং হিংসুকের অনিষ্ট থেকে যখন সে হিংসা করে।", "Dan dari bahaya orang yang dengki apabila ia melakukan kedengkiannya.", "dan dari kejahatan orang yang dengki apabila dia dengki.\"", "اور حاسد کے شر سے جب کہ وہ حسد کرے", "et contre le mal de l'envieux quand il envie».", 30)
        ),
        114 to listOf(
            Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, \"I seek refuge in the Lord of mankind,", "বলুন, আমি আশ্রয় প্রার্থনা করছি মানুষের পালনকর্তার,", "Katakanlah (wahai Muhammad): Aku berlindung kepada Tuhan sekalian manusia.", "Katakanlah, \"Aku berlindung kepada Tuhannya manusia,", "کہو، میں پناہ مانگتا ہوں انسانوں کے پروردگار کی", "Dis: «Je cherche protection auprès du Seigneur des hommes.", 30),
            Ayah(114, 2, "مَلِكِ النَّاسِ", "The Sovereign of mankind,", "মানুষের অধিপতির,", "Raja sekalian manusia,", "Raja manusia,", "انسانوں کے بادشاہ کی", "Le Souverain des hommes,", 30),
            Ayah(114, 3, "إِلَٰهِ النَّاسِ", "The God of mankind,", "মানুষের উপাস্যের,", "Tuhan sekalian manusia,", "sembahan manusia,", "انسانوں کے حقیقی معبود کی", "Dieu des hommes,", 30),
            Ayah(114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "কুপ্ররোচকের অনিষ্ট থেকে যে আত্মগোপন করে,", "Dari kejahatan bisikan syaitan yang timbul tenggelam,", "dari kejahatan (bisikan) setan yang bersembunyi,", "وسوسہ ڈالنے والے کے شر سے جو پیچھے ہٹ جاتا ہے", "contre le mal du mauvais conseiller, furtif,", 30),
            Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [evil] into the breasts of mankind -", "যে মানুষের অন্তরে প্ররোচনা দেয়,", "Yang melemparkan bisikan dan hasutannya ke dalam dada manusia,", "yang membisikkan (kejahatan) ke dalam dada manusia,", "جو لوگوں کے دلوں میں وسوسے ڈالتا ہے", "qui souffle le mal dans les poitrines des hommes,", 30),
            Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.\"", "জিনদের মধ্য থেকে এবং মানুষের মধ্য থেকে।", "(Iaitu pembisik dan penghasut) dari kalangan jin dan manusia.", "dari (golongan) jin dan manusia.\"", "خواہ وہ جنوں میں سے ہو یا انسانوں میں سے", "qu'il soit, (le conseiller), parmi les djinns, ou parmi les hommes».", 30)
        ),
        108 to listOf(
            Ayah(108, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "নিশ্চয় আমি আপনাকে কাওসার (অফুরন্ত কল্যাণ) দান করেছি।", "Sesungguhnya Kami telah mengurniakan kepadamu (wahai Muhammad) kebaikan yang banyak (di dunia dan di akhirat).", "Sungguh, Kami telah memberimu (Muhammad) nikmat yang banyak.", "ہم نے تمہیں کوثر عطا کر دیا", "Nous t'avons certes, accordé l'Abondance.", 30),
            Ayah(108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone].", "অতএব আপনার রবের উদ্দেশ্যে সালাত আদায় করুন এবং কোরবানী করুন।", "Oleh itu, kerjakanlah sembahyang kerana Tuhanmu semata-mata, dan sembelihlah korban (sebagai bersyukur).", "Maka laksanakanlah salat karena Tuhanmu, dan berkurbanlah.", "پس تم اپنے رب ہی کے لیے نماز پڑھو اور قربانی کرو", "Accomplis donc la Salât pour ton Seigneur et sacrifie.", 30),
            Ayah(108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.", "নিশ্চয় আপনার শত্রুই নির্বংশ।", "Sesungguhnya orang yang bencikan engkau, dialah yang terputus (dari mendapat sebarang perkara yang baik).", "Sungguh, orang-orang yang membencimu dialah yang terputus (dari rahmat Allah).", "یقیناً تمہارا دشمن ہی جڑ کٹا ہے", "Certes, celui qui te hait sera certes, sans postérité.", 30)
        ),
        109 to listOf(
            Ayah(109, 1, "قُلْ يَا أَيُّهَا الْكَافِرُونَ", "Say, \"O disbelievers,", "বলুন, হে কাফেরেরা!", "Katakanlah (wahai Muhammad): Hai orang-orang kafir!", "Katakanlah (Muhammad), \"Wahai orang-orang kafir!", "کہو، اے کافرو!", "Dis: «Ô vous les infidèles!", 30),
            Ayah(109, 2, "لَا أَعْبُدُ مَا تَعْبُدُونَ", "I do not worship what you worship.", "আমি তাদের ইবাদত করি না যাদের তোমরা ইবাদত কর,", "Aku tidak akan menyembah apa yang kamu sembah.", "aku tidak akan menyembah apa yang kamu sembah,", "میں اُن کی عبادت نہیں کرتا جن کی تم عبادت کرتے ہو", "Je n'adore pas ce que vous adorez.", 30),
            Ayah(109, 3, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "Nor are you worshippers of what I worship.", "এবং তোমরাও তাঁর ইবাদতকারী নও যাঁর ইবাদত আমি করি,", "Dan kamu tidak mahu menyembah (Allah) yang aku sembah.", "Dan kamu bukan penyembah apa yang aku sembah,", "اور نہ تم اس کی عبادت کرنے والے ہو جس کی میں عبادت کرتا ہوں", "Et vous n'êtes pas adorateurs de ce que j'adore.", 30),
            Ayah(109, 4, "وَلَا أَنَا عَابِدٌ مَّا عَبَدتُّمْ", "Nor will I be a worshipper of what you worship.", "এবং আমি তাদের ইবাদতকারী হব না যাদের তোমরা ইবাদত করেছ,", "Dan aku tidak akan menjadi penyembah apa yang kamu sembah.", "Dan aku tidak pernah menjadi penyembah apa yang kamu sembah,", "اور نہ میں اُن کی عبادت کرنے والا ہوں جن کی تم نے عبادت کی ہے", "Je ne suis pas adorateur de ce que vous adorez.", 30),
            Ayah(109, 5, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "Nor will you be worshippers of what I worship.", "এবং তোমরাও তাঁর ইবাদতকারী হবে না যাঁর ইবাদত আমি করি।", "Dan kamu tidak mahu menyembah (Allah) yang aku sembah.", "dan kamu tidak pernah (pula) menjadi penyembah apa yang aku sembah.", "اور نہ تم اس کی عبادت کرنے والے ہو جس کی میں عبادت کرتا ہوں", "Et vous n'êtes pas adorateurs de ce que j'adore.", 30),
            Ayah(109, 6, "لَكُمْ دِينُكُمْ وَلِيَ دِينِ", "For you is your religion, and for me is my religion.\"", "তোমাদের দ্বীন তোমাদের জন্য, আর আমার দ্বীন আমার জন্য।", "Bagi kamu ugama kamu, dan bagiku ugamaku.", "Untukmu agamamu, dan untukku agamaku.\"", "تمہارے لیے تمہارا دین ہے اور میرے لیے میرا دین", "A vous votre religion, et à moi ma religion».", 30)
        ),
        67 to listOf(
            Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -", "পরম কল্যাণময় তিনি, যাঁর হাতে রাজত্ব এবং তিনি সর্ববিষয়ে সর্বশক্তিমান।", "Maha Berkat (serta Maha Tinggilah kelebihan) Tuhan yang menguasai pemerintahan (dunia dan akhirat); dan memanglah Ia Maha Kuasa atas tiap-tiap sesuatu;", "Mahasuci Allah yang menguasai (segala) kerajaan, dan Dia Mahakuasa atas segala sesuatu.", "نہایت بزرگ و برتر ہے وہ جس کے ہاتھ میں کائنات کی سلطنت ہے، اور وہ ہر چیز پر قادر ہے", "Béni soit celui dans la main de qui est la royauté, et Il est Omnipotent.", 29),
            Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "যিনি সৃষ্টি করেছেন মৃত্যু ও জীবন, যাতে তোমাদের পরীক্ষা করেন যে কে তোমাদের মধ্যে কর্মে উত্তম; এবং তিনি পরাক্রমশালী, ক্ষমাশীল।", "Dia lah yang telah mentakdirkan adanya mati dan hidup (kamu) - untuk menguji dan menzahirkan keadaan kamu: siapakah di antara kamu yang lebih baik amalnya; dan Ia Maha Kuasa (membalas amal kamu), lagi Maha Pengampun,", "Yang menciptakan mati dan hidup, untuk menguji kamu, siapa di antara kamu yang lebih baik amalnya. Dan Dia Mahaperkasa, Maha Pengampun,", "جس نے موت اور زندگی کو پیدا کیا تاکہ تم لوگوں کو آزما کر دیکھے تم میں سے کون بہتر عمل کرنے والا ہے", "Celui qui a créé la mort et la vie afin de vous éprouver (et de savoir) qui de vous est le meilleur en œuvre, et c'est Lui le Puissant, le Pardonneur.", 29),
            Ayah(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency. So return [your] vision [to the sky]; do you see any breaks?", "যিনি সাত আকাশ স্তরে স্তরে সৃষ্টি করেছেন। পরম করুণাময়ের সৃষ্টিতে তুমি কোন খুঁত দেখতে পাবে না। আবার চোখ ফিরিয়ে দেখ, কোন ত্রুটি দেখতে পাও কি?", "Dia lah yang telah menciptakan tujuh petala langit berlapis-lapis; engkau tidak dapat melihat pada ciptaan Allah Yang Maha Pemurah itu sebarang keadaan yang tidak seimbang dan tidak munasabah; (jika engkau ragu-ragu) maka ulangilah pandangan - dapatkah engkau melihat sebarang kecacatan?", "Yang menciptakan tujuh langit berlapis-lapis. Tidak akan kamu lihat sesuatu yang tidak seimbang pada ciptaan Tuhan Yang Maha Pengasih. Maka lihatlah sekali lagi, adakah kamu lihat sesuatu yang cacat?", "جس نے تہ بر تہ سات آسمان بنائے تم رحمن کی تخلیق میں کسی قسم کا کوئی نقص نہ دیکھو گے، پھر پلٹ کر دیکھو، کہیں تمہیں کوئی خلل نظر آتا ہے؟", "Celui qui a créé sept cieux superposés sans que tu voies de disproportion en la création du Tout Miséricordieux. Ramène [sur elle] le regard. Y vois-tu une brèche quelconque?", 29)
        )
    )

    init {
        bundledSurahs.forEach { (number, ayahs) ->
            cache[number] = ayahs
        }
    }

    suspend fun getSurahVerses(surahNumber: Int): List<Ayah> {
        // Return from cache if already loaded
        cache[surahNumber]?.let { return it }

        // Fetch from API in background thread
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val jsonStr = response.body?.string()

                if (response.isSuccessful && jsonStr != null) {
                    val root = JSONObject(jsonStr)
                    val data = root.getJSONArray("data")
                    val arabicEdition = data.getJSONObject(0).getJSONArray("ayahs")
                    val englishEdition = data.getJSONObject(1).getJSONArray("ayahs")

                    val result = mutableListOf<Ayah>()
                    for (i in 0 until arabicEdition.length()) {
                        val arObj = arabicEdition.getJSONObject(i)
                        val enObj = englishEdition.getJSONObject(i)
                        val ayahNum = arObj.getInt("numberInSurah")
                        val textAr = arObj.getString("text")
                        val textEn = enObj.getString("text")
                        val juz = arObj.optInt("juz", 1)

                        result.add(
                            Ayah(
                                surahNumber = surahNumber,
                                ayahNumber = ayahNum,
                                textAr = textAr,
                                translationEn = textEn,
                                juz = juz
                            )
                        )
                    }
                    if (result.isNotEmpty()) {
                        cache[surahNumber] = result
                        return@withContext result
                    }
                }
            } catch (e: Exception) {
                Log.e("QuranRepo", "Network error fetching surah $surahNumber: ${e.message}")
            }

            // Fallback for surah if network is unavailable
            val surahMeta = allSurahs.firstOrNull { it.number == surahNumber }
            val fallback = List(surahMeta?.totalVerses ?: 1) { i ->
                Ayah(
                    surahNumber = surahNumber,
                    ayahNumber = i + 1,
                    textAr = if (surahNumber != 9 && i == 0) "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" else "آيَةٌ مِن سُورَةِ ${surahMeta?.nameAr ?: ""}",
                    translationEn = "Verse ${i + 1} of ${surahMeta?.nameEn ?: "Surah"}. Please connect to internet to download full text.",
                    juz = surahMeta?.juzStart ?: 1
                )
            }
            cache[surahNumber] = fallback
            fallback
        }
    }
}
