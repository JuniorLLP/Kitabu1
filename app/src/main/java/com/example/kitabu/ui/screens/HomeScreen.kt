package com.example.kitabu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.kitabu.data.local.entities.BookEntity
import com.example.kitabu.ui.components.AvailablePill
import com.example.kitabu.ui.components.BookCoverImage
import com.example.kitabu.ui.components.BorrowedPill
import com.example.kitabu.ui.theme.*
import com.example.kitabu.ui.viewmodel.KitabuViewModel

@Composable
fun HomeScreen(viewModel: KitabuViewModel, onBookClick: (Int) -> Unit) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val availableBooks = books.filter { it.isAvailable }
    val borrowedBooks = books.filter { !it.isAvailable }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        StatCardsRow(
            total = books.size,
            available = availableBooks.size,
            borrowed = borrowedBooks.size
        )
        
        SectionHeader(title = "Available Now", onSeeAll = {})
        BookRow(books = availableBooks, onBookClick = onBookClick)
        
        SectionHeader(title = "Recently Added", onSeeAll = {})
        BookRow(books = books.reversed(), onBookClick = onBookClick)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryNavy)
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 48.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Kitabu",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Your university library in your pocket",
                color = TaglineGray,
                fontSize = 14.sp
            )
        }
        
        // Search bar floating
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 28.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = InactiveNavIcon)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search by title or author...",
                    color = InactiveNavIcon,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StatCardsRow(total: Int, available: Int, borrowed: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Total Books",
            value = total.toString(),
            bgColor = BlueTint,
            contentColor = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Available",
            value = available.toString(),
            bgColor = GreenTint,
            contentColor = SuccessGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Borrowed",
            value = borrowed.toString(),
            bgColor = RedTint,
            contentColor = DangerRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(label: String, value: String, bgColor: Color, contentColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(text = value, color = contentColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = contentColor.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 40.dp, end = 24.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = BodyTextDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "See all →",
            color = PrimaryBlue,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun BookRow(books: List<BookEntity>, onBookClick: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            BookHomeCard(book = book, onClick = { onBookClick(book.bookId) })
        }
    }
}

@Composable
fun BookHomeCard(book: BookEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                BookCoverImage(
                    coverUrl = book.coverUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Rating Badge
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "★ ${book.rating}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Favorite Indicator
                if (book.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = DangerRed,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = book.title, color = BodyTextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(text = book.author, color = MutedTextGray, fontSize = 12.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(12.dp))
                if (book.isAvailable) AvailablePill() else BorrowedPill()
            }
        }
    }
}
