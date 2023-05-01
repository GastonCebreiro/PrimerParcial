package com.example.primerparcial.fragments

import android.app.AlertDialog
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
    lateinit var btnAddIngredient: Button
    lateinit var rvNewIngredients: RecyclerView
    lateinit var btnDeleteDish: Button

    lateinit var adapterIngredients: IngredientsAdapter

    private var newIngredients: MutableList<String> = mutableListOf()
    private var newImageURL: String = ""
    private var newCategory: String = ""

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
        btnAddIngredient = v.findViewById(R.id.btnAddIngredient)
        rvNewIngredients = v.findViewById(R.id.rvNewIngredients)
        btnDeleteDish = v.findViewById(R.id.btnDeleteDish)

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
                setAddView()
                setSpinnerCategory(MODE_ADD)
            }
            MODE_EDIT -> {
                setEditView()
                setSpinnerCategory(MODE_EDIT)
            }
        }

    }

    private fun setShowView() {
        clShowView.visibility = View.VISIBLE

        tvDetailName.text = dishSelected!!.name
        tvDetailPrice.text = "$ " + dishSelected!!.price.toString()
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
        clAddView.visibility = View.VISIBLE

        etDishNewName.setText(dishSelected?.name)
        newCategory = dishSelected?.category ?: ""
        etDishNewPrice.setText(dishSelected?.price.toString())
        newIngredients = dishSelected?.ingredients?.split("-")!!.toMutableList()
        newImageURL = dishSelected?.imageUrl ?: ""
        Glide.with(ivDishNewPhoto)
            .load(dishSelected!!.imageUrl)
            .into(ivDishNewPhoto)

        btnSave.setOnClickListener {
            saveButtonAction(MODE_EDIT)
        }

        btnDeleteDish.visibility = View.VISIBLE
        btnDeleteDish.setOnClickListener {
            deleteDishButtonAction()
        }

        btnAddIngredient.setOnClickListener {
            addIngredientButtonAction()
        }

        ivDishNewPhoto.setOnClickListener {
            addImageButtonAction()
        }

        setAdapterIngredients()
    }

    private fun setAddView() {
        clAddView.visibility = View.VISIBLE

        btnSave.setOnClickListener {
            saveButtonAction(MODE_ADD)
        }

        btnAddIngredient.setOnClickListener {
            addIngredientButtonAction()
        }

        ivDishNewPhoto.setOnClickListener {
            addImageButtonAction()
        }

        setAdapterIngredients()
    }

    private fun setAdapterIngredients() {
        adapterIngredients = IngredientsAdapter(newIngredients)
        rvDetailIngredients.layoutManager = LinearLayoutManager(context)
        rvDetailIngredients.adapter = adapterIngredients
    }

    private fun deleteDishButtonAction() {
        deleteDishFormDB()
        findNavController().navigateUp()
    }

    private fun addImageButtonAction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.fragment_detail_image_title))

        val etImageURL = EditText(requireContext())
        builder.setView(etImageURL)

        builder.setPositiveButton("Guardar") { _, _ ->
            val newImageText = etImageURL.text.toString()
            if (newImageText.isNotBlank())
                newImageURL = newImageText
        }
        builder.setNegativeButton("Cancelar") { _, _ -> }

        builder.show()
    }

    private fun addIngredientButtonAction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.fragment_detail_ingredient_title))

        val etIngredient = EditText(requireContext())
        builder.setView(etIngredient)

        builder.setPositiveButton("Guardar") { _, _ ->
            val newIngredient = etIngredient.text.toString()
            if (newIngredient.isNotBlank())
                newIngredients.add(newIngredient)
        }
        builder.setNegativeButton("Cancelar") { _, _ -> }

        builder.show()
    }

    private fun saveButtonAction(mode: String) {
        dishSelected?.name = etDishNewName.text.toString().trim()
        dishSelected?.category = newCategory
        var price = 0.0
        if (!etDishNewPrice.text.isNullOrEmpty())
            price = etDishNewPrice.text.toString().trim().toDouble()
        dishSelected?.price = price
        dishSelected?.ingredients = newIngredients.joinToString("-")
        dishSelected?.imageUrl = newImageURL
        dishSelected?.userId = getUserIdFromSharedPref()

        Log.d(
            "GASTON",
            "name=${dishSelected?.name}\nprice=${dishSelected?.price}\ningredients=${dishSelected?.ingredients}"
        )

        if (mode == MODE_ADD)
            insertDishInDB()
        else
            updateDishInDB()

        findNavController().navigateUp()
    }

    private fun setSpinnerCategory(mode: String) {
        val itemsCategory = listOf(ENTRY, MAIN_COURSE, DESSERT, DRINK)

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
                newCategory = item.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        if (mode == MODE_EDIT) {
            val itemPosition = when (newCategory) {
                ENTRY -> 0
                MAIN_COURSE -> 1
                DESSERT -> 2
                else -> 3
            }
            spinnerCategory.setSelection(itemPosition)
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

    private fun deleteDishFormDB() {
        dishRepository?.deleteDish(dishSelected!!)
    }

    private fun updateDishInDB() {
        dishRepository?.updateDish(dishSelected!!)
    }


    companion object {
        const val ENTRY = "ENTRADA"
        const val MAIN_COURSE = "PLATO PRINCIPAL"
        const val DESSERT = "POSTRE"
        const val DRINK = "BEBIDA"
    }

}