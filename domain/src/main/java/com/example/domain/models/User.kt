package com.example.domain.models

data class User(
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val master: Boolean? = null,
    val uid: String? = ""
)
