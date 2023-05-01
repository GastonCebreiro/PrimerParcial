package com.example.primerparcial.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primerparcial.R
import com.example.primerparcial.adapters.DishAdapter
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.DishDao
import com.example.primerparcial.database.UserDao
import com.example.primerparcial.entities.Dish
import com.example.primerparcial.entities.DishRepository
import com.example.primerparcial.entities.User
import com.example.primerparcial.entities.UserRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class HomeFragment : Fragment() {

    lateinit var v: View
    lateinit var rvDish: RecyclerView
    lateinit var adapter: DishAdapter
    lateinit var btnAdd: FloatingActionButton

    private var db: AppDatabase? = null
    private var dishDao: DishDao? = null

    private var dishRepository: DishRepository? = null

    private var dishes: MutableList<Dish?>? = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)
        rvDish = v.findViewById(R.id.rvDishes)
        btnAdd = v.findViewById(R.id.btnAddDish)

        db = AppDatabase.getInstance(requireContext())
        dishDao = db?.dishDao()
        dishRepository = DishRepository(dishDao)

        return v
    }


    override fun onStart() {
        super.onStart()

        /*
        dishRepository?.insertDish(
            "Pepe",
            "Entrada",
            mutableListOf("PAN", "CARNE", "QUESO").joinToString("-"),
            1000.0,
            "https://assets.unileversolutions.com/recipes-v2/232055.jpg",
            2
        )*/

        dishes = dishRepository?.getDishesByUserId(getUserIdFromSharedPref())

        if (dishes != null) {
            dishes?.forEach {
                if (it == null) {
                    Log.d("GASTON", "HAY UN DISH NULL")
                    return
                }
            }
        } else {
            Log.d("GASTON", "DISHES NULL")
            return
        }

        btnAdd.setOnClickListener {
            addButtonAction()
        }

        adapter = DishAdapter(dishes!!,
            onClick = { position -> showButtonAction(position) },
            onLongClick = { position -> editButtonAction(position) })
        rvDish.layoutManager = LinearLayoutManager(context)
        rvDish.adapter = adapter
    }

    private fun showButtonAction(position: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
            dishes!![position]!!,
            MODE_SHOW
        )
        findNavController().navigate(action)
    }

    private fun editButtonAction(position: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
            dishes!![position]!!,
            MODE_EDIT
        )
        findNavController().navigate(action)
    }

    private fun addButtonAction() {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
            Dish(0, "", "", "", 0.0, "", 1),
            MODE_ADD
        )
        findNavController().navigate(action)
    }

    private fun getUserIdFromSharedPref(): Int{
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("USER_LOGGED_PREF", Context.MODE_PRIVATE)
        val userLoggedJson =  sharedPref.getString("USER_LOGGED_IN","")
        if(userLoggedJson.isNullOrEmpty()) return 0
        return Gson().fromJson(userLoggedJson, User::class.java).id
    }

    companion object {
        const val MODE_ADD = "MODE_ADD"
        const val MODE_SHOW = "MODE_SHOW"
        const val MODE_EDIT = "MODE_EDIT"
    }


}