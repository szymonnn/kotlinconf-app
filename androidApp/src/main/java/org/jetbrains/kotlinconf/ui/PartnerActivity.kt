package org.jetbrains.kotlinconf.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_partner.*
import android.content.Intent
import android.net.Uri
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*


class PartnerActivity : AppCompatActivity() {

    private val logos = mapOf(
        "android" to R.drawable.logo_android,
        "47" to R.drawable.logo_47,
        "freenow" to R.drawable.logo_freenow,
        "bitrise" to R.drawable.logo_bitrise,
        "instill" to R.drawable.logo_instl,
        "gradle" to R.drawable.logo_gradle,
        "n26" to R.drawable.logo_n_26,
        "kodein" to R.drawable.logo_kodein,
        "data2viz" to R.drawable.logo_data_2_viz,
        "pleo" to R.drawable.logo_pleo,
        "shape" to R.drawable.logo_shape,
        "touchlab" to R.drawable.touchlab_logo,
        "cash" to R.drawable.logo_cash,
        "bignerdranch" to R.drawable.logo_bignerdranch,
        "manning" to R.drawable.logo_manning,
        "pretix" to R.drawable.logo_pretix,
        "stickermule" to R.drawable.logo_stickermule
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        val partnerName = intent.getStringExtra("partner") ?: return
        displayPartner(partnerName)
    }

    private fun displayPartner(name: String) {
        logos[name]?.let {
            partner_logo.setImageResource(it)
        }

        val partner = Partners.partner(name)
        partner_name.text = partner.title
        partner_description.text = Partners.descriptionByName(name)
        partner_link.text = partner.url

        partner_link.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(partner.url))
            startActivity(browserIntent)
        }
    }
}
