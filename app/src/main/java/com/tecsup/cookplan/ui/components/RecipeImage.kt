package com.tecsup.cookplan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.tecsup.cookplan.data.local.RecipeImageCatalog

@Composable
fun RecipeImage(
    imageRef: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val localImage = RecipeImageCatalog.byKey(imageRef)
    when {
        localImage != null -> {
            Image(
                painter = painterResource(localImage.resId),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        }

        !imageRef.isNullOrBlank() -> {
            AsyncImage(
                model = imageRef,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        }

        else -> {
            Box(
                modifier = modifier.background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
