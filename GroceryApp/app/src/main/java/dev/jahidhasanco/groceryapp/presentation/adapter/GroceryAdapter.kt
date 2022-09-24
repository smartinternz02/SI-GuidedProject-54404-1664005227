package dev.jahidhasanco.groceryapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jahidhasanco.groceryapp.R
import dev.jahidhasanco.groceryapp.data.local.model.GroceryItems

class GroceryAdapter(
    var list: List<GroceryItems>, val groceryItemClickInterface: GroceryItemClickInterface
) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    interface GroceryItemClickInterface {
        fun onItemClick(groceryItems: GroceryItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.nameTV.text = list[position].itemName
        holder.quantityTV.text = list[position].itemQuantity.toString()
        holder.rateTV.text = "Rs. ${list.get(position).itemPrice}"
        val itemTotal: Int = list[position].itemPrice * list[position].itemQuantity
        holder.amountTV.text = "Rs. " + itemTotal.toString()
        holder.deleteTV.setOnClickListener {
            groceryItemClickInterface.onItemClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV = itemView.findViewById<TextView>(R.id.name)
        val quantityTV = itemView.findViewById<TextView>(R.id.quantity)
        val rateTV = itemView.findViewById<TextView>(R.id.rate)
        val amountTV = itemView.findViewById<TextView>(R.id.amount)
        val deleteTV = itemView.findViewById<ImageView>(R.id.delete)
    }
}