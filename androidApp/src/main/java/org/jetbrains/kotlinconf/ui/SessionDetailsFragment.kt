package org.jetbrains.kotlinconf.ui

import android.os.*
import android.support.v7.app.*
import android.view.*
import android.widget.*
import com.bumptech.glide.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.ui.views.*

class SessionDetailsFragment : BaseFragment(), SessionDetailsView {
    private lateinit var session: Session
    private val sessionView: SessionView by lazy { SessionView(this) }

    private val repository by lazy { (activity!!.application as KotlinConfApplication).service }
    private val presenter by lazy { SessionDetailsPresenter(this, session, repository) }

    private val Session.timeString: String get() = (startsAt to endsAt).toReadableString()
    private val Session.roomText: String get() = "Room $room"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = sessionView.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpActionBar()

        with(sessionView) {
            favoriteButton.setOnClickListener { presenter.onFavoriteButtonClicked() }
            goodButton.setOnClickListener { presenter.onRatingButtonClicked(RatingData.GOOD) }
            okButton.setOnClickListener { presenter.onRatingButtonClicked(RatingData.OK) }
            badButton.setOnClickListener { presenter.onRatingButtonClicked(RatingData.BAD) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun setRatingClickable(clickable: Boolean) {
        with(sessionView) {
            goodButton.isClickable = clickable
            okButton.isClickable = clickable
            badButton.isClickable = clickable
        }
    }

    private fun setUpActionBar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(sessionView.toolbar)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayUseLogoEnabled(false)
        }
    }

    private fun ImageView.showSpeakerImage(imageUrl: String) {
        visibility = View.VISIBLE
        Glide.with(this@SessionDetailsFragment)
            .load(imageUrl)
            .centerCrop()
            .into(this)
    }


    override fun updateFavorite(isFavorite: Boolean) {
    }

    override fun updateRating(rating: RatingData?) {
        fun selectButton(target: RatingData): Int = when (rating) {
            target -> R.drawable.round_toggle_button_background_selected
            else -> R.drawable.round_toggle_button_background
        }

        with(sessionView) {
            goodButton.backgroundResource = selectButton(RatingData.GOOD)
            okButton.backgroundResource = selectButton(RatingData.OK)
            badButton.backgroundResource = selectButton(RatingData.BAD)
        }
    }

    fun updateSession(session: Session) {
        with(sessionView) {
            collapsingToolbar.title = session.title
            speakersTextView.text = session.speakers.joinToString(separator = ", ") { it.fullName }

            timeTextView.text = session.timeString
            detailsTextView.text = (session.tags + session.roomText)
                .joinToString(", ")

            descriptionTextView.text = session.descriptionText

            val online = context?.let { it.isConnected?.and(!it.isAirplaneModeOn) } ?: false
            for (button in listOf(votingButtonsLayout, favoriteButton)) {
                button.visibility = if (online) View.VISIBLE else View.GONE
            }

            val favoriteIcon = if (session.isFavorite) {
                R.drawable.ic_favorite_white_24dp
            } else {
                R.drawable.ic_favorite_border_white_24dp
            }

            favoriteButton.setImageResource(favoriteIcon)

            session.speakers
                .takeIf { it.size < 3 }
                ?.map { it.profilePicture }
                ?.apply {
                    forEachIndexed { index, imageUrl ->
                        imageUrl?.let { speakerImageViews[index].showSpeakerImage(it) }
                    }
                }
        }
    }

    companion object {
        const val TAG = "SessionDetailsFragment"
        private const val KEY_SESSION_ID = "SessionId"

        fun forSession(session: Session): SessionDetailsFragment = SessionDetailsFragment().apply {
            arguments = Bundle().apply { putString(KEY_SESSION_ID, session.id) }
        }
    }
}