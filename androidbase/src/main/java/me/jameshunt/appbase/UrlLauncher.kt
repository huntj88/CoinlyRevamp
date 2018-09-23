package me.jameshunt.appbase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat


interface UrlLauncher {
    fun launchUrl(url: String)
}

class UrlLauncherImpl(private val context: Context): UrlLauncher {
    override fun launchUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(context.packageManager) != null) {
            ContextCompat.startActivity(context, intent, null)
        }
    }
}