package com.example.kitabu.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,
    val title: String,
    val author: String,
    val category: String,
    val description: String = "",
    val pages: Int = 0,
    val year: Int = 0,
    val rating: Double = 4.5,
    val isFavorite: Boolean = false,
    val coverUrl: String = "",
    val isAvailable: Boolean = true
)
