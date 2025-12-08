package com.example.finalseldroid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: Int,
    var quantity: Int,
    val stock: Int
) : Parcelable
