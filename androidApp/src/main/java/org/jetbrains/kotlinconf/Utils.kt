package org.jetbrains.kotlinconf

import android.content.*
import android.content.res.*
import android.graphics.Color
import android.net.*
import android.os.Build.VERSION_CODES.*
import android.provider.*
import android.text.*
import android.util.*
import android.view.View
import androidx.annotation.*
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

fun Context.getResourceId(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.resourceId
}

@ColorInt
fun View.color(@ColorRes attribute: Int): Int = ContextCompat.getColor(context, attribute)

fun Context.getHtmlText(resId: Int): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= N) {
        Html.fromHtml(getText(resId).toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(getText(resId).toString())
    }
}

val Context.connectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

val Context.isConnected: Boolean?
    get() = connectivityManager?.activeNetworkInfo?.isConnected

val Context.isAirplaneModeOn: Boolean
    @RequiresApi(JELLY_BEAN_MR1)
    get() = try {
        Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    } catch (error: Throwable) {
        false
    }

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()
