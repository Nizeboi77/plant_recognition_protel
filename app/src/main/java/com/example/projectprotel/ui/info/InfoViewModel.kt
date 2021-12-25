package com.example.projectprotel.ui.info

import android.app.SearchManager
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.projectprotel.Data.PlantsEntity
import com.example.projectprotel.utils.dataDummy

class InfoViewModel : ViewModel() {

    private lateinit var plantsId: String
    private lateinit var plantsId2: String
    private lateinit var plantsId3: String

    fun setSelectedplant(plantsId: String){
        this.plantsId = plantsId
    }

    fun setSelectedplants(plantsId: String, plantsId2: String, plantsId3: String){
        this.plantsId = plantsId
        this.plantsId2 = plantsId2
        this.plantsId3 = plantsId3
    }

    fun getPlants(): PlantsEntity? {


        var plant: PlantsEntity? =null
        val plantsEntity = dataDummy.generateDummyPlants()
        for (plantsEntity in plantsEntity) {
            if (plantsEntity.kategorisatu == plantsId && plantsEntity.kategoridua == plantsId2 && plantsEntity.kategoritiga == plantsId3) {
                plant = plantsEntity
            }
           else {

               plantsEntity.kategorisatu = plantsEntity.kategorisatu
                plantsEntity.kategoridua = plantsEntity.kategoridua
                plantsEntity.kategoritiga = plantsEntity.kategoritiga
//               webintent(InfoActivity(),plantsId)
//                //val intent = Intent(Intent.ACTION_WEB_SEARCH)
//                //intent.putExtra(SearchManager.QUERY, plantsId)
//                //AndroidViewModel.startActivity(intent)
//                //startActivity(intent)
            }
        }
        return plant

    }

    fun webintent(activity: InfoActivity, plantsId: String){
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, plantsId)
        //AndroidViewModel.startActivity(intent)
        activity.startActivity(intent)
    }

    fun getPlant(): PlantsEntity? {
        var plant: PlantsEntity? =null
        val plantsEntity = dataDummy.generateDummyPlants()
        for (plantsEntity in plantsEntity) {
            if (plantsEntity.name == plantsId) {
                plant = plantsEntity
            }
//            else {
//                val intent = Intent(Intent.ACTION_WEB_SEARCH)
//                intent.putExtra(SearchManager.QUERY, plantsId)
//                startActivity(intent)
//            }
        }
        return plant

    }

}