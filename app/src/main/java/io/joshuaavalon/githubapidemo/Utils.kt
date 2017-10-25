package io.joshuaavalon.githubapidemo

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat

/**
 * Open an in-app tab if the user installed Chrome.
 * If Chrome is not installed, it will open normally as a web page.
 */
fun Context.openUrl(url: String) {
    CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
            .build()
            .launchUrl(this, Uri.parse(url))
}