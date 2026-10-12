package com.example.kitabu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kitabu.data.local.entities.BookEntity
import com.example.kitabu.data.local.entities.BookingEntity
import com.example.kitabu.data.model.BookingStatus
import com.example.kitabu.data.repository.KitabuRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KitabuViewModel(private val repository: KitabuRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.allBooks
            } else {
                repository.searchBooks(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBookings: StateFlow<List<BookingEntity>> = repository.activeBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedBook = MutableStateFlow<BookEntity?>(null)
    val selectedBook = _selectedBook.asStateFlow()

    fun getBookById(bookId: Int) {
        viewModelScope.launch {
            _selectedBook.value = repository.getBookById(bookId)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(book: BookEntity) {
        viewModelScope.launch {
            val updated = book.copy(isFavorite = !book.isFavorite)
            repository.updateBook(updated)
            if (_selectedBook.value?.bookId == book.bookId) {
                _selectedBook.value = updated
            }
        }
    }

    fun reserveBook(book: BookEntity, userName: String, durationDays: Int) {
        viewModelScope.launch {
            val bookingDate = System.currentTimeMillis()
            val returnDeadline = bookingDate + (durationDays * 24 * 60 * 60 * 1000L)
            
            val booking = BookingEntity(
                bookId = book.bookId,
                bookTitle = book.title,
                bookAuthor = book.author,
                bookCoverUrl = book.coverUrl,
                userName = userName,
                bookingDate = bookingDate,
                returnDeadline = returnDeadline,
                status = BookingStatus.ACTIVE
            )
            repository.insertBooking(booking)
            repository.updateBook(book.copy(isAvailable = false))
        }
    }

    fun renewBooking(booking: BookingEntity, extendDays: Int) {
        viewModelScope.launch {
            val newDeadline = booking.returnDeadline + (extendDays * 24 * 60 * 60 * 1000L)
            repository.updateBooking(booking.copy(returnDeadline = newDeadline))
        }
    }

    fun returnBook(booking: BookingEntity) {
        viewModelScope.launch {
            repository.updateBooking(booking.copy(status = BookingStatus.RETURNED))
            val book = repository.getBookById(booking.bookId)
            book?.let {
                repository.updateBook(it.copy(isAvailable = true))
            }
        }
    }

    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.deleteBooking(booking)
            val book = repository.getBookById(booking.bookId)
            book?.let {
                repository.updateBook(it.copy(isAvailable = true))
            }
        }
    }
}

class KitabuViewModelFactory(private val repository: KitabuRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KitabuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KitabuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
