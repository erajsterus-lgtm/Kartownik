package com.erakles.kartownik.ui.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erakles.kartownik.KartownikApp
import com.erakles.kartownik.data.model.CardEntity
import com.erakles.kartownik.widget.KartownikWidgetProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val cardDao = (application as KartownikApp).database.cardDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _favoriteOnly = MutableStateFlow(false)
    val favoriteOnly = _favoriteOnly.asStateFlow()

    val cards: StateFlow<List<CardEntity>> = combine(
        _searchQuery,
        _favoriteOnly
    ) { query, favOnly ->
        Pair(query, favOnly)
    }.flatMapLatest { (query, favOnly) ->
        if (query.isNotBlank()) {
            cardDao.searchCards(query)
        } else if (favOnly) {
            cardDao.getFavoriteCards()
        } else {
            cardDao.getAllCards()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleFavoriteFilter() {
        _favoriteOnly.value = !_favoriteOnly.value
    }

    fun addCard(
        storeName: String,
        cardNumber: String,
        barcodeFormat: String,
        colorHex: String,
        note: String
    ) {
        viewModelScope.launch {
            val card = CardEntity(
                storeName = storeName.trim(),
                cardNumber = cardNumber.trim(),
                barcodeFormat = barcodeFormat,
                colorHex = colorHex,
                note = note.trim()
            )
            cardDao.insertCard(card)
            notifyWidget()
        }
    }

    fun updateCard(card: CardEntity) {
        viewModelScope.launch {
            cardDao.updateCard(card)
            notifyWidget()
        }
    }

    fun toggleFavorite(card: CardEntity) {
        viewModelScope.launch {
            cardDao.updateCard(card.copy(isFavorite = !card.isFavorite))
            notifyWidget()
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            cardDao.deleteCard(card)
            notifyWidget()
        }
    }

    suspend fun getCardById(id: Long): CardEntity? {
        return cardDao.getCardById(id)
    }

    private fun notifyWidget() {
        try {
            val context = getApplication<Application>()
            val intent = Intent(context, KartownikWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(context, KartownikWidgetProvider::class.java)
                )
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        } catch (_: Exception) {
        }
    }
}
