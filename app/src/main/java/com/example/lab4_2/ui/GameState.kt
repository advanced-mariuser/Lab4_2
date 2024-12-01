package com.example.lab4_2.ui

import com.example.app.models.Building

data class GameState(
    val cookieCount: Double = 0.0,
    val cookiesPerSecond: Double = 0.0,
    val buildings: List<Building> = emptyList(),
    val averageSpeed: Double = 0.0,
    val elapsedTime: String = "0:00",
    val nextToastCookieCount: Double = 100.0 // Тост каждые 100 печенек
)