package com.example.projectprotel.ui.search

import androidx.lifecycle.ViewModel
import com.example.projectprotel.Data.PlantsEntity
import com.example.projectprotel.utils.dataDummy

class SearchViewModel : ViewModel() {

    fun getPlants(): List<PlantsEntity> = dataDummy.generateDummyPlants()
}