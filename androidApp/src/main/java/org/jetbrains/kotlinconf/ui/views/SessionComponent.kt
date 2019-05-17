package org.jetbrains.kotlinconf.ui.views

import android.graphics.*
import android.graphics.drawable.*
import android.support.design.widget.*
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.v4.app.*
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.R

class SessionView(
    fragment: Fragment
) {
    internal lateinit var collapsingToolbar: CollapsingToolbarLayout
    internal lateinit var toolbar: Toolbar
    internal lateinit var speakersTextView: TextView
    internal lateinit var timeTextView: TextView
    internal lateinit var detailsTextView: TextView
    internal lateinit var descriptionTextView: TextView
    internal lateinit var favoriteButton: FloatingActionButton
    internal lateinit var votingButtonsLayout: LinearLayout

    internal lateinit var goodButton: ImageButton
    internal lateinit var badButton: ImageButton
    internal lateinit var okButton: ImageButton

    internal val speakerImageViews: MutableList<ImageView> = mutableListOf()

    internal val root: View? = fragment.UI {
        coordinatorLayout {
            lparams(width = matchParent, height = matchParent)
            themedAppBarLayout(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                id = R.id.app_bar_layout
                collapsingToolbar = multilineCollapsingToolbarLayout {
                    contentScrim = ColorDrawable(theme.getColor(R.attr.colorPrimary))
                    maxLines = 5
                    expandedTitleMarginStart = dip(20)
                    expandedTitleMarginEnd = dip(20)
                    setExpandedTitleTextAppearance(R.style.SessionTitleExpanded)

                    linearLayout {
                        layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, matchParent).apply {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }

                        imageView {
                            visibility = View.GONE
                        }.lparams(width = 0, height = matchParent) {
                            weight = 0.5f
                        }.also { speakerImageViews.add(it) }

                        imageView {
                            visibility = View.GONE
                        }.lparams(width = 0, height = matchParent) {
                            weight = 0.5f
                        }.also { speakerImageViews.add(it) }
                    }

                    view {
                        backgroundResource = R.drawable.appbar_buttons_scrim
                        layoutParams = CollapsingToolbarLayout.LayoutParams(
                            matchParent,
                            dimen(context.getResourceId(R.attr.actionBarSize))
                        ).apply {
                            gravity = Gravity.TOP
                        }
                    }

                    view {
                        backgroundResource = R.drawable.appbar_title_scrim
                        layoutParams = CollapsingToolbarLayout.LayoutParams(matchParent, matchParent).apply {
                            gravity = Gravity.BOTTOM
                        }
                    }

                    toolbar = toolbar {
                        layoutParams = CollapsingToolbarLayout.LayoutParams(
                            matchParent,
                            dimen(context.getResourceId(R.attr.actionBarSize))
                        ).apply {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
                        }
                    }
                }.lparams(width = matchParent, height = matchParent) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                }
            }.lparams(width = matchParent, height = dip(256))

            favoriteButton = floatingActionButton().lparams {
                anchorId = R.id.app_bar_layout
                anchorGravity = Gravity.BOTTOM or Gravity.END
                margin = dip(8)
            }

            nestedScrollView {
                verticalLayout {
                    speakersTextView = textView {
                        textSize = 26f
                        textColor = Color.BLACK
                    }.lparams {
                        bottomMargin = dip(6)
                    }

                    timeTextView = textView {
                        textSize = 17f
                    }.lparams {
                        bottomMargin = dip(4)
                    }

                    detailsTextView = textView {
                        textSize = 17f
                    }

                    descriptionTextView = textView {
                        textSize = 19f
                    }.lparams {
                        topMargin = dip(20)
                    }

                    votingButtonsLayout = linearLayout {
                        goodButton = imageButton {
                            imageResource = R.drawable.ic_happy
                        }
                        okButton = imageButton {
                            imageResource = R.drawable.ic_neutral
                        }
                        badButton = imageButton {
                            imageResource = R.drawable.ic_sad
                        }
                    }.lparams {
                        topMargin = dip(10)
                        bottomMargin = dip(80)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.applyRecursively { view ->
                        when (view) {
                            is ImageButton -> {
                                view.lparams {
                                    width = dip(56)
                                    height = dip(56)
                                    gravity = Gravity.CENTER_VERTICAL
                                    margin = dip(10)
                                }
                            }
                        }
                    }

                }.lparams(width = matchParent, height = wrapContent) {
                    margin = dip(20)
                }.applyRecursively { view ->
                    // Making a Button widget selectable causes problems with clicking
                    // (Button class extends TextView)
                    if (view is Button) return@applyRecursively
                    (view as? TextView)?.setTextIsSelectable(true)
                }

            }.lparams(width = matchParent, height = matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }.view

}