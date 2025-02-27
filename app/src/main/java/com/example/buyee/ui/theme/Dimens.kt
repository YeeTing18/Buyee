package com.example.buyee.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val extraSmall : Dp = 0.dp,
    val small1 : Dp = 0.dp,
    val small2 : Dp = 0.dp,
    val small3 : Dp = 0.dp,
    val medium1 : Dp = 0.dp,
    val medium2 : Dp = 0.dp,
    val medium3 : Dp = 0.dp,
    val large : Dp = 0.dp,
    val buttonHeight : Dp = 40.dp,
    val logoSize: Dp = 42.dp
)

val CompactDimens = Dimens(
    small1 = 10.dp,
    small2 = 15.dp,
    small3 = 20.dp,
    medium1 = 30.dp,
    medium2 = 36.dp,
    medium3 = 40.dp,
    large = 80.dp
)

val CompactSmallDimens = Dimens(
    small1 = 6.dp,
    small2 = 10.dp,
    small3 = 50.dp,
    medium1 = 30.dp,
    medium2 = 130.dp,
    medium3 = 100.dp,
    large = 330.dp,
    buttonHeight = 30.dp,
    logoSize = 40.dp
)

val MediumDimens = Dimens(
    small1 = 8.dp,
    small2 = 13.dp,
    small3 = 17.dp,
    medium1 = 25.dp,
    medium2 = 30.dp,
    medium3 = 35.dp,
    large = 65.dp,
)

val CompactMediumDimens = Dimens(
    small1 = 10.dp,
    small2 = 24.dp,
    small3 = 64.dp,
    medium1 = 100.dp,
    medium2 = 280.dp,
    medium3 = 160.dp,
    large = 380.dp,
    logoSize = 80.dp
)

val ExpandedDimens = Dimens(
    small1 = 15.dp,
    small2 = 20.dp,
    small3 = 25.dp,
    medium1 = 30.dp,
    medium2 = 36.dp,
    medium3 = 45.dp,
    large = 130.dp,
    logoSize = 72.dp
)
