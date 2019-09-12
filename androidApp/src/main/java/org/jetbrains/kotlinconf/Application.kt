package org.jetbrains.kotlinconf

import android.app.*
import android.content.*
import androidx.multidex.*

class KotlinConf : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    companion object {
        lateinit var service: ConferenceService
    }
}
