package uz.m1nex.testfirebaseauth

import android.content.Context
import androidx.core.content.edit

class Pref private constructor(context: Context){
    fun clear() {
        pref.edit { clear() }
    }

    companion object {
        private lateinit var pref: Pref
        fun init(context: Context) {
            pref = Pref(context)
        }
        fun getInstance() = pref
    }
    private val pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)

    var isLogin
        get() = pref.getBoolean("isLogin", false)
        set(value) { pref.edit { putBoolean("isLogin", value) } }


    var email
        get() = pref.getString("email", "")
        set(value) { pref.edit { putString("email", value) } }

    var password
        get() = pref.getString("password", "")
        set(value) { pref.edit { putString("password", value) } }


    var gmail
        get() = pref.getString("gmail", "")
        set(value) { pref.edit { putString("gmail", value) } }

    var name
        get() = pref.getString("name", "")
        set(value) { pref.edit { putString("name", value) } }


    var github
        get() = pref.getString("github", "")
        set(value) { pref.edit { putString("github", value) } }

    var photoUrl
        get() = pref.getString("photoUrl", "")
        set(value) { pref.edit { putString("photoUrl", value) } }

}