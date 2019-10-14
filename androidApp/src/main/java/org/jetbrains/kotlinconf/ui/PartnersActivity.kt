package org.jetbrains.kotlinconf.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_partners.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.showActivity

class PartnersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_partners)

        listOf(
            button_android,
            button_47,
            button_bitrise,
            button_freenow,
            button_instill,
            button_gradle,
            button_n26,
            button_kodein
        ).forEach {
            it.setOnClickListener {
                showActivity<PartnerActivity> {
                    putExtra("partner", it.tag.toString())
                }
            }
        }
    }
}