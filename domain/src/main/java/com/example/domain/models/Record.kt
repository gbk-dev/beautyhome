package com.example.domain.models

data class Record(
    val date: String = "",
    val time: List<String> = listOf(),
    val service: List<String> = listOf(),
    val userName: String = "",
    val phone: String = ""
)
