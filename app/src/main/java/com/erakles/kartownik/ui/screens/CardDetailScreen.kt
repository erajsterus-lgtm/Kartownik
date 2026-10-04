package com.erakles.kartownik.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erakles.kartownik.data.model.CardEntity
import com.erakles.kartownik.ui.components.BarcodeCardView
import com.erakles.kartownik.ui.viewmodel.CardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    cardId: Long,
    viewModel: CardViewModel,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var card by remember { mutableStateOf<CardEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Automatyczne rozjaśnienie ekranu do 100% (kluczowe przy kasie w sklepie!)
    DisposableEffect(Unit) {
        val activity = context as? Activity
        val window = activity?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

        window?.let {
            val layoutParams = it.attributes
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            it.attributes = layoutParams
        }

        onDispose {
            window?.let {
                val layoutParams = it.attributes
                layoutParams.screenBrightness = originalBrightness
                it.attributes = layoutParams
            }
        }
    }

    LaunchedEffect(cardId) {
        card = viewModel.getCardById(cardId)
    }

    if (showDeleteDialog && card != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usunąć kartę?") },
            text = { Text("Czy na pewno chcesz usunąć kartę ${card?.storeName}? Tej operacji nie można cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        card?.let { viewModel.deleteCard(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card?.storeName ?: "Karta lojalnościowa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                actions = {
                    card?.let { currentCard ->
                        IconButton(onClick = {
                            viewModel.toggleFavorite(currentCard)
                            card = currentCard.copy(isFavorite = !currentCard.isFavorite)
                        }) {
                            Icon(
                                imageVector = if (currentCard.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Ulubione",
                                tint = if (currentCard.isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(onClick = { onEditClick(cardId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edytuj kartę")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń kartę", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (card == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val c = card!!
            val bannerColor = try {
                Color(android.graphics.Color.parseColor(c.colorHex))
            } catch (_: Exception) {
                Color(0xFF263238)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Kolorowy nagłówek z nazwą
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = bannerColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = c.storeName,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (c.note.isNotBlank()) {
                            Text(
                                text = c.note,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Generowany kod kreskowy do skanowania na kasie
                BarcodeCardView(
                    cardNumber = c.cardNumber,
                    barcodeFormat = c.barcodeFormat
                )

                // Przyciski akcji
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Numer karty", c.cardNumber)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Numer skopiowany do schowka", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kopiuj numer karty")
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "💡 Wskazówka: Ekran został automatycznie rozjaśniony do 100%, aby skaner kasowy bez problemu odczytał kod.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}
