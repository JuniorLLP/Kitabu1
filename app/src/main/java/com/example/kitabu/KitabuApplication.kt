package com.example.kitabu

import android.app.Application
import com.example.kitabu.data.local.db.AppDatabase
import com.example.kitabu.data.repository.KitabuRepository

class KitabuApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { KitabuRepository(database.bookDao(), database.bookingDao()) }
}
