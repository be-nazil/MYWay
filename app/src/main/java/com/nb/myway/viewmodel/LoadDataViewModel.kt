package com.nb.myway.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.nb.myway.repository.LoadDataRepository
import com.nb.myway.ui.model.DirectionModel
import com.nb.myway.ui.model.MapData
import com.nb.myway.ui.model.Route
import com.nb.myway.ui.model.RoutesModelItem
import com.nb.myway.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class LoadDataViewModel(private val loadDataRepository: LoadDataRepository): ViewModel() {

    private val TAG = "LoadDataViewModel"

    private var routesModelItemMutable : MutableLiveData<List<RoutesModelItem>> = MutableLiveData()

    val routesModelItemLiveData: LiveData<List<RoutesModelItem>>
        get() {
            return routesModelItemMutable
        }

    private var lineOptionsMutable: MutableLiveData<PolylineOptions> = MutableLiveData()
    val lineOptionsLiveData: LiveData<PolylineOptions>
        get() {
            return lineOptionsMutable
        }

    private var listPlace: MutableLiveData<List<String>> = MutableLiveData()
    val listPlaces: LiveData<List<String>>
        get() {
            return listPlace
        }


    fun getDirection(directionModel: DirectionModel){
        viewModelScope.launch(Dispatchers.IO)  {
            val data= loadDataRepository.getDirection(directionModel)
            val result = ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data.string(), MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(LocationUtils.decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
                val lineoption = PolylineOptions()
                for (i in result.indices){
                    lineoption.addAll(result[i])
                    lineoption.width(10f)
                    lineoption.color(Color.GREEN)
                    lineoption.geodesic(true)
                }

                lineOptionsMutable.postValue(lineoption)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }


    }


    public fun readFile(json: String){
        val routesModelItemList = mutableListOf<RoutesModelItem>()
        try {
            var stringList: ArrayList<String> = ArrayList()
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val routesList = mutableListOf<Route>()
                val jsonObject = jsonArray.getJSONObject(i) as JSONObject
                val jsonArray1 = jsonObject.getJSONArray("routes")
                Log.i(
                    "GetJsonData",
                    "getData: lenght - " + jsonArray1.length()
                )
                for (j in 0 until jsonArray1.length()) {

                    val sourceTitle = jsonArray1.getJSONObject(j).getString("sourceTitle");
                    val destTitle = jsonArray1.getJSONObject(j)["destinationTitle"].toString()
                    val route = Route(
                            sourceLat = jsonArray1.getJSONObject(j)["sourceLat"] as Double,
                            sourceLong = jsonArray1.getJSONObject(j)["sourceLong"] as Double,
                            busRouteDetails= (jsonArray1.getJSONObject(j).getString("busRouteDetails")),
                            destinationLat=jsonArray1.getJSONObject(j)["destinationLat"] as Double,
                            destinationLong=jsonArray1.getJSONObject(j)["destinationLong"] as Double,
                            destinationTime=jsonArray1.getJSONObject(j)["destinationTime"].toString(),
                            destinationTitle=destTitle ,
                            distance=jsonArray1.getJSONObject(j)["distance"] as Double,
                            duration=jsonArray1.getJSONObject(j)["duration"].toString(),
                            fare=jsonArray1.getJSONObject(j)["fare"] as Double,
                            medium=jsonArray1.getJSONObject(j)["medium"].toString(),
                            rideEstimation= (jsonArray1.getJSONObject(i).getString("rideEstimation")),
                            routeName=jsonArray1.getJSONObject(j)["routeName"].toString(),
                            sourceTime=jsonArray1.getJSONObject(j)["sourceTime"].toString(),
                            sourceTitle= sourceTitle,
                            trails=(jsonArray1.getJSONObject(j).getString("trails"))
                    )
                    routesList.add(route)
                    stringList.add(sourceTitle)
                }

                val routesModelItem = RoutesModelItem(
                        routeTitle=jsonObject.getString("routeTitle"),
                        routes=routesList,
                        totalDistance=jsonObject.getDouble("totalDistance"),
                        totalDuration=jsonObject.getString("totalDuration"),
                        totalFare=jsonObject.getDouble("totalFare")
                )

                routesModelItemList.add(routesModelItem)
            }

            routesModelItemMutable.postValue(routesModelItemList)
            listPlace.postValue(stringList)

        }catch (e: IOException){
            Log.e(TAG, "readFile: ${e.message}")
        }


    }

}