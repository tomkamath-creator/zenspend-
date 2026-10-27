package com.vk.zenspend.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vk.zenspend.ui.screens.ZenSpendViewModel
import com.vk.zenspend.ui.theme.SageAccent
import com.vk.zenspend.ui.theme.ZenBottomSheetShape
import com.vk.zenspend.ui.theme.ZenColorBorder
import com.vk.zenspend.ui.theme.ZenColorSagePastel
import com.vk.zenspend.ui.theme.ZenColorSagePrimary
import com.vk.zenspend.ui.theme.ZenColorSurface
import com.vk.zenspend.ui.theme.ZenColorSurfaceVariant
import com.vk.zenspend.ui.theme.ZenPillShape
import com.vk.zenspend.ui.theme.ZenTextMuted
import com.vk.zenspend.ui.theme.ZenTextPrimary
import com.vk.zenspend.ui.theme.ZenTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingsModal(
    sheetState: SheetState,
    viewModel: ZenSpendViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importBackup(context, uri) { success, msg ->
                isError = !success
                statusMessage = msg
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = ZenBottomSheetShape,
        containerColor = ZenColorSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(ZenColorBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ZenColorSagePastel),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = ZenColorSagePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Backup & Restore",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary
                        )
                        Text(
                            text = "Export or restore all data on device",
                            fontSize = 12.sp,
                            color = ZenTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ZenTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Status feedback banner
            if (statusMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isError) Color(0xFFFEECEB) else ZenColorSagePastel
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = statusMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isError) Color(0xFFC53030) else ZenColorSagePrimary
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Export Option
            ZenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.exportBackup(context)
                        statusMessage = "Backup ready to export/save!"
                        isError = false
                    },
                elevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZenColorSagePastel),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export Backup",
                            tint = ZenColorSagePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Export Backup to Device",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZenTextPrimary
                        )
                        Text(
                            text = "Save all expenses, EMIs, MFs, and categories as a JSON file",
                            fontSize = 12.sp,
                            color = ZenTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Import Option
            ZenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        filePickerLauncher.launch("*/*")
                    },
                elevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ZenColorSurfaceVariant)
                            .border(1.dp, ZenColorBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Import Backup",
                            tint = SageAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import Backup from Device",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZenTextPrimary
                        )
                        Text(
                            text = "Select a previously exported ZenSpend backup file to restore",
                            fontSize = 12.sp,
                            color = ZenTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
