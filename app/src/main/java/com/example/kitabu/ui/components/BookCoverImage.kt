package com.example.kitabu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.kitabu.ui.theme.DividerGray

@Composable
fun BookCoverImage(
    coverUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val drawableResId = remember(coverUrl) {
        if (coverUrl.isNotBlank()) {
            context.resources.getIdentifier(coverUrl, "drawable", context.packageName)
        } else {
            0
        }
    }

    if (drawableResId != 0) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(modifier = modifier.background(DividerGray))
    }
}
