package com.example.primerparcial.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.fragment.findNavController
import com.example.primerparcial.R
import com.example.primerparcial.activities.LoginActivity
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.DishDao
import com.example.primerparcial.database.UserDao
import com.example.primerparcial.entities.DishRepository
import com.example.primerparcial.entities.User
import com.example.primerparcial.entities.UserRepository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class UserFragment : Fragment() {

    lateinit var v: View
    lateinit var btnDelete: Button
    lateinit var btnSignOff: Button
    lateinit var ivEdit: ImageView
    lateinit var tvUserName: TextView
    lateinit var etUserName: EditText
    lateinit var tvUserEmail: TextView
    lateinit var etUserEmail: EditText
    lateinit var tvUserPassword: TextView
    lateinit var etUserPassword: EditText
    lateinit var btnSaveChanges: Button
    lateinit var btnCancelChanges: Button
    lateinit var ivVisible: ImageView

    private var db: AppDatabase? = null
    private var userDao: UserDao? = null
    private var dishDao: DishDao? = null

    private var userRepository: UserRepository? = null
    private var dishRepository: DishRepository? = null

    private var userLogged: User? = null

    private var isEditMode = false
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_user, container, false)

        db = AppDatabase.getInstance(requireContext())
        userDao = db?.userDao()
        dishDao = db?.dishDao()
        userRepository = UserRepository(userDao)
        dishRepository = DishRepository(dishDao)

        userLogged = getUserFromSharedPref()

        btnDelete = v.findViewById(R.id.btnDeleteUser)
        btnSignOff = v.findViewById(R.id.btnSignOff)
        btnSaveChanges = v.findViewById(R.id.btnSaveChanges)
        btnCancelChanges = v.findViewById(R.id.btnCancelChanges)
        ivEdit = v.findViewById(R.id.ivEdit)
        tvUserName = v.findViewById(R.id.tvUserName)
        tvUserEmail = v.findViewById(R.id.tvUserEmail)
        tvUserPassword = v.findViewById(R.id.tvUserPassword)
        etUserName = v.findViewById(R.id.etUserName)
        etUserEmail = v.findViewById(R.id.etUserEmail)
        etUserPassword = v.findViewById(R.id.etUserPassword)
        ivVisible = v.findViewById(R.id.ivVisible)

        return v
    }

    override fun onStart() {
        super.onStart()
        if (userLogged == null) return

        setInitView()

        btnDelete.setOnClickListener {
            deleteButtonAction()
        }

        btnSignOff.setOnClickListener {
            signOffButtonAction()
        }

        ivEdit.setOnClickListener {
            editButtonAction()
        }

        btnSaveChanges.setOnClickListener {
            saveButtonAction()
        }

        btnCancelChanges.setOnClickListener {
            editButtonAction()
        }

        ivVisible.setOnClickListener{
            passwordVisibilityAction()
        }
    }

    private fun passwordVisibilityAction() {
        if (!isEditMode) return

        val imageVisible: Int
        val inputType: Int

        if(isPasswordVisible) {
            isPasswordVisible = false
            imageVisible = R.drawable.visible
            inputType = 0x00000012
        } else {
            isPasswordVisible = true
            imageVisible = R.drawable.invisible
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        ivVisible.setImageResource(imageVisible)
        etUserPassword.inputType = inputType
    }

    private fun saveButtonAction() {
        val userName = getText(etUserName)
        val userEmail = getText(etUserEmail)
        val userPassword = getText(etUserPassword)

        val errorMessage = checkValidEntries(userName, userEmail, userPassword)

        if (errorMessage.isNotEmpty()) {
            Snackbar.make(v, errorMessage, Snackbar.LENGTH_SHORT).show()
            return
        }

        userLogged!!.name = userName
        userLogged!!.email = userEmail
        userLogged!!.password = userPassword

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.fragment_user_update_title))
        builder.setMessage(getString(R.string.fragment_user_update_message))
        builder.setPositiveButton(getString(R.string.activity_main_logout_yes)) { _, _ ->
            updateUserInDB()
            navToLoginActivity()
        }
        builder.setNegativeButton(getString(R.string.activity_main_logout_no)) { _, _ -> }
        builder.create().show()
    }

    private fun editButtonAction() {
        if (isEditMode) {
            isEditMode = false

            ivVisible.visibility = View.INVISIBLE

            ivEdit.setBackgroundColor(Color.TRANSPARENT)

            setTextViewVisibility(View.VISIBLE)
            setEditTextVisibility(View.INVISIBLE)

            btnSignOff.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
            btnSaveChanges.visibility = View.INVISIBLE
            btnCancelChanges.visibility = View.INVISIBLE
        } else {
            isEditMode = true

            ivVisible.visibility = View.VISIBLE

            ivEdit.setBackgroundColor(getColor(requireContext(), R.color.light_blue))

            setTextViewVisibility(View.INVISIBLE)
            setEditTextVisibility(View.VISIBLE)

            etUserName.setText(userLogged!!.name)
            etUserEmail.setText(userLogged!!.email)
            etUserPassword.setText(userLogged!!.password)

            btnSignOff.visibility = View.INVISIBLE
            btnDelete.visibility = View.INVISIBLE
            btnSaveChanges.visibility = View.VISIBLE
            btnCancelChanges.visibility = View.VISIBLE
        }
    }

    private fun setEditTextVisibility(visibility: Int) {
        etUserName.visibility = visibility
        etUserEmail.visibility = visibility
        etUserPassword.visibility = visibility
    }

    private fun setTextViewVisibility(visibility: Int) {
        tvUserName.visibility = visibility
        tvUserEmail.visibility = visibility
        tvUserPassword.visibility = visibility
    }

    private fun setInitView() {
        tvUserName.text = userLogged!!.name
        tvUserEmail.text = userLogged!!.email
        tvUserPassword.text = userLogged!!.password

        setEditTextVisibility(View.INVISIBLE)
        btnSaveChanges.visibility = View.INVISIBLE
        btnCancelChanges.visibility = View.INVISIBLE
    }

    private fun deleteButtonAction() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.fragment_user_delete_title))
        builder.setMessage(getString(R.string.fragment_user_delete_message))
        builder.setPositiveButton(getString(R.string.activity_main_logout_yes)) { _, _ ->
            deleteUserDishesFromDB()
            deleteUserFromDB()
            navToLoginActivity()
        }
        builder.setNegativeButton(getString(R.string.activity_main_logout_no)) { _, _ -> }
        builder.create().show()

    }

    private fun signOffButtonAction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.fragment_user_sign_off_title))
        builder.setMessage(getString(R.string.fragment_user_sign_off_message))
        builder.setPositiveButton(getString(R.string.activity_main_logout_yes)) { _, _ ->
            navToLoginActivity()
        }
        builder.setNegativeButton(getString(R.string.activity_main_logout_no)) { _, _ -> }
        builder.create().show()
    }

    private fun navToLoginActivity() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

    private fun getUserFromSharedPref(): User? {
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences("USER_LOGGED_PREF", Context.MODE_PRIVATE)
        val userLoggedJson = sharedPref.getString("USER_LOGGED_IN", "")
        if (userLoggedJson.isNullOrEmpty()) return null
        return Gson().fromJson(userLoggedJson, User::class.java)
    }

    private fun deleteUserDishesFromDB() {
        dishRepository?.deleteDishByUserId(userLogged!!.id)
    }

    private fun deleteUserFromDB() {
        userRepository?.deleteUser(userLogged!!)
    }

    private fun updateUserInDB() {
        userRepository?.updateUser(userLogged!!)
    }

    private fun getText(editText: EditText): String = editText.text.toString().trim()
    private fun checkValidEntries(
        userName: String,
        userEmail: String,
        userPassword: String
    ): String {
        return when {
            userName.isBlank() -> {
                getString(R.string.snackbar_new_user_name)
            }
            userEmail.isBlank() || !userEmail.contains("@") -> {
                getString(R.string.snackbar_new_user_email)
            }
            userPassword.isBlank() ->
                getString(R.string.snackbar_new_user_password)
            else ->
                ""
        }
    }
}