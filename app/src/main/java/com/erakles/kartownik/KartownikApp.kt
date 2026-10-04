package com.erakles.kartownik

import android.app.Application
import com.erakles.kartownik.data.local.AppDatabase

class KartownikApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: KartownikApp
            private set
    }
}
