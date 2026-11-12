# 🌟 Noor – Your Daily Islamic Companion

<p align="center">
  <img src="https://raw.githubusercontent.com/google/material-design-icons/master/png/action/brightness_auto/materialicons/48dp/2x/baseline_brightness_auto_black_48dp.png" width="96" height="96" alt="Noor Logo" />
</p>

<p align="center">
  <strong>A peaceful, modern, and authentic Islamic mobile app for Android</strong><br>
  Built with Kotlin, Jetpack Compose, Material 3, and Room Offline Caching.
</p>

<p align="center">
  <a href="https://github.com/masudrana01677/noor-islamic-companion/raw/main/release/noor-v1.0.0.apk">
    <img src="https://img.shields.io/badge/⬇️_Direct_Download-APK_(v1.0.0)-0B3B2C?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
  </a>
  <a href="https://github.com/masudrana01677/noor-islamic-companion/releases/latest">
    <img src="https://img.shields.io/badge/📦_GitHub_Releases-Latest_Build-D4AF37?style=for-the-badge&logo=github&logoColor=black" alt="GitHub Releases" />
  </a>
</p>

---

## 📥 Downloadable APK Links

### 🔗 Option 1: Direct Raw Download (Instant 1-Click)
Once pushed to your GitHub repository, this link starts an immediate download on any browser or Android phone:

```text
https://github.com/masudrana01677/noor-islamic-companion/raw/main/release/noor-v1.0.0.apk
```
> **Alternative Raw Link:**
> ```text
> https://raw.githubusercontent.com/masudrana01677/noor-islamic-companion/main/release/noor-v1.0.0.apk
> ```
*(If your GitHub username or repository name is different, replace `masudrana01677` and `noor-islamic-companion` with your actual username and repo name).*

---

### 📦 Option 2: GitHub Releases Download
If you create a GitHub Release or use the included GitHub Actions workflow, the release asset is permanently available at:

```text
https://github.com/masudrana01677/noor-islamic-companion/releases/latest/download/noor-v1.0.0.apk
```

---

## 📱 How to Push from AI Studio to GitHub

1. In AI Studio, look at the top-right toolbar or the settings menu.
2. Click **"Push to GitHub"** (or connect your GitHub account `masudrana01677`).
3. Choose your repository name (e.g., `noor-islamic-companion`).
4. Once pushed, the folder `release/noor-v1.0.0.apk` will automatically be published to GitHub.
5. Anyone can immediately click the download URL above to download and install the app on their phone!

---

## 📲 How to Install the APK on Android

1. **Download:** Click the download link above on your Android phone's browser (Chrome, Samsung Internet, Firefox, etc.).
2. **Open APK:** When the download completes, tap the notification or open the downloaded file from your `Downloads` folder.
3. **Allow Installation:** If prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings** and toggle **"Allow from this source"**.
4. **Install:** Tap **Install** and enjoy your daily Islamic companion!

---

## ✨ Features

- 📖 **Al-Quran Recitation with Bengali Pronunciation (বাংলা উচ্চারণ):**
  - Full Quran with beautiful Arabic typography.
  - Bengali Meaning (বাংলা অর্থ) & English Translation.
  - Complete Bengali phonetic pronunciation (উচ্চারণ) for ease of reciting.
  - Audio streaming with play/pause and verse tracking.
- 💾 **Offline Room Database Caching:**
  - Surahs and Ayahs are saved locally to SQLite Room DB once loaded.
  - Access your favorite Surahs seamlessly without internet connectivity.
- 🕌 **Accurate Prayer Times & Countdown:**
  - Fajr, Sunrise, Dhuhr, Asr, Maghrib, and Isha calculated accurately based on coordinates.
  - Live animated countdown to the next prayer with remaining time progress.
- 🧭 **Real-time Qibla Compass:**
  - Sensor-fused magnetic compass pointing directly towards Kaaba in Makkah.
- 📜 **Daily Hadith with Refresh:**
  - 16 verified, authentic Hadiths from Sahih Bukhari, Muslim, and Tirmidhi.
  - Instant refresh button to cycle through insightful Hadiths with Bengali and English reflections.
- 🌙 **Daily Fasting / Ramadan Schedule:**
  - Live Sehri/Imsak end time and Iftar time countdown.
- 📿 **Tasbih Counter:**
  - Digital interactive Dhikr counter with haptic feedback and target presets.
- 🎨 **Deep Emerald & Muted Gold Theme:**
  - Elegant, soothing Islamic visual design with full Light & Dark mode support.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 100%
- **UI Framework:** Jetpack Compose (Material 3)
- **Local Persistence:** Room Database (`AppDatabase`, `NoorDao`, `CachedAyahEntity`)
- **Architecture:** MVVM with `NoorViewModel`, Kotlin Coroutines & `StateFlow`
- **Sensors:** Android `SensorManager` (Accelerometer & Magnetometer fusion)
- **Audio:** Android `MediaPlayer` with stateful playback controller
- **CI/CD:** GitHub Actions (`.github/workflows/release.yml`) for automated APK releases

---

## 📄 License & Attribution
Developed with devotion as a free and open Islamic resource.
May Allah accept our humble efforts.
