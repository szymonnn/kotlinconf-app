package org.jetbrains.kotlinconf

import android.app.*
import android.content.*
import android.support.multidex.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.storage.*
import java.util.*

class KotlinConfApplication : Application(), AnkoLogger {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun getUserId(): String = "android-" + UUID.randomUUID().toString()
}