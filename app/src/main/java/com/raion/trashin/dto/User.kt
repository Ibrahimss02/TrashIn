package com.raion.trashin.dto

data class User (
        val id : String = "",
        val email : String = "",
        val username : String = "",
        val point : Int = 0,
        val productListId : ArrayList<String> = arrayListOf()
)