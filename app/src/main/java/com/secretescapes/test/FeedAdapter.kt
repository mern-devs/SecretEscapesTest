package com.secretescapes.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeedAdapter(private val context: Context, private val itemClickListener: (Sale)->Unit) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    private val internalSalesList = mutableListOf<Sale>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_sale_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.sale_title).text =
            internalSalesList[position].title
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(internalSalesList[position])
        }
    }

    override fun getItemCount(): Int = internalSalesList.size

    fun submitList(sales: List<Sale>) {
        internalSalesList.clear()
        internalSalesList.addAll(sales)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
