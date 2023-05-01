package com.example.primerparcial.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.primerparcial.R
import com.example.primerparcial.activities.LoginActivity
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.UserDao
import com.example.primerparcial.entities.User
import com.example.primerparcial.entities.UserRepository
import com.google.gson.Gson


class UserFragment : Fragment() {

    lateinit var v: View
    lateinit var btnDelete: Button
    lateinit var btnSignOff: Button

    private var db: AppDatabase? = null
    private var userDao: UserDao? = null

    private var userRepository: UserRepository? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_user, container, false)

        db = AppDatabase.getInstance(requireContext())
        userDao = db?.userDao()
        userRepository = UserRepository(userDao)

        btnDelete = v.findViewById(R.id.btnDeleteUser)
        btnSignOff = v.findViewById(R.id.btnSignOff)

        return v
    }

    override fun onStart() {
        super.onStart()

        btnDelete.setOnClickListener{
            deleteButtonAction()
        }

        btnSignOff.setOnClickListener{
            deleteSignOffAction()
        }
    }

    private fun deleteButtonAction() {
        val userLogged = getUserFromSharedPref()
        if(userLogged != null) {

            userRepository?.deleteUser(userLogged)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.fragment_user_delete_title))
            builder.setMessage(getString(R.string.fragment_user_delete_message))
            builder.setPositiveButton(getString(R.string.activity_main_logout_yes)) { _, _ ->
                navToLoginActivity()
            }
            builder.setNegativeButton(getString(R.string.activity_main_logout_no)) { _, _ -> }
            builder.create().show()
        }
    }

    private fun deleteSignOffAction() {
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

    private fun getUserFromSharedPref(): User?{
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("USER_LOGGED_PREF", Context.MODE_PRIVATE)
        val userLoggedJson =  sharedPref.getString("USER_LOGGED_IN","")
        if(userLoggedJson.isNullOrEmpty()) return null
        return Gson().fromJson(userLoggedJson, User::class.java)
    }
}