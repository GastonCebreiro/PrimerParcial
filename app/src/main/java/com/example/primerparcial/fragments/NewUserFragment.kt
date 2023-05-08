package com.example.primerparcial.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.primerparcial.R
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.UserDao
import com.example.primerparcial.entities.UserRepository
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class NewUserFragment : Fragment() {

    lateinit var v: View


    lateinit var etUserName: EditText
    lateinit var etUserEmail: EditText
    lateinit var etUserPassword: EditText
    lateinit var btnSave: Button
    lateinit var lUserName: TextInputLayout
    lateinit var lUserEmail: TextInputLayout
    lateinit var lUserPassword: TextInputLayout
    lateinit var ivUser: ImageView
    lateinit var ivMail: ImageView
    lateinit var ivPassword: ImageView

    private var db: AppDatabase? = null
    private var userDao: UserDao? = null

    private var userRepository: UserRepository? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_new_user, container, false)

        db = AppDatabase.getInstance(requireContext())
        userDao = db?.userDao()
        userRepository = UserRepository(userDao)

        etUserName = v.findViewById(R.id.etUserName)
        etUserEmail = v.findViewById(R.id.etUserEmail)
        etUserPassword = v.findViewById(R.id.etUserPassword)
        btnSave = v.findViewById(R.id.btnSave)
        lUserName = v.findViewById(R.id.lUserName)
        lUserEmail = v.findViewById(R.id.lUserEmail)
        lUserPassword = v.findViewById(R.id.lUserPassword)
        ivUser = v.findViewById(R.id.ivUser)
        ivMail = v.findViewById(R.id.ivMail)
        ivPassword = v.findViewById(R.id.ivPassword)

        return v
    }


    override fun onStart() {
        super.onStart()

        btnSave.setOnClickListener {
            saveButtonAction()
        }
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

        Log.d("GASTON", "userName=$userName\nuserEmail$userEmail\nuserPassword=$userPassword")
        userRepository?.insertUser(userName,userEmail,userPassword)

        Snackbar.make(v, getString(R.string.snackbar_new_user_register), Snackbar.LENGTH_SHORT).show()

        navToLoginFragment()
    }


    private fun checkValidEntries(
        userName: String,
        userEmail: String,
        userPassword: String
    ): String {
        val errorColor = ContextCompat.getColor(requireContext(), R.color.red)
        lUserName.error = null
        lUserEmail.error = null
        lUserPassword.error = null
        lUserName.errorIconDrawable = null
        lUserEmail.errorIconDrawable = null
        lUserPassword.errorIconDrawable = null
        ivUser.setColorFilter(R.color.black)
        ivMail.setColorFilter(R.color.black)
        ivPassword.setColorFilter(R.color.black)
        return when {
            userName.isBlank() -> {
                lUserName.error = getString(R.string.snackbar_new_user_name)
                ivUser.setColorFilter(errorColor)
                getString(R.string.snackbar_new_user_name)
            }
            userEmail.isBlank() || !userEmail.contains("@") -> {
                lUserEmail.error = getString(R.string.snackbar_new_user_email)
                ivMail.setColorFilter(errorColor)
                getString(R.string.snackbar_new_user_email)
            }
            userPassword.isBlank() -> {
                lUserPassword.error = getString(R.string.snackbar_new_user_password)
                ivPassword.setColorFilter(errorColor)
                getString(R.string.snackbar_new_user_password)
            }
            else ->
                ""
        }
    }

    private fun getText(editText: EditText): String = editText.text.toString().trim()

    private fun navToLoginFragment(){
        findNavController().navigateUp()
    }

}