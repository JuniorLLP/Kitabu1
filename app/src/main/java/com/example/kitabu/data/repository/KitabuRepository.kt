package com.example.kitabu.data.repository

import com.example.kitabu.data.local.dao.BookDao
import com.example.kitabu.data.local.dao.BookingDao
import com.example.kitabu.data.local.entities.BookEntity
import com.example.kitabu.data.local.entities.BookingEntity
import kotlinx.coroutines.flow.Flow

class KitabuRepository(
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) {
    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()
    val activeBookings: Flow<List<BookingEntity>> = bookingDao.getActiveBookings()

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.searchBooks("%$query%")
    }

    suspend fun insertBook(book: BookEntity) = bookDao.insertBook(book)
    suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)
    suspend fun deleteBook(book: BookEntity) = bookDao.deleteBook(book)

    suspend fun insertBooking(booking: BookingEntity) = bookingDao.insertBooking(booking)
    suspend fun updateBooking(booking: BookingEntity) = bookingDao.updateBooking(booking)
    suspend fun deleteBooking(booking: BookingEntity) = bookingDao.deleteBooking(booking)
    
    suspend fun getBookById(bookId: Int) = bookDao.getBookById(bookId)
    suspend fun getBookingById(bookingId: Int) = bookingDao.getBookingById(bookingId)
}
