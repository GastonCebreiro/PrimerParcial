package com.example.primerparcial.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.primerparcial.R
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.UserDao
import com.example.primerparcial.entities.User
import com.example.primerparcial.entities.UserRepository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class LoginFragment : Fragment() {

    lateinit var v: View

    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    lateinit var etInputMail: EditText
    lateinit var etInputPassword: EditText

    private var db: AppDatabase? = null
    private var userDao: UserDao? = null

    private var userRepository: UserRepository? = null

    //FIXME USE REPOSITORY
    private var userList: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_login, container, false)

        btnLogin = v.findViewById(R.id.btnLogin)
        btnRegister = v.findViewById(R.id.btnRegister)
        etInputMail = v.findViewById(R.id.etInputMail)
        etInputPassword = v.findViewById(R.id.etInputPassword)

        db = AppDatabase.getInstance(requireContext())
        userDao = db?.userDao()
        userRepository = UserRepository(userDao)

        //FIXME USE DATABASE
        userList = createHardcodedList()

        return v
    }

    override fun onStart() {
        super.onStart()



        clearEntries()

        btnLogin.setOnClickListener{
            loginButtonAction()
        }
        btnRegister.setOnClickListener{
            registerButtonAction()
        }
    }

    private fun clearEntries() {
        etInputMail.setText("")
        etInputPassword.setText("")
        etInputPassword.clearFocus()
        etInputMail.requestFocus()
    }

    private fun registerButtonAction() {
        navToNewUserFragment()
    }

    private fun loginButtonAction() {
        val userLogged = getUserSelected()
        if(userLogged != null) {

            saveUserInSharedPref(userLogged)

            navToMainActivity()
        }
    }

    private fun saveUserInSharedPref(userLogged: User) {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences("USER_LOGGED_PREF", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val userLoggedJson = Gson().toJson(userLogged)

        editor.putString("USER_LOGGED_IN", userLoggedJson)
        editor.apply()
    }

    private fun getUserSelected(): User?{
        val userSelected: User?
        var errorMessage: String?
        val mail: String = etInputMail.text.toString().trim()
        val password: String = etInputPassword.text.toString()

        errorMessage = checkEmptyEntries(mail, password)
        if(!errorMessage.isNullOrEmpty()){
            showSnackbar(errorMessage)
            return null
        }

        userSelected = findUserSelected(mail)
        Log.d("GASTON", "userSelected=$userSelected")

        if (userSelected == null) {
            errorMessage = getString(R.string.snackbar_mail_not_found)
            showSnackbar(errorMessage)
            return null
        }

        errorMessage = checkUserPassword(userSelected, password)
        if(!errorMessage.isNullOrEmpty()){
            showSnackbar(errorMessage)
            return null
        }

        return userSelected
    }

    private fun checkUserPassword(userSelected: User, password: String): String? {
        return if (userSelected.password != password)
            getString(R.string.snackbar_invalid_password)
        else
            null
    }

    private fun checkEmptyEntries(mail: String, password: String): String?{
        return when{
            mail.isBlank() ->
                getString(R.string.snackbar_enter_mail)
            password.isBlank() ->
                getString(R.string.snackbar_enter_password)
            else ->
                null
        }
    }

    private fun findUserSelected(mail: String): User? {
        return userRepository?.getUserByEmail(mail)
    }

    private fun navToNewUserFragment(){
        val action = LoginFragmentDirections.actionLoginFragmentToNewUserFragment()
        findNavController().navigate(action)
    }

    private fun navToMainActivity(){
        val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
        findNavController().navigate(action)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show()
    }


    //FIXME DELETE THIS WHEN DATABASE READY
    private fun createHardcodedList(): MutableList<User> {
        val users: MutableList<User> = mutableListOf()
        users.add(User(1, "Kai Havertz", "khavertz@chelseafc.com", "2929"))
        users.add(User(2, "Mason Mount", "mmount@chelseafc.com", "1919"))
        users.add(User(3, "Ngolo Kante", "nkante@chelseafc.com", "7777"))
        users.add(User(4, "Enzo Fernandez", "efernandez@chelseafc.com", "5555"))

        return users
    }

}