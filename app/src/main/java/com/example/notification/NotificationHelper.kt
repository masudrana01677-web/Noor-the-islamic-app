package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.calculator.PrayerSchedule
import com.example.data.local.UserSettings
import com.example.data.repository.HadithRepository
import java.util.Calendar

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"
        val prayerArabic = intent.getStringExtra(EXTRA_PRAYER_ARABIC) ?: ""
        val isHadith = intent.getBooleanExtra(EXTRA_IS_HADITH, false)

        if (isHadith) {
            val hadith = HadithRepository.getDailyHadith()
            NotificationHelper.showHadithNotification(context, hadith.textEn, hadith.book)
        } else {
            NotificationHelper.showPrayerNotification(context, prayerName, prayerArabic)
        }
    }

    companion object {
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_ARABIC = "extra_prayer_arabic"
        const val EXTRA_IS_HADITH = "extra_is_hadith"
    }
}

object NotificationHelper {

    private const val CHANNEL_PRAYER_ID = "noor_prayer_times"
    private const val CHANNEL_HADITH_ID = "noor_daily_hadith"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ID,
                "Prayer Times & Adhan",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications and Adhan calls at prayer times"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(defaultSound, audioAttributes)
            }

            val hadithChannel = NotificationChannel(
                CHANNEL_HADITH_ID,
                "Daily Hadith & Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily inspirational Islamic hadith"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(hadithChannel)
        }
    }

    fun showPrayerNotification(context: Context, prayerNameEn: String, prayerNameAr: String) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYER_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time for $prayerNameEn ($prayerNameAr)")
            .setContentText("Hayya 'ala-s-Salah — Come to prayer, come to success.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = prayerNameEn.hashCode()
        notificationManager.notify(id, notification)
    }

    fun showHadithNotification(context: Context, hadithText: String, bookSource: String) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HADITH_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Daily Hadith Reflection")
            .setContentText(hadithText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("\"$hadithText\"\n\n— $bookSource"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(9999, notification)
    }

    fun schedulePrayerAlarms(context: Context, schedule: PrayerSchedule, settings: UserSettings) {
        if (!settings.notificationsEnabled) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val prayersToSchedule = listOf(
            Triple("Fajr", "الفجر", schedule.fajr) to settings.fajrNotification,
            Triple("Dhuhr", "الظهر", schedule.dhuhr) to settings.dhuhrNotification,
            Triple("Asr", "العصر", schedule.asr) to settings.asrNotification,
            Triple("Maghrib", "المغرب", schedule.maghrib) to settings.maghribNotification,
            Triple("Isha", "العشاء", schedule.isha) to settings.ishaNotification
        )

        val now = System.currentTimeMillis()
        prayersToSchedule.forEachIndexed { index, (prayerData, isEnabled) ->
            val (nameEn, nameAr, cal) = prayerData
            if (!isEnabled) return@forEachIndexed

            val triggerTime = cal.timeInMillis - (settings.notifyMinutesBefore * 60 * 1000)
            if (triggerTime > now) {
                val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, nameEn)
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ARABIC, nameAr)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    100 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    }
                } catch (e: SecurityException) {
                    // Exact alarm permission not granted or restricted by OEM
                }
            }
        }

        // Daily Hadith schedule
        if (settings.dailyHadithNotification) {
            val hadithCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, settings.dailyHadithHour)
                set(Calendar.MINUTE, settings.dailyHadithMinute)
                set(Calendar.SECOND, 0)
                if (timeInMillis <= now) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val hadithIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra(PrayerAlarmReceiver.EXTRA_IS_HADITH, true)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                999,
                hadithIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, hadithCal.timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, hadithCal.timeInMillis, pendingIntent)
                }
            } catch (e: SecurityException) {
            }
        }
    }
}
