package com.nb.myway.ui.activities;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.directions.route.Route
import com.directions.route.RouteException
import com.directions.route.RoutingListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.nb.myway.R
import com.nb.myway.repository.LoadDataRepository
import com.nb.myway.ui.adapter.RoutesAdapter
import com.nb.myway.ui.model.DirectionModel
import com.nb.myway.utils.toast
import com.nb.myway.viewmodel.LoadDataViewModel
import com.nb.myway.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream


class MainActivity : FragmentActivity(), OnMapReadyCallback,
    GoogleApiClient.OnConnectionFailedListener,
    RoutingListener {
    var locationPermission = false
    private val TAG = MainActivity.javaClass.simpleName

    lateinit var viewModel: LoadDataViewModel

    lateinit var mMap: GoogleMap
    var myLocation: Location? = null
    protected var start: LatLng? = null

    //polyline object
    private var polylines: MutableList<Polyline>? = null
    lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //request location permission.
        requestPermision()

        innitView()
    }

    private fun setData(position: Int) {
        try {
            val inputStream: InputStream = assets.open("response.json")

            val loadDataRepository = LoadDataRepository()
            viewModel = ViewModelProvider(
                this, ViewModelFactory(loadDataRepository)
            ).get(LoadDataViewModel::class.java)

            val json = inputStream.bufferedReader().use { it.readText() }
            viewModel.readFile(json)


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mMap.isMyLocationEnabled= false
            mMap.clear()


            val routesAdapter = RoutesAdapter()

            viewModel.routesModelItemLiveData.observe(this, Observer { it ->

                val originLocation = LatLng(it[position].routes[0].sourceLat, it[position].routes[0].sourceLong)
                val destinationLocation = LatLng(it[position].routes[0].destinationLat, it[position].routes[0].destinationLong)
                getLocation(originLocation, destinationLocation,14F)


                /**
                * Setting data to Adapter
                * */
                routesAdapter.setData(it[position].routes)
                recycler_view_route.adapter = routesAdapter
                routesAdapter.onItemClickListener = { view, position,item ->
                    animateView(view)
                    getLocation(LatLng(item.sourceLat, item.sourceLong),
                            LatLng(item.destinationLat, item.destinationLong), 20F)
                }

            })

        }catch (e: IOException){

        }
    }

    private fun innitView() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_map_api_key))
        }

        //init google map fragment to show map.
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getMyLocation()

        r_layout.setOnClickListener {
            animateView(it)
            val intent = Intent(this, SearchPlaceActivity::class.java)
            startActivityForResult(intent, 1)
        }

    }

    private fun getLocation(originLocation: LatLng, destinationLocation: LatLng,zoom: Float){
         mapFragment.getMapAsync {
                 mMap = it
                 mMap.addMarker(MarkerOptions().position(originLocation))
                 mMap.addMarker(MarkerOptions().position(destinationLocation))

                val directionModel = DirectionModel(
                        originLocation,
                        destinationLocation,
                        "false",
                        "driving",
                        resources.getString(R.string.google_map_api_key)
                )
                viewModel.getDirection(directionModel)
                viewModel.lineOptionsLiveData.observe(this, Observer {lineoption->
                    mMap.addPolyline(lineoption)
                })

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, zoom))
         }

    }

    //to get user location
    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mapFragment.getMapAsync {
            mMap = it
            mMap.isMyLocationEnabled= true
            mMap.setOnMyLocationChangeListener { location ->
                myLocation = location
                val ltlng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(ltlng))
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f
                )
                mMap.animateCamera(cameraUpdate)
            }

        }


    }

    private fun animateView(view: View){
        view.animate().apply {
            duration = 100
            translationXBy(40f)
        }.withEndAction {
            view.animate().apply {
                duration = 100
                translationXBy(-40f)
            }.start()
        }
    }


    private fun requestPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            locationPermission = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermission = true
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 23
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        Log.d(TAG, "Added Markers");
    }

    //Routing call back functions.
    override fun onRoutingFailure(e: RouteException) {
        val parentLayout: View = findViewById(R.id.content_layout)
        val snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG)
        snackbar.show()
        Log.d(TAG, "onRoutingFailure: ${e.toString()}")
    }

    override fun onRoutingStart() {
        toast("Finding Route...")
    }

    //If Route finding success..
    override fun onRoutingSuccess(route: ArrayList<Route>, shortestRouteIndex: Int) {
        val center = CameraUpdateFactory.newLatLng(start!!)
        val zoom = CameraUpdateFactory.zoomTo(16f)
        if (polylines != null) {
            polylines!!.clear()
        }
        val polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null
        polylines = ArrayList()
        //add route(s) to the map using polyline
        for (i in 0 until route.size) {
            if (i == shortestRouteIndex) {
                polyOptions.color(resources.getColor(R.color.purple_700))
                polyOptions.width(7f)
                polyOptions.addAll(route[shortestRouteIndex].points)
                val polyline = mMap.addPolyline(polyOptions)
                polylineStartLatLng = polyline.points[0]
                val k = polyline.points.size
                polylineEndLatLng = polyline.points[k - 1]
                (polylines as ArrayList<Polyline>).add(polyline)
            } else {
            }
        }

        //Add Marker on route starting position
        val startMarker = MarkerOptions()
        startMarker.position(polylineStartLatLng!!)
        startMarker.title("My Location")
        mMap.addMarker(startMarker)

        //Add Marker on route ending position
        val endMarker = MarkerOptions()
        endMarker.position(polylineEndLatLng!!)
        endMarker.title("Destination")
        mMap.addMarker(endMarker)
    }

    override fun onRoutingCancelled() {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val position = data?.getIntExtra("position",0)
            position?.let {
                setData(position)
            }
        }
    }


}