package com.nb.myway.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nb.myway.R
import com.nb.myway.ui.model.Route
import kotlinx.android.synthetic.main.layout_destination_item.view.*
import kotlinx.android.synthetic.main.layout_item.view.*
import kotlinx.android.synthetic.main.layout_source_item.view.*
import kotlinx.android.synthetic.main.layout_source_item.view.tv_distance
import kotlinx.android.synthetic.main.layout_source_item.view.tv_source
import kotlinx.android.synthetic.main.layout_source_item.view.tv_time


class RoutesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private var TYPE_FOOTER = 1
    private val TYPE_ITEM = 2

    private var type: Int = 0

    private lateinit var routesList: List<Route>
    var onItemClickListener: ((View, Int, Route) -> Unit)? = null


    private inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tviewSource: TextView? = null
        var tviewTime: TextView?= null
        var tviewDistance: TextView?= null

        init {
            tviewSource = view.tv_source
            tviewTime = view.tv_time
            tviewDistance = view.tv_distance

            view.setOnClickListener {
                onItemClickListener?.invoke(it,adapterPosition,routesList[adapterPosition])
            }
        }
    }

    private inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tviewDestination: TextView? = null
        init {
            tviewDestination = view.tv_destination
            view.setOnClickListener {
                onItemClickListener?.invoke(it,adapterPosition,routesList[adapterPosition])
            }
        }
    }

    private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tviewSource: TextView? = null
        var tviewTime: TextView?= null
        var tviewDistance: TextView?= null
        var tviewFare: TextView?= null

        init {
            tviewSource = view.tv_source
            tviewTime = view.tv_time
            tviewDistance = view.tv_distance
            tviewFare = view.tv_fare
            view.setOnClickListener {
                onItemClickListener?.invoke(it,adapterPosition,routesList[adapterPosition])
            }
        }
    }


    fun setData(routesList: List<Route>){
        this.routesList = routesList
        TYPE_FOOTER = routesList.lastIndex
        type = routesList.lastIndex
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        when (parent.childCount) {
            TYPE_HEADER -> {
                itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_source_item, parent, false)
                return HeaderViewHolder(itemView)
            }
            TYPE_FOOTER -> {
                itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_destination_item, parent, false)
                return FooterViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item, parent, false)
                return ItemViewHolder(itemView)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = routesList[position]
        when (holder) {
            is HeaderViewHolder -> {
                holder.tviewSource?.text = "${currentItem.sourceTitle}"
                holder.tviewDistance?.text = currentItem.distance.toString()
                holder.tviewTime?.text = currentItem.duration
            }
            is FooterViewHolder -> {
                holder.tviewDestination?.text = "${currentItem.sourceTitle}"
            }
            is ItemViewHolder -> {
                holder.tviewSource?.text = "${currentItem.sourceTitle} To ${currentItem.destinationTitle}"
                holder.tviewDistance?.text = currentItem.distance.toString()
                holder.tviewTime?.text = currentItem.duration
                holder.tviewFare?.text = currentItem.fare.toString()
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    override fun getItemCount(): Int {
        return routesList.size
    }
}