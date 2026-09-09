package com.example.tvapp.data.model


data class Show(
    val id: Int,
    val url: String?,
    val name: String,
    val premiered: String?,
    val rating: Rating?,
    val image: ShowImage?,
    val summary: String?
)
