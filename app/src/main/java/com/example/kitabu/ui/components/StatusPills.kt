package com.example.kitabu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitabu.ui.theme.*

@Composable
fun StatusPill(
    status: String,
    color: Color,
    backgroundColor: Color = Color.Transparent,
    showDot: Boolean = true
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = if (backgroundColor == Color.Transparent) 0.dp else 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        Text(
            text = status,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun AvailablePill(isDetail: Boolean = false) {
    StatusPill(
        status = "AVAILABLE",
        color = SuccessGreen,
        backgroundColor = if (isDetail) GreenTint else Color.Transparent
    )
}

@Composable
fun BorrowedPill(isDetail: Boolean = false) {
    StatusPill(
        status = "BORROWED",
        color = DangerRed,
        backgroundColor = if (isDetail) RedTint else Color.Transparent
    )
}

@Composable
fun PendingPill() {
    StatusPill(
        status = "PENDING",
        color = WarningOrange,
        backgroundColor = OrangeTint
    )
}
