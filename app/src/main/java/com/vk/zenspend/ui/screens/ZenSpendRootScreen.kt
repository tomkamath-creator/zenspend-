package com.vk.zenspend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.ui.components.VkBadge
import com.vk.zenspend.ui.components.ZenCard
import com.vk.zenspend.ui.theme.ApricotCaution
import com.vk.zenspend.ui.theme.ApricotPastel
import com.vk.zenspend.ui.theme.LavenderPastel
import com.vk.zenspend.ui.theme.LavenderPastelBg
import com.vk.zenspend.ui.theme.MintPastel
import com.vk.zenspend.ui.theme.MintPastelBg
import com.vk.zenspend.ui.theme.PorcelainBg
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.SagePastel
import com.vk.zenspend.ui.theme.SagePrimary
import com.vk.zenspend.ui.theme.SkyBluePastel
import com.vk.zenspend.ui.theme.SkyBluePastelBg
import com.vk.zenspend.ui.theme.TerracottaAlert
import com.vk.zenspend.ui.theme.TerracottaPastel
import com.vk.zenspend.ui.theme.ZenMotion
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

@Composable
fun ZenSpendRootScreen(
    viewModel: ZenSpendViewModel
) {
    val categories by viewModel.categories.collectAsState()
    var isSpringTapped by remember { mutableStateOf(false) }

    val springScale by animateFloatAsState(
        targetValue = if (isSpringTapped) 1.05f else 1.0f,
        animationSpec = ZenMotion.DefaultSpring,
        label = "springBounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PorcelainBg)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ZenSpend",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Calm, effortless money management",
                            fontSize = 14.sp,
                            color = ZenTextSecondary
                        )
                    }
                    VkBadge()
                }
            }

            // Phase 1 Foundation Card
            item {
                ZenCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(springScale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isSpringTapped = !isSpringTapped
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SagePastel),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = SagePrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Phase 1: Architecture Ready",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZenTextPrimary
                            )
                            Text(
                                text = "Tap for spring motion check",
                                fontSize = 13.sp,
                                color = ZenTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    StatusItem(title = "Room Database Engine", detail = "Transaction, Category, Budget, Debt tables ready")
                    StatusItem(title = "Hilt Dependency Injection", detail = "KSP & Singleton bindings active")
                    StatusItem(title = "Zen Scandinavian Theme", detail = "Porcelain #F8F9FA, 24dp radii & pastels")
                    StatusItem(title = "Offline-First Guarantee", detail = "Zero tracking, 100% private SQLite storage")
                }
            }

            // Design Tokens & Mood Palette
            item {
                Text(
                    text = "Mood-Tuned Color Palette",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { ColorTokenChip("Safe Budget", SageAccent, SagePastel) }
                    item { ColorTokenChip("Caution (75%)", ApricotCaution, ApricotPastel) }
                    item { ColorTokenChip("Limit Alert", TerracottaAlert, TerracottaPastel) }
                    item { ColorTokenChip("Mindful", MintPastel, MintPastelBg) }
                    item { ColorTokenChip("Rituals", LavenderPastel, LavenderPastelBg) }
                    item { ColorTokenChip("Transit", SkyBluePastel, SkyBluePastelBg) }
                }
            }

            // Reactive Categories from Room Flow
            item {
                Text(
                    text = "Mindful Preloaded Categories (${categories.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTextPrimary
                )
            }

            if (categories.isEmpty()) {
                item {
                    ZenCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Initializing default categories in SQLite...",
                            fontSize = 14.sp,
                            color = ZenTextSecondary
                        )
                    }
                }
            } else {
                items(categories) { category ->
                    CategoryItemRow(category)
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun StatusItem(title: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SageAccent,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTextPrimary
            )
            Text(
                text = detail,
                fontSize = 12.sp,
                color = ZenTextSecondary
            )
        }
    }
}

@Composable
private fun ColorTokenChip(label: String, color: Color, bg: Color) {
    Row(
        modifier = Modifier
            .clip(ZenPillShape)
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ZenTextPrimary
        )
    }
}

@Composable
private fun CategoryItemRow(category: Category) {
    ZenCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(category.colorHex).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(category.colorHex))
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTextPrimary
                )
                Text(
                    text = "Type: ${category.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = ZenTextMuted
                )
            }
        }
    }
}
