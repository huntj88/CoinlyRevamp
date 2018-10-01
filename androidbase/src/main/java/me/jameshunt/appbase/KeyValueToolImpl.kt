package me.jameshunt.appbase

import android.annotation.SuppressLint
import android.content.SharedPreferences
import me.jameshunt.base.KeyValueTool

class KeyValueToolImpl(private val sharedPrefs: SharedPreferences) : KeyValueTool {

    @SuppressLint("ApplySharedPref")
    override fun set(key: String, value: String) {
        sharedPrefs.edit().putString(key, value).commit()
    }

    override fun get(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

    @SuppressLint("ApplySharedPref")
    override fun remove(key: String) {
        sharedPrefs.edit().remove(key).commit()
    }
}
