package com.nb.myway.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nb.myway.R
import com.nb.myway.repository.LoadDataRepository
import com.nb.myway.ui.adapter.RecyclerAdapter
import com.nb.myway.viewmodel.LoadDataViewModel
import com.nb.myway.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_search_place.*
import java.io.IOException
import java.io.InputStream


class SearchPlaceActivity: AppCompatActivity() {

    lateinit var viewModel: LoadDataViewModel
    private val TAG = "SearchPlaceActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_place)

        innitView()

    }


    private fun innitView() {

        try {
            val inputStream: InputStream = assets.open("response.json")

            val loadDataRepository = LoadDataRepository()
            viewModel = ViewModelProvider(
                this,
                ViewModelFactory(loadDataRepository)
            ).get(LoadDataViewModel::class.java)

            val json = inputStream.bufferedReader().use { it.readText() }
            viewModel.readFile(json)
        }catch (e: IOException){
            Log.d(TAG, "innitView: ${e.message}")
        }

        // Create adapter and add in AutoCompleteTextView

        val recAdapter = RecyclerAdapter(this)
        viewModel.routesModelItemLiveData.observe(this, Observer {
            recycler_view_route.adapter = recAdapter
            recAdapter.setData(it)
            recAdapter.onItemClick = { view, position,item ->

                Log.e(TAG, "onCreate: ${item.routeTitle}")
                Toast.makeText(this, "${item.totalDistance}", Toast.LENGTH_SHORT).show()
                animateView(view).also {
                    sendBackData(position)
                }
            }
        })

        viewModel.listPlaces.observe(this, Observer {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, it)
            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.setOnItemClickListener { _, _, i, _ ->
                editText_dest.setText(it[i])
            }
        })

    }

    private fun sendBackData(position: Int) {

        val resultIntent = Intent()
        resultIntent.putExtra("position", position)
        setResult(RESULT_OK, resultIntent)
        finish()
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

}