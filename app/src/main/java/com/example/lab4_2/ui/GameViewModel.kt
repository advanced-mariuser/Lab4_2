package com.example.lab4_2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.models.Building
import com.example.app.models.BuildingType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel()
{
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    init
    {
        initializeBuildings()
        startCookieProduction()
        startTimer()
    }

    private fun startCookieProduction()
    {
        viewModelScope.launch {
            while (true)
            {
                delay(1000)
                _gameState.update {
                    val newCount = it.cookieCount + it.cookiesPerSecond
                    val showToast = newCount >= it.nextToastCookieCount
                    it.copy(
                        cookieCount = newCount,
                        nextToastCookieCount = if (showToast) it.nextToastCookieCount + 100 else it.nextToastCookieCount
                    )
                }
                if (_gameState.value.cookieCount >= _gameState.value.nextToastCookieCount)
                {
                    _toastMessage.emit("Вы собрали ${_gameState.value.cookieCount.toInt()} печенек!")
                }
            }
        }
    }

    fun onCookieClicked()
    {
        _gameState.update {
            it.copy(cookieCount = it.cookieCount + 1.0)
        }
    }

    private fun initializeBuildings()
    {
        _gameState.value = _gameState.value.copy(
            buildings = listOf(
                Building("Клик", BuildingType.CLICKER, 0, 15, 0.1, false),
                Building("Бабуля", BuildingType.GRANDMA, 0, 100, 1.0, false),
                Building("Ферма", BuildingType.FARM, 0, 1100, 8.0, false),
                Building("Шахта", BuildingType.MINE, 0, 12000, 47.0, false),
                Building("Фабрика", BuildingType.FABRIC, 0, 130000, 260.0, false),
                Building("Банк", BuildingType.BANK, 0, 1400000, 1400.0, false),
                Building("Храм", BuildingType.TEMPLE, 0, 20000000, 7800.0, false),
                Building(
                    "Башня волшебника",
                    BuildingType.WIZARD_TOWER,
                    0,
                    330000000,
                    44000.0,
                    false
                ),
                Building("Космический корабль", BuildingType.ROCKET, 0, 510000000, 260000.0, false)

            )
        )
    }

    fun buyBuilding(building: Building)
    {
        val currentPrice = calculatePrice(building.basePrice, building.count)
        if (_gameState.value.cookieCount >= currentPrice) {
            val updatedBuildings = _gameState.value.buildings.map {
                if (it.name == building.name) {
                    it.copy(count = it.count + 1) // Увеличиваем количество зданий
                } else it
            }

            val additionalIncome = calculateIncome(building.income, building.count + 1)
            _gameState.value = _gameState.value.copy(
                cookieCount = _gameState.value.cookieCount - currentPrice,
                buildings = updatedBuildings,
                cookiesPerSecond = _gameState.value.cookiesPerSecond + additionalIncome
            )

            viewModelScope.launch {
                _toastMessage.emit("Вы купили ${building.name} за $currentPrice печенек!")
            }
        } else {
            viewModelScope.launch {
                _toastMessage.emit("Недостаточно печенек для покупки ${building.name}.")
            }
        }
    }

    fun sellBuilding(building: Building) {
        if (building.count > 0) {
            val updatedBuildings = _gameState.value.buildings.map {
                if (it.name == building.name) {
                    it.copy(
                        count = it.count - 1,
                        basePrice = (it.basePrice / 1.15).toInt()
                    )
                } else it
            }
            _gameState.value = _gameState.value.copy(
                cookieCount = _gameState.value.cookieCount + building.basePrice,
                buildings = updatedBuildings,
                cookiesPerSecond = _gameState.value.cookiesPerSecond - building.income
            )

            viewModelScope.launch {
                _toastMessage.emit("Вы продали ${building.name} за ${building.basePrice} печенек!")
            }
        }
    }

    private fun startTimer()
    {
        viewModelScope.launch {
            var seconds = 0
            while (true)
            {
                delay(1000)
                seconds++
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                _gameState.update {
                    it.copy(elapsedTime = String.format("%d:%02d", minutes, remainingSeconds))
                }
            }
        }
    }

    private fun calculatePrice(basePrice: Int, count: Int): Int
    {
        return (basePrice * Math.pow(1.15, count.toDouble())).toInt()
    }

    private fun calculateIncome(baseIncome: Double, count: Int): Double {
        return baseIncome * (1 + 0.15 * (count - 1))
    }
}