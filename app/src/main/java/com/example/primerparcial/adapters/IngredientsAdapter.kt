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

class IngredientsAdapter(
    var ingredients: MutableList<String>,
) : RecyclerView.Adapter<IngredientsAdapter.IngredientHolder>() {

    class IngredientHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setIngredient(ingredient: String) {
            val tvIngredient: TextView = view.findViewById(R.id.tvIngredient)
            tvIngredient.text = ingredient
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return (IngredientHolder(view))
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        holder.setIngredient(ingredients[position])
    }

}