package com.example.primerparcial.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.primerparcial.R
import com.example.primerparcial.entities.Dish

class DishAdapter(
    var dishes: MutableList<Dish?>,
    var onClick: (Int) -> Unit,
    var onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<DishAdapter.DishHolder>() {

    class DishHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setName(name: String?) {
            val tvName: TextView = view.findViewById(R.id.tvName)
            tvName.text = name
        }

        fun setPrice(price: Double?) {
            val tvPrice: TextView = view.findViewById(R.id.tvPrice)
            tvPrice.text = "$ " + price.toString()
        }

        fun setImage(imageUrl: String?) {
            val ivPhoto: ImageView = view.findViewById(R.id.ivPhoto)
            Glide.with(ivPhoto)
                .load(imageUrl)
                .circleCrop()
                .into(ivPhoto)
        }

        fun getCard(): CardView {
            return view.findViewById(R.id.cardDish)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        return (DishHolder(view))
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        holder.setName(dishes[position]?.name)
        holder.setPrice(dishes[position]?.price)
        holder.setImage(dishes[position]?.imageUrl)

        holder.getCard().setOnClickListener {
            onClick(position)
        }
        holder.getCard().setOnLongClickListener {
            onLongClick(position)
            return@setOnLongClickListener true
        }
    }

}