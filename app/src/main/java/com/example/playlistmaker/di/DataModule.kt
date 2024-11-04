package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<SharedPreferences> {
        get<Context>().getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single { SearchHistory(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl(get()) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}
