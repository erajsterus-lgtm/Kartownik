package com.erakles.kartownik.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.erakles.kartownik.data.model.StorePresets
import com.erakles.kartownik.ui.components.BarcodeCardView
import com.erakles.kartownik.ui.components.CameraBarcodeScanner
import com.erakles.kartownik.ui.viewmodel.CardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    viewModel: CardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var storeName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var barcodeFormat by remember { mutableStateOf("CODE_128") }
    var selectedColor by remember { mutableStateOf(StorePresets.COLOR_PALETTE[0]) }
    var note by remember { mutableStateOf("") }

    var isScanningCamera by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isScanningCamera = true
        }
    }

    if (isScanningCamera) {
        CameraBarcodeScanner(
            onBarcodeScanned = { code, format ->
                cardNumber = code
                barcodeFormat = format
                isScanningCamera = false
            },
            onDismiss = {
                isScanningCamera = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj nową kartę", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Przycisk skanera
            Button(
                onClick = {
                    val hasCam = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasCam) {
                        isScanningCamera = true
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zeskanuj kartę aparatem", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            // Presety popularnych sklepów
            Text(
                text = "Wybierz sklep lub wpisz własny:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(StorePresets.STORES) { preset ->
                    val isSelected = storeName.equals(preset.name, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            storeName = preset.name
                            selectedColor = preset.defaultColorHex
                            barcodeFormat = preset.defaultBarcodeFormat
                        },
                        label = { Text(preset.name) }
                    )
                }
            }

            // Nazwa sklepu
            OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("Nazwa sklepu / programu") },
                placeholder = { Text("np. Biedronka, Rossmann...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Numer karty
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Numer karty / kod kreskowy") },
                placeholder = { Text("np. 1234567890123") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Wybór formatu kodu
            val formats = listOf("CODE_128", "EAN_13", "QR_CODE", "CODE_39", "PDF_417", "AZTEC")
            Text(
                text = "Format kodu:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(formats) { fmt ->
                    FilterChip(
                        selected = barcodeFormat == fmt,
                        onClick = { barcodeFormat = fmt },
                        label = { Text(fmt) }
                    )
                }
            }

            // Wybór koloru
            Text(
                text = "Kolor karty:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(StorePresets.COLOR_PALETTE) { hexColor ->
                    val color = Color(android.graphics.Color.parseColor(hexColor))
                    val isChosen = selectedColor.equals(hexColor, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = hexColor }
                            .then(
                                if (isChosen) Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChosen) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Notatka
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notatka (opcjonalnie)") },
                placeholder = { Text("np. Karta żony, karta do punktów") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Podgląd na żywo
            if (cardNumber.isNotBlank()) {
                Text(
                    text = "Podgląd karty:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BarcodeCardView(
                    cardNumber = cardNumber,
                    barcodeFormat = barcodeFormat
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Przycisk Zapisu
            Button(
                onClick = {
                    if (storeName.isNotBlank() && cardNumber.isNotBlank()) {
                        viewModel.addCard(
                            storeName = storeName,
                            cardNumber = cardNumber,
                            barcodeFormat = barcodeFormat,
                            colorHex = selectedColor,
                            note = note
                        )
                        onNavigateBack()
                    }
                },
                enabled = storeName.isNotBlank() && cardNumber.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Zapisz kartę w portfelu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
