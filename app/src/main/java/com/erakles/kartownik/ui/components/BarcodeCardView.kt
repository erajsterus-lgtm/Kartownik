package com.erakles.kartownik.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erakles.kartownik.utils.BarcodeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BarcodeCardView(
    cardNumber: String,
    barcodeFormat: String,
    modifier: Modifier = Modifier
) {
    var barcodeBitmap by remember(cardNumber, barcodeFormat) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(cardNumber, barcodeFormat) { mutableStateOf(true) }

    LaunchedEffect(cardNumber, barcodeFormat) {
        isLoading = true
        barcodeBitmap = withContext(Dispatchers.Default) {
            BarcodeUtils.generateBarcodeBitmap(
                content = cardNumber,
                formatName = barcodeFormat,
                width = 900,
                height = 360
            )
        }
        isLoading = false
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else if (barcodeBitmap != null) {
                val isQrOrAztec = barcodeFormat == "QR_CODE" || barcodeFormat == "AZTEC"
                Image(
                    bitmap = barcodeBitmap!!.asImageBitmap(),
                    contentDescription = "Kod kreskowy",
                    modifier = Modifier
                        .fillMaxWidth(if (isQrOrAztec) 0.75f else 1f)
                        .height(if (isQrOrAztec) 220.dp else 140.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nie udało się wygenerować kodu kreskowego",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Numer karty
            Text(
                text = cardNumber,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF1E1E1E),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Format: $barcodeFormat",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
