package com.example.primerparcial.database
import androidx.room.*
import com.example.primerparcial.entities.Dish
import com.example.primerparcial.entities.User

@Dao
interface DishDao {

    @Query("SELECT * FROM dishes ORDER BY id")
    fun fetchAllDishes(): MutableList<Dish?>?

    @Query("SELECT * FROM dishes WHERE userId = :userId")
    fun fetchDishesByUserId(userId: Int): MutableList<Dish?>?
    @Query("DELETE FROM dishes WHERE userId = :userId")
    fun deleteDishesByUserId(userId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDish(dish: Dish)

    @Update
    fun updateDish(dish: Dish)

    @Delete
    fun delete(dish: Dish)
}