package com.example.primerparcial.entities

import androidx.room.util.joinIntoString
import com.example.primerparcial.database.DishDao
import com.google.gson.Gson

class DishRepository(dishDao: DishDao?) {

    private val dishDao = dishDao

    fun getDishesByUserId(userId: Int): MutableList<Dish?>? {
        return dishDao?.fetchDishesByUserId(userId)
    }

    fun deleteDish(dish: Dish) {
        dishDao?.delete(dish)
    }

    fun updateDish(dish: Dish){
        dishDao?.updateDish(dish)
    }

    fun getAllDishes(): MutableList<Dish?>? {
        return dishDao?.fetchAllDishes()
    }

    fun insertDish(
        dishName: String,
        dishCategory: String,
        dishIngredients: String,
        dishPrice: Double,
        dishImage: String,
        dishUserId: Int
    ) {
        dishDao?.insertDish(
            Dish(
                0,
                dishName,
                dishCategory,
                dishIngredients,
                dishPrice,
                dishImage,
                dishUserId
            )
        )
    }
}