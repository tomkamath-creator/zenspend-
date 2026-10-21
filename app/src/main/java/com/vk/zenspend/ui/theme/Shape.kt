package com.vk.zenspend.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ZenShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp), // Signature 24.dp soft Zen corners
    extraLarge = RoundedCornerShape(28.dp)
)

val ZenPillShape = RoundedCornerShape(percent = 50)
val ZenCardShape = RoundedCornerShape(24.dp)
val ZenBottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
