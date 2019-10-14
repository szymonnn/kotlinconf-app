package org.jetbrains.kotlinconf

class Partner(val title: String, val url: String)

object Partners {
    private val all = listOf(
        "android",
        "47",
        "freenow",
        "bitrise",
        "instill",
        "gradle",
        "n26",
        "kodein",
        "data2viz",
        "pleo",
        "shape",
        "touchlab",
        "cash",
        "bignerdranch",
        "manning",
        "pretix",
        "stickermule"
    )

    private val partners = mapOf(
        "android" to Partner("Android", "https://developer.android.com/"),
        "47" to Partner("47 degrees", "https://www.47deg.com"),
        "freenow" to Partner("FREE NOW", "https://free-now.com/"),
        "bitrise" to Partner("Bitrise", "https://www.bitrise.io/"),
        "instill" to Partner("Instill", "https://www.instil.co"),
        "gradle" to Partner("Gradle", "https://gradle.com/"),
        "n26" to Partner("N26", "https://n26.com/"),
        "kodein" to Partner("Kodein", "https://kodein.net"),
        "data2viz" to Partner("Data2Viz", "https://data2viz.io/"),
        "pleo" to Partner("Pleo", "https://www.pleo.io"),
        "shape" to Partner("Shape", "https://www.shape.dk/"),
        "touchlab" to Partner("Touchlab", "https://touchlab.co/"),
        "cash" to Partner("Cash", "https://cash.app/"),
        "bignerdranch" to Partner("", ""),
        "manning" to Partner("", ""),
        "pretix" to Partner("", ""),
        "stickermule" to Partner("", "")
    )

    private val descriptions = mutableMapOf(
        "android" to "Android is the world's most popular mobile platform with more than 2.5 billion monthly active devices worldwide. Development on the platform is increasingly becoming Kotlin-first. Learn more about writing Android apps faster with Kotlin. https://developer.android.com/kotlin",
        "47" to "47 Degrees is a global consulting firm specializing in enterprise platform modernization, microservices architectures, mobile application development, and big data solutions, all using proven functional programming expertise.",
        "freenow" to "",
        "bitrise" to "Bitrise helps you build and operate better apps, faster. By effortlessly combining services and tools mobile developers love, we make development easier, more scalable and take away the fear of change that causes development processes to stagnate.",
        "instill" to "Instil is a software engineering consultancy based in Belfast, UK. We specialise in the development of bespoke, business-critical software and developer training to clients globally.",
        "gradle" to "Gradle dramatically improves the software development process in terms of speed, reliability, and developer productivity to enable success in digital transformation.",
        "n26" to "N26 is Europe’s first Mobile Bank with a full European banking license. We have 3.5 million customers across 24 markets. Our team of over 1,300 employees in 4 locations is concentrated on reinventing the banking experience for the digital generation.",
        "kodein" to "Kodein Koders is the first startup in Europe to be entirely dedicated to Kotlin, anywhere Kotlin goes! From the ground up, we invested into making the Kodein Framework the first Open-Source Kotlin/Multiplatform Framework.",
        "data2viz" to "Data2viz is a company that creates libraries and tools for data-visualizations. We base our solutions on Kotlin, deeply convinced by the vast benefits it can provide to manipulate data and render the visualizations on different platforms: mobiles, web, and desktop.",
        "pleo" to "Pleo is a fundamentally new way to manage company expenses. Offering smart payment cards to employees, Pleo enables everyone to buy whatever they need for work, all the while making sure the company remains in full control of spending.",
        "shape" to "Shape is an award winning digital product studio. We are a team of 70+ devoted in-house developers, designers and strategists that combine innovation with digital craftsmanship to deliver lasting products for mobile and beyond.",
        "touchlab" to "",
        "cash" to "",
        "bignerdranch" to "",
        "manning" to "",
        "pretix" to "",
        "stickermule" to ""
    )

    fun partner(name: String): Partner = partners[name]!!

    fun descriptionByName(name: String): String = descriptions[name] ?: ""
}