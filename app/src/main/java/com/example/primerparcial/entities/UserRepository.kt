package com.example.primerparcial.entities

import android.content.Context
import android.util.Log
import com.example.primerparcial.database.AppDatabase
import com.example.primerparcial.database.UserDao

class UserRepository(userDao: UserDao?) {

    private val userDao = userDao

    fun getUserByEmail(email: String): User?{
        return userDao?.fetchUserByEmail(email)
    }

    fun insertUser(userName: String, userEmail: String, userPassword: String){
        userDao?.insertUser(User(0, userName, userEmail, userPassword))
    }

    fun deleteUser(user: User){
        userDao?.delete(user)
    }

}