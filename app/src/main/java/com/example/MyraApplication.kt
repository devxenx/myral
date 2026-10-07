package com.example

import android.app.Application
import com.example.core.database.MyraDatabase

class MyraApplication : Application() {
    val database: MyraDatabase by lazy {
        MyraDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
