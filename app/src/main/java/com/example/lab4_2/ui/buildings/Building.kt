package com.example.app.models

data class Building(
    val name: String,
    val type: BuildingType,
    val count: Int,        // Количество построенных зданий
    val basePrice: Int,    // Базовая цена
    val income: Double,    // Доход в печеньках
    val isAvailable: Boolean,
)

enum class BuildingType
{
    CLICKER, GRANDMA, FARM, MINE, FABRIC, BANK, TEMPLE, WIZARD_TOWER, ROCKET
}