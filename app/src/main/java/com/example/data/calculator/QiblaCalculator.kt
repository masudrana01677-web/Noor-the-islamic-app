package com.example.data.calculator

import kotlin.math.*

object QiblaCalculator {
    const val KAABA_LATITUDE = 21.422524
    const val KAABA_LONGITUDE = 39.826182
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the Qibla bearing in degrees (0-360) clockwise from True North.
     */
    fun calculateBearing(userLatitude: Double, userLongitude: Double): Double {
        val lat1 = Math.toRadians(userLatitude)
        val lon1 = Math.toRadians(userLongitude)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val lon2 = Math.toRadians(KAABA_LONGITUDE)

        val dLon = lon2 - lon1

        val y = sin(dLon)
        val x = cos(lat1) * tan(lat2) - sin(lat1) * cos(dLon)

        val initialBearing = Math.toDegrees(atan2(y, x))
        return (initialBearing + 360.0) % 360.0
    }

    /**
     * Calculates the great-circle distance to the Kaaba in kilometers.
     */
    fun calculateDistanceKm(userLatitude: Double, userLongitude: Double): Double {
        val lat1 = Math.toRadians(userLatitude)
        val lon1 = Math.toRadians(userLongitude)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val lon2 = Math.toRadians(KAABA_LONGITUDE)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_KM * c
    }

    /**
     * Checks if current compass heading points to Qibla within tolerance (degrees).
     */
    fun isAligned(compassHeading: Float, qiblaBearing: Double, tolerance: Double = 4.0): Boolean {
        val diff = abs(compassHeading - qiblaBearing)
        val shortestAngle = min(diff, 360.0 - diff)
        return shortestAngle <= tolerance
    }
}
