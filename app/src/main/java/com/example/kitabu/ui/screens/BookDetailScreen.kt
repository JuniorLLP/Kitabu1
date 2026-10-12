package com.example.kitabu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.local.entities.BookEntity
import com.example.kitabu.ui.components.AvailablePill
import com.example.kitabu.ui.components.BookCoverImage
import com.example.kitabu.ui.components.BorrowedPill
import com.example.kitabu.ui.theme.*
import com.example.kitabu.ui.viewmodel.KitabuViewModel

@Composable
fun BookDetailScreen(bookId: Int, viewModel: KitabuViewModel, onBack: () -> Unit) {
    LaunchedEffect(bookId) {
        viewModel.getBookById(bookId)
    }

    val book by viewModel.selectedBook.collectAsStateWithLifecycle()

    book?.let { currentBook ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
        ) {
            HeroHeader(
                book = currentBook, 
                onBack = onBack,
                onFavoriteToggle = { viewModel.toggleFavorite(currentBook) }
            )
            
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(72.dp))
                
                Text(
                    text = currentBook.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = BodyTextDark,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = currentBook.author,
                    fontSize = 18.sp,
                    color = MutedTextGray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                if (currentBook.isAvailable) AvailablePill(isDetail = true) else BorrowedPill(isDetail = true)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                MetadataRow(currentBook)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 32.dp), color = DividerGray)
                
                Text(
                    text = "Description",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BodyTextDark
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = currentBook.description,
                    color = MutedTextGray,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                ActionButton(
                    isAvailable = currentBook.isAvailable,
                    onClick = { viewModel.reserveBook(currentBook, "User", 7) }
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun HeroHeader(book: BookEntity, onBack: () -> Unit, onFavoriteToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Blurred Cover Background
        BookCoverImage(
            coverUrl = book.coverUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        // Dark Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryNavy.copy(alpha = 0.7f), PrimaryNavy),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 48.dp, start = 24.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x55000000))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
        }

        // Favorite Button
        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x55000000))
        ) {
            Icon(
                if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (book.isFavorite) DangerRed else Color.White
            )
        }
        
        // Floating Cover
        BookCoverImage(
            coverUrl = book.coverUrl,
            contentDescription = book.title,
            modifier = Modifier
                .size(160.dp, 220.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 60.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

@Composable
fun MetadataRow(book: BookEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetadataCard(label = "Category", value = book.category, modifier = Modifier.weight(1.2f))
        MetadataCard(label = "Pages", value = book.pages.toString(), modifier = Modifier.weight(0.9f))
        MetadataCard(label = "Year", value = book.year.toString(), modifier = Modifier.weight(0.9f))
        MetadataCard(label = "Rating", value = "★ ${book.rating}", modifier = Modifier.weight(1.0f))
    }
}

@Composable
fun MetadataCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontWeight = FontWeight.Bold, color = BodyTextDark, fontSize = 16.sp, maxLines = 1)
        Text(text = label, color = MutedTextGray, fontSize = 12.sp)
    }
}

@Composable
fun ActionButton(isAvailable: Boolean, onClick: () -> Unit) {
    if (isAvailable) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(text = "Reserve Book", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(RedTint)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(DangerRed))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Currently Borrowed", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "You can check back later", color = MutedTextGray, fontSize = 12.sp)
        }
    }
}
