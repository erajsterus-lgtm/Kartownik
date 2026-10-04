package com.erakles.kartownik.data.model

data class PredefinedStore(
    val name: String,
    val defaultColorHex: String,
    val defaultBarcodeFormat: String
)

object StorePresets {
    val STORES = listOf(
        PredefinedStore("Biedronka", "#E2001A", "CODE_128"),
        PredefinedStore("Lidl", "#0050AA", "QR_CODE"),
        PredefinedStore("Rossmann", "#E30613", "EAN_13"),
        PredefinedStore("Hebe", "#E6007E", "CODE_128"),
        PredefinedStore("Żabka", "#007A33", "CODE_128"),
        PredefinedStore("Carrefour", "#004F9F", "EAN_13"),
        PredefinedStore("Auchan", "#E2001A", "EAN_13"),
        PredefinedStore("IKEA Family", "#0058A3", "CODE_128"),
        PredefinedStore("Kaufland", "#D40511", "CODE_128"),
        PredefinedStore("Castorama", "#004B93", "CODE_128"),
        PredefinedStore("Leroy Merlin", "#78BE20", "CODE_128"),
        PredefinedStore("Media Expert", "#FFED00", "CODE_128"),
        PredefinedStore("RTV Euro AGD", "#E30613", "CODE_128"),
        PredefinedStore("Empik", "#1E1E1E", "CODE_128"),
        PredefinedStore("CCC", "#E30613", "CODE_128"),
        PredefinedStore("Decathlon", "#0082C3", "CODE_128"),
        PredefinedStore("Action", "#001E62", "CODE_128"),
        PredefinedStore("Pepco", "#002B49", "CODE_128"),
        PredefinedStore("Orlen Vitay", "#D52B1E", "CODE_128"),
        PredefinedStore("Shell ClubSmart", "#FFD500", "CODE_128"),
        PredefinedStore("Inna karta", "#455A64", "CODE_128")
    )

    val COLOR_PALETTE = listOf(
        "#E53935", // Czerwony
        "#D81B60", // Różowy
        "#8E24AA", // Fioletowy
        "#3949AB", // Indygo
        "#1E88E5", // Niebieski
        "#00ACC1", // Cyjan
        "#00897B", // Morski
        "#43A047", // Zielony
        "#7CB342", // Limonkowy
        "#FB8C00", // Pomarańczowy
        "#6D4C41", // Brązowy
        "#37474F", // Grafitowy
        "#212121"  // Czarny
    )
}
