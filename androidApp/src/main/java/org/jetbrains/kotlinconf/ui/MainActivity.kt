package org.jetbrains.kotlinconf.ui

import android.os.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.navigation.*
import androidx.navigation.ui.*
import com.mapbox.mapboxsdk.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.BuildConfig.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.storage.*
import java.io.*

class MainActivity : AppCompatActivity() {
    private lateinit var errorsWatcher: Closeable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = ApplicationContext(this, R.drawable.app_icon)
        KotlinConf.service = ConferenceService(context)

        errorsWatcher = KotlinConf.service.errors.watch { cause ->
            when (cause) {
                is Unauthorized -> showActivity<WelcomeActivity> {
                    putExtra("page", PrivacyPolicyFragment.name)
                }
                else -> {
                    Toast.makeText(this, cause.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        setContentView(R.layout.activity_main)
        setupNavigationBar()

        if (KotlinConf.service.isFirstLaunch()) {
//            showActivity<WelcomeActivity>()
        }
    }

    override fun onDestroy() {
        errorsWatcher.close()
        super.onDestroy()
    }

    private fun setupNavigationBar() {
        val controller = findNavController(R.id.nav_host_fragment)
        bottom_navigation.setupWithNavController(controller)

        Mapbox.getInstance(
            this, MAPBOX_ACCESS_TOKEN
        )
    }
}