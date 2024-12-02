package com.example.app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.models.Building
import com.example.app.models.BuildingType
import com.example.lab4_2.R
import com.example.lab4_2.databinding.ItemBuildingBinding

class BuildingsAdapter(
    private val onBuildingClick: (Building) -> Unit,
    private val onSellClick: (Building) -> Unit
) : RecyclerView.Adapter<BuildingsAdapter.BuildingViewHolder>()
{
    //Список зданий
    private var buildingsList: List<Building> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder
    {
        val binding = ItemBuildingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BuildingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int)
    {
        val building = buildingsList[position]
        holder.bind(building)
    }

    override fun getItemCount(): Int = buildingsList.size

    //Обновляем список зданий и сообщаем адаптеру, что данные изменились, чтобы перерисовать список
    fun submitList(buildings: List<Building>)
    {
        buildingsList = buildings
        notifyDataSetChanged()
    }

    inner class BuildingViewHolder(private val binding: ItemBuildingBinding) :
        RecyclerView.ViewHolder(binding.root)
    {

        fun bind(building: Building) {
            val currentIncome = calculateIncome(building.income, building.count + 1)
            val currentPrice = calculatePrice(building.basePrice, building.count) // Рассчитываем текущую цену
            binding.name.text = building.name
            binding.count.text = building.count.toString()
            binding.cost.text = "$currentPrice 🍪"
            binding.income.text = "+$currentIncome 🍪/сек"
            binding.icon.setImageResource(getIconForBuildingType(building.type))
            binding.root.alpha = if (building.isAvailable) 1.0f else 0.5f
            binding.root.isClickable = building.isAvailable
            binding.root.setOnClickListener { onBuildingClick(building) }
            binding.sellButton.setOnClickListener { onSellClick(building) }
        }
    }

    private fun getIconForBuildingType(type: BuildingType): Int
    {
        return when (type)
        {
            BuildingType.CLICKER -> R.drawable.ic_clicker
            BuildingType.GRANDMA -> R.drawable.ic_grandma
            BuildingType.FARM -> R.drawable.ic_farm
            BuildingType.MINE -> R.drawable.ic_mine
            BuildingType.FABRIC -> R.drawable.ic_fabric
            BuildingType.BANK -> R.drawable.ic_bank
            BuildingType.TEMPLE -> R.drawable.ic_temple
            BuildingType.WIZARD_TOWER -> R.drawable.ic_wizard_tower
            BuildingType.ROCKET -> R.drawable.ic_rocket
        }
    }

    private fun calculatePrice(basePrice: Int, count: Int): Int {
        return (basePrice * Math.pow(1.15, count.toDouble())).toInt()
    }

    private fun calculateIncome(baseIncome: Double, count: Int): Double {
        return baseIncome * (1 + 0.15 * (count - 1))
    }

}