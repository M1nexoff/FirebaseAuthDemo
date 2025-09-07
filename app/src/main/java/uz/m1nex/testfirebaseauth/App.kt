package uz.m1nex.testfirebaseauth

import android.app.Application

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Pref.init(this)
    }
}