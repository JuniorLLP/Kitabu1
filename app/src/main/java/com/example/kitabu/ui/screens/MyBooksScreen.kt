package com.example.kitabu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.local.entities.BookingEntity
import com.example.kitabu.data.model.BookingStatus
import com.example.kitabu.ui.components.AvailablePill
import com.example.kitabu.ui.components.BookCoverImage
import com.example.kitabu.ui.components.PendingPill
import com.example.kitabu.ui.theme.*
import com.example.kitabu.ui.viewmodel.KitabuViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyBooksScreen(viewModel: KitabuViewModel) {
    val bookings by viewModel.activeBookings.collectAsStateWithLifecycle()
    val activeRentals = bookings.filter { it.status == BookingStatus.ACTIVE }
    val pendingReservations = bookings.filter { it.status == BookingStatus.PENDING }
    
    var showRenewModal by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<BookingEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryNavy)
                .padding(top = 48.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "My Books", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "${bookings.size} reservations",
                    color = TaglineGray,
                    fontSize = 14.sp
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            if (activeRentals.isNotEmpty()) {
                item { SectionLabel("ACTIVE RENTALS") }
                items(activeRentals) { booking ->
                    ActiveRentalCard(
                        booking = booking,
                        onRenew = { 
                            selectedBooking = booking
                            showRenewModal = true
                        },
                        onReturn = { viewModel.returnBook(booking) }
                    )
                }
            }

            if (pendingReservations.isNotEmpty()) {
                item { SectionLabel("PENDING RESERVATIONS") }
                items(pendingReservations) { booking ->
                    PendingReservationCard(
                        booking = booking,
                        onCancel = { viewModel.cancelBooking(booking) }
                    )
                }
            }
        }
    }

    if (showRenewModal && selectedBooking != null) {
        RenewRentalModal(
            booking = selectedBooking!!,
            onDismiss = { showRenewModal = false },
            onConfirm = { days ->
                viewModel.renewBooking(selectedBooking!!, days)
                showRenewModal = false
            }
        )
    }
}

@Composable
fun SectionLabel(label: String) {
    Text(
        text = label,
        color = MutedTextGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun ActiveRentalCard(
    booking: BookingEntity,
    onRenew: () -> Unit,
    onReturn: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val remainingDays = ((booking.returnDeadline - System.currentTimeMillis()) / (24 * 60 * 60 * 1000L)).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BookCoverImage(
                    coverUrl = booking.bookCoverUrl,
                    contentDescription = booking.bookTitle,
                    modifier = Modifier.size(56.dp, 72.dp).clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = booking.bookTitle, color = BodyTextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = booking.bookAuthor, color = MutedTextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    AvailablePill()
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightGrayInset)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Reserved", color = MutedTextGray, fontSize = 12.sp)
                    Text(text = dateFormat.format(Date(booking.bookingDate)), color = BodyTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Due", color = MutedTextGray, fontSize = 12.sp)
                    Text(text = dateFormat.format(Date(booking.returnDeadline)), color = BodyTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⏳", fontSize = 16.sp) // Hourglass emoji as in screenshot
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$remainingDays days remaining",
                    color = WarningOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onRenew,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Renew", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onReturn,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RedTint),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Return", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingReservationCard(
    booking: BookingEntity,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BookCoverImage(
                    coverUrl = booking.bookCoverUrl,
                    contentDescription = booking.bookTitle,
                    modifier = Modifier.size(56.dp, 72.dp).clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = booking.bookTitle, color = BodyTextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = booking.bookAuthor, color = MutedTextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    PendingPill()
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeTint),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Cancel Reservation", color = WarningOrange, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RenewRentalModal(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val currentReturnDate = Date(booking.returnDeadline)
    val newReturnDate = Date(booking.returnDeadline + (14 * 24 * 60 * 60 * 1000L)) // 14 days in screenshot

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Text(text = "Renew Rental", color = BodyTextDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column {
                Text(text = booking.bookTitle, color = BodyTextDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGrayInset)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Current return date", color = MutedTextGray, fontSize = 14.sp)
                        Text(text = dateFormat.format(currentReturnDate), color = BodyTextDark, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "New return date", color = MutedTextGray, fontSize = 14.sp)
                        Text(text = dateFormat.format(newReturnDate), color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = LightGrayInset),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Cancel", color = BodyTextDark, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onConfirm(14) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
