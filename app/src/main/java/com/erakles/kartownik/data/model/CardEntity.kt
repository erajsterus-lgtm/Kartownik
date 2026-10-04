package com.erakles.kartownik.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loyalty_cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeName: String,
    val cardNumber: String,
    val barcodeFormat: String = "CODE_128", // np. QR_CODE, CODE_128, EAN_13, AZTEC
    val colorHex: String = "#E53935",
    val note: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
