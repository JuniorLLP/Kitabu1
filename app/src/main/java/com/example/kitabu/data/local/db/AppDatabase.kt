package com.example.kitabu.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kitabu.data.local.dao.BookDao
import com.example.kitabu.data.local.dao.BookingDao
import com.example.kitabu.data.local.entities.BookEntity
import com.example.kitabu.data.local.entities.BookingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BookEntity::class, BookingEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_db_v5"
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.bookDao())
                }
            }
        }

        suspend fun populateDatabase(bookDao: BookDao) {
            val books = listOf(
                BookEntity(
                    title = "Clean Code", 
                    author = "Robert C. Martin", 
                    category = "Software Engineering", 
                    description = "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees. Every year, countless hours and significant resources are lost because of poorly written code.", 
                    pages = 464, 
                    year = 2008,
                    coverUrl = "cover_1"
                ),
                BookEntity(
                    title = "Design Patterns", 
                    author = "Gang of Four", 
                    category = "Computer Science", 
                    description = "Capturing a wealth of experience about the design of object-oriented software, four top-notch designers present a catalog of simple and succinct solutions to commonly occurring design problems.", 
                    pages = 395, 
                    year = 1994,
                    coverUrl = "cover_2"
                ),
                BookEntity(
                    title = "The Pragmatic Programmer", 
                    author = "Andrew Hunt", 
                    category = "Software Engineering", 
                    description = "One of the most significant books in software development, The Pragmatic Programmer illustrates the best practices and major pitfalls of many different aspects of software development.", 
                    pages = 352, 
                    year = 1999,
                    coverUrl = "cover_3"
                ),
                BookEntity(
                    title = "You Don't Know JS", 
                    author = "Kyle Simpson", 
                    category = "Programming", 
                    description = "An in-depth guide to the core mechanisms of the JavaScript language.", 
                    pages = 278, 
                    year = 2014, 
                    isAvailable = false,
                    coverUrl = "cover_5"
                ),
                BookEntity(
                    title = "Refactoring", 
                    author = "Martin Fowler", 
                    category = "Software Engineering", 
                    description = "Improving the design of existing code through proven patterns and transformation rules.", 
                    pages = 448, 
                    year = 1999,
                    coverUrl = "cover_4"
                ),
                BookEntity(
                    title = "Operating System Concepts", 
                    author = "Silberschatz, Galvin", 
                    category = "Computer Science", 
                    description = "The fundamental concepts of operating systems including processes, memory, and file systems.", 
                    pages = 976, 
                    year = 2018,
                    coverUrl = "cover_6"
                ),
                BookEntity(
                    title = "Computer Networks", 
                    author = "Andrew S. Tanenbaum", 
                    category = "Networking", 
                    description = "The core principles of computer networking, hardware, and protocols.", 
                    pages = 912, 
                    year = 2010, 
                    isAvailable = false,
                    coverUrl = "cover_7"
                ),
                BookEntity(
                    title = "Effective Java", 
                    author = "Joshua Bloch", 
                    category = "Programming", 
                    description = "Best practices for the Java platform for every professional developer.", 
                    pages = 416, 
                    year = 2001,
                    coverUrl = "cover_8"
                )
            )
            books.forEach { bookDao.insertBook(it) }
        }
    }
}
