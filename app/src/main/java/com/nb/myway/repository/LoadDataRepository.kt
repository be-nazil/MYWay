package com.nb.myway.repository

import android.content.Context
import com.nb.myway.repository.network.RetrofitClient
import com.nb.myway.ui.model.DirectionModel
import okhttp3.ResponseBody
import retrofit2.http.Query

class LoadDataRepository {

    private val apiInterface = RetrofitClient.buildApi()

    suspend fun getDirection(directionModel: DirectionModel): ResponseBody {
        return  apiInterface.getDirection(
            "${directionModel.origin.latitude},${directionModel.origin.longitude}",
            "${directionModel.destination.latitude},${directionModel.destination.longitude}",
            directionModel.sensor,
            directionModel.mode,
            directionModel.key
        )

    }

}