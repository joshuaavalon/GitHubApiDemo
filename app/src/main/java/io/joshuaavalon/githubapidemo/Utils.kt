package io.joshuaavalon.githubapidemo

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat


fun Context.openUrl(url: String) {
    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    builder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
    builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
    builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}