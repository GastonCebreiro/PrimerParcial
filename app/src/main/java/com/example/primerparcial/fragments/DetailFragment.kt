package com.example.primerparcial.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.primerparcial.R
import com.example.primerparcial.adapters.IngredientsAdapter
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.DishDao
import com.example.primerparcial.entities.Dish
import com.example.primerparcial.entities.DishRepository
import com.example.primerparcial.entities.User
import com.example.primerparcial.fragments.HomeFragment.Companion.MODE_ADD
import com.example.primerparcial.fragments.HomeFragment.Companion.MODE_EDIT
import com.example.primerparcial.fragments.HomeFragment.Companion.MODE_SHOW
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DetailFragment : Fragment() {

    lateinit var v: View

    var dishSelected: Dish? = null
    var detailMode: String = MODE_ADD

    lateinit var tvDetailName: TextView
    lateinit var tvDetailPrice: TextView
    lateinit var tvDetailCategory: TextView
    lateinit var spinnerCategory: Spinner
    lateinit var rvDetailIngredients: RecyclerView
    lateinit var ivDetailPhoto: ImageView
    lateinit var clShowView: ConstraintLayout
    lateinit var clAddView: ConstraintLayout
    lateinit var etDishNewName: EditText
    lateinit var etDishNewPrice: EditText
    lateinit var ivDishNewPhoto: ImageView
    lateinit var btnSave: Button

    lateinit var adapterIngredients: IngredientsAdapter

    private var db: AppDatabase? = null
    private var dishDao: DishDao? = null

    private var dishRepository: DishRepository? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_detail, container, false)

        tvDetailName = v.findViewById(R.id.tvDetailName)
        tvDetailPrice = v.findViewById(R.id.tvDetailPrice)
        tvDetailCategory = v.findViewById(R.id.tvDetailCategory)
        rvDetailIngredients = v.findViewById(R.id.rvIngredients)
        ivDetailPhoto = v.findViewById(R.id.ivDetailPhoto)
        spinnerCategory = v.findViewById(R.id.spinnerCategory)
        clShowView = v.findViewById(R.id.clShowView)
        clAddView = v.findViewById(R.id.clAddView)
        etDishNewName = v.findViewById(R.id.etDishNewName)
        etDishNewPrice = v.findViewById(R.id.etDishNewPrice)
        ivDishNewPhoto = v.findViewById(R.id.ivDishNewPhoto)
        btnSave = v.findViewById(R.id.btnSaveDish)

        db = AppDatabase.getInstance(requireContext())
        dishDao = db?.dishDao()
        dishRepository = DishRepository(dishDao)

        return v
    }

    override fun onStart() {
        super.onStart()

        dishSelected = DetailFragmentArgs.fromBundle(requireArguments()).dishSelected
        detailMode = DetailFragmentArgs.fromBundle(requireArguments()).detailMode

        Log.d("GASTON", "dishSelected.name=${dishSelected?.name}")
        Log.d("GASTON", "detailMode=$detailMode")

        when (detailMode) {
            MODE_SHOW -> {
                setShowView()
            }
            MODE_ADD -> {
                setSpinnerCategory()
                setAddView()
            }
            MODE_EDIT -> {
                setEditView()
            }
        }

    }

    private fun setShowView() {
        clShowView.visibility = View.VISIBLE

        tvDetailName.text = dishSelected!!.name
        tvDetailPrice.text = dishSelected!!.price.toString()
        tvDetailCategory.text = dishSelected!!.category
        Glide.with(ivDetailPhoto)
            .load(dishSelected!!.imageUrl)
            .into(ivDetailPhoto)

        val ingredients: List<String> = dishSelected?.ingredients?.split("-") ?: mutableListOf()
        adapterIngredients = IngredientsAdapter(ingredients.toMutableList())
        rvDetailIngredients.layoutManager = LinearLayoutManager(context)
        rvDetailIngredients.adapter = adapterIngredients
    }

    private fun setEditView() {
        TODO("Not yet implemented")
    }

    private fun setAddView() {
        clAddView.visibility = View.VISIBLE

        btnSave.setOnClickListener {
            saveButtonAction()
        }
    }

    private fun saveButtonAction() {
        dishSelected?.name = etDishNewName.text.toString().trim()
        var price = 0.0
        if (!etDishNewPrice.text.isNullOrEmpty())
            price = etDishNewPrice.text.toString().trim().toDouble()
        dishSelected?.price = price
        //FIXME INGREDIENTS HARDCODED FOR TEST
        dishSelected?.ingredients = mutableListOf("Agua", "Botella").joinToString("-")
        //FIXME IMAGE HARDCODED FOR TEST
        dishSelected?.imageUrl =
            "https://carrefourar.vtexassets.com/arquivos/ids/171888/7790315000446_02.jpg?v=637468542321600000"
        //TODO INGREDIENTS SELECTION
        dishSelected?.userId = getUserIdFromSharedPref()

        Log.d(
            "GASTON",
            "name=${dishSelected?.name}\nprice=${dishSelected?.price}\ningredients=${dishSelected?.ingredients}"
        )

        insertDishInDB()

        findNavController().navigateUp()
    }

    private fun setSpinnerCategory() {
        val itemsCategory = listOf("Entrada", "Plato Principal", "Postre", "Bebidas")

        val adapterCategory =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsCategory)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapterCategory

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getItemAtPosition(position)
                Toast.makeText(
                    requireContext(),
                    "Categoria Seleccionada: $item",
                    Toast.LENGTH_SHORT
                ).show()
                dishSelected?.category = item.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun getUserIdFromSharedPref(): Int {
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences("USER_LOGGED_PREF", Context.MODE_PRIVATE)
        val userLoggedJson = sharedPref.getString("USER_LOGGED_IN", "")
        if (userLoggedJson.isNullOrEmpty()) return 0
        return Gson().fromJson(userLoggedJson, User::class.java).id
    }

    private fun insertDishInDB() {
        dishRepository?.insertDish(
            dishSelected?.name!!,
            dishSelected?.category!!,
            dishSelected?.ingredients!!,
            dishSelected?.price!!,
            dishSelected?.imageUrl!!,
            dishSelected?.userId!!
        )
    }


}