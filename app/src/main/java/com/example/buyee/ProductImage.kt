package com.example.buyee

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation

@Composable
fun ProductImage(url: String, modifier: Modifier = Modifier) {
    Image(
        painter = rememberImagePainter(
            data = url,
            builder = {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
