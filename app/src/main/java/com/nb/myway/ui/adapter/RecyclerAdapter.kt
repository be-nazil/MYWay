package com.nb.myway.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nb.myway.R
import com.nb.myway.ui.model.Route
import com.nb.myway.ui.model.RoutesModelItem
import kotlinx.android.synthetic.main.layout_route_item.view.*
import kotlinx.android.synthetic.main.layout_sub_route_item.view.*
import java.util.ArrayList


class RecyclerAdapter(val context: Context) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>()  {


    private lateinit var routesModelItemList: List<RoutesModelItem>

    var onItemClick: ((View, Int, RoutesModelItem) -> Unit)? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tv_schedule: TextView = view.tv_schedule
        val tv_fare: TextView = view.tv_fare
        val tv_duration: TextView = view.tv_duration
        val tv_distance: TextView = view.tv_distance

        val recyclerView: RecyclerView = view.recyclerView

        init {
            view.setOnClickListener {
                onItemClick?.invoke(it,adapterPosition,routesModelItemList[adapterPosition])
            }
        }

    }

    fun setData(routesModelItem: List<RoutesModelItem>){
        this.routesModelItemList = routesModelItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_route_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = routesModelItemList[position]

        holder.tv_schedule.text = currentItem.routeTitle
        holder.tv_distance.text = "${currentItem.totalDistance} Kms"
        holder.tv_duration.text = currentItem.totalDuration
        holder.tv_fare.text = currentItem.totalFare.toString()

        holder.recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        val innerAdapter = InnerAdapter()
        holder.recyclerView.adapter = innerAdapter
        Log.i("Adapterrr", "onBindViewHolder:\n" +
            " Routes of ${currentItem.routeTitle} =  ${currentItem.routes.size}" +
                "\n ${currentItem.routes[position].medium}")
        innerAdapter.setInnerData(currentItem.routes)
    }

    override fun getItemCount(): Int {
        return routesModelItemList.size
    }

    var count = 0

    inner class InnerAdapter : RecyclerView.Adapter<InnerAdapter.SubViewHolder>() {

        var mediumList: List<Route> = ArrayList<Route>()

        inner class SubViewHolder(view: View): RecyclerView.ViewHolder(view){
            val imgView = view.image_view_medium
            val view = view.view_line
        }

        fun setInnerData(list : List<Route>){
            if (this.mediumList.isNotEmpty()){
                this.mediumList = emptyList()
            }
            this.mediumList = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_sub_route_item, parent, false)

            return SubViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: SubViewHolder, position: Int) {
            count = count.plus(1)
            Log.i("InnerAdapter", "onBindViewHolder ${count}: ${mediumList[position].medium}")
            if (mediumList[position].medium == "Bus") {
                holder.imgView.setImageResource(R.drawable.ic_directions_bus)
                holder.view.setBackgroundResource(R.color.yellow_dark)
            }else{
                holder.imgView.setImageResource(R.drawable.ic_directions_walk)
            }
        }

        override fun getItemCount(): Int {
            return mediumList.size?:0
        }


    }


}