package ru.korobeynikov.newsapplication.di

import android.app.Application

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().newsModule(NewsModule(this)).build()
    }
}