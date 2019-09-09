package org.jetbrains.kotlinconf.storage

actual fun ApplicationStorage(): ApplicationStorage = AndroidStorage()

internal class AndroidStorage(
//    context: Context
) : ApplicationStorage {
//    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun putBoolean(key: String, value: Boolean) {
//        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = defaultValue
//        sharedPreferences.getBoolean(key, defaultValue)

    override fun putString(key: String, value: String) {
//        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return null
//        return sharedPreferences.getString(key, null)
    }
}
