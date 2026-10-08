package com.example.kitabu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
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
import com.example.kitabu.data.local.entities.BookEntity
import com.example.kitabu.ui.components.AvailablePill
import com.example.kitabu.ui.components.BorrowedPill
import com.example.kitabu.ui.theme.*
import com.example.kitabu.ui.viewmodel.KitabuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: KitabuViewModel, onBookClick: (Int) -> Unit) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredBooks = when (selectedFilter) {
        "Available" -> books.filter { it.isAvailable }
        "Borrowed" -> books.filter { !it.isAvailable }
        else -> books
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Navy Header with Search
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryNavy)
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Library", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = { Text("Search by title or author...", color = TaglineGray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TaglineGray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2B3B54),
                        unfocusedContainerColor = Color(0xFF2B3B54),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }

        // Filter Chips
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KitabuFilterChip(
                label = "All",
                isActive = selectedFilter == "All",
                onClick = { selectedFilter = "All" }
            )
            KitabuFilterChip(
                label = "Available",
                isActive = selectedFilter == "Available",
                onClick = { selectedFilter = "Available" }
            )
            KitabuFilterChip(
                label = "Borrowed",
                isActive = selectedFilter == "Borrowed",
                onClick = { selectedFilter = "Borrowed" }
            )
        }

        Text(
            text = "${filteredBooks.size} books found",
            color = MutedTextGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredBooks) { book ->
                BookHomeCard(book = book, onClick = { onBookClick(book.bookId) })
            }
        }
    }
}

@Composable
fun KitabuFilterChip(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) Color.White else Color(0xFF3A4A63))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isActive) PrimaryNavy else Color.White,
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
        )
    }
}
