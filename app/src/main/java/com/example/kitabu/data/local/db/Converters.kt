package com.example.kitabu.data.local.db

import androidx.room.TypeConverter
import com.example.kitabu.data.model.BookingStatus

class Converters {
    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(status: String): BookingStatus {
        return BookingStatus.valueOf(status)
    }
}
