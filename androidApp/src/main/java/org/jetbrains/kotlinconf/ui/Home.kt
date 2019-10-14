package org.jetbrains.kotlinconf.ui

import android.os.*
import android.view.*
import androidx.core.view.*
import androidx.fragment.app.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.*
import com.google.android.youtube.player.*
import io.ktor.util.date.*
import io.ktor.utils.io.core.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_before.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.view_dont_miss_card.view.*
import kotlinx.android.synthetic.main.view_session_live_card.view.*
import kotlinx.android.synthetic.main.view_tweet_card.view.*
import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.BuildConfig.*
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.presentation.*
import java.util.concurrent.*
import kotlin.time.*

class HomeController : Fragment() {
    private val liveCards by lazy { LiveCardsAdapter() }
    private val reminders by lazy { RemaindersAdapter() }
    private val feed by lazy { FeedAdapter() }

    private lateinit var liveWatcher: Closeable
    private lateinit var remindersWatcher: Closeable
    private lateinit var feedWatcher: Closeable
    private var timer: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        liveWatcher = KotlinConf.service.liveSessions.watch {
            liveCards.data = it
            liveCards.notifyDataSetChanged()

            val visible = it.isNotEmpty()
            live_title?.isVisible = visible
            live_cards_container?.isVisible = visible
        }

        remindersWatcher = KotlinConf.service.upcomingFavorites.watch {
            reminders.data = it
            reminders.notifyDataSetChanged()

            val visible = it.isNotEmpty()
            dont_miss_title?.isVisible = visible
            dont_miss_block?.isVisible = visible
        }

        feedWatcher = KotlinConf.service.feed.watch {
            feed.data = it.statuses
            feed.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        liveWatcher.close()
        remindersWatcher.close()
        feedWatcher.close()
        timer?.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        if (KotlinConf.service.now() >= CONFERENCE_START) {
            return inflater.inflate(R.layout.fragment_home, container, false).apply {
                setupLiveCards()
                setupRemainders()
                setupTwitter()
                setupPartners()
            }
//        }
        return inflater.inflate(R.layout.fragment_before, container, false).apply {
            setupTimer()
        }
    }

    private fun View.setupLiveCards() {
        live_cards_container.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                isNestedScrollingEnabled = true
            }
            adapter = liveCards
        }
    }

    @UseExperimental(ExperimentalTime::class)
    private fun View.setupTimer() {
        timer = GlobalScope.launch(Dispatchers.Main) {
            val now = KotlinConf.service.now()

            val diff = (CONFERENCE_START.timestamp - now.timestamp) / 1000
            var time = diff.toDuration(DurationUnit.SECONDS)

            while (time.inSeconds >= 0) {
                time.toComponents { days, hours, minutes, seconds, nanoseconds ->
                    before_days.text = days.toString()
                    before_hours.text = hours.toString()
                    before_minutes.text = minutes.toString()
                    before_seconds.text = seconds.toString()
                }

                delay(1000)
                time -= 1.toDuration(TimeUnit.SECONDS)
            }
        }
    }

    private fun View.setupRemainders() {
        dont_miss_block.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = reminders
        }
    }

    private fun View.setupTwitter() {
        tweet_feed_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                isNestedScrollingEnabled = true
            }
            adapter = feed
        }
    }

    private fun View.setupPartners() {
        show_partners.setOnClickListener {
            showActivity<PartnersActivity>()
        }
    }

    inner class LiveCardsAdapter(
        var data: List<SessionCard> = emptyList()
    ) : RecyclerView.Adapter<LiveCardHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveCardHolder {
            val view = layoutInflater.inflate(
                R.layout.view_session_live_card, parent, false
            ).apply {
                live_video_view.initialize(YOUTUBE_API_KEY, LiveCardHolder)
            }

            return LiveCardHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: LiveCardHolder, position: Int) {
            holder.setupCard(data[position])
        }
    }

    inner class RemaindersAdapter(
        var data: List<SessionCard> = emptyList()
    ) : RecyclerView.Adapter<RemainderHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainderHolder {
            val view = layoutInflater.inflate(R.layout.view_dont_miss_card, parent, false)
            return RemainderHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: RemainderHolder, position: Int) {
            holder.setupCard(data[position])
        }
    }

    inner class FeedAdapter(
        var data: List<FeedPost> = emptyList()
    ) : RecyclerView.Adapter<TweetHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetHolder {
            val view = layoutInflater.inflate(R.layout.view_tweet_card, parent, false)
            return TweetHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: TweetHolder, position: Int) {
            holder.showPost(data[position])
        }
    }
}

class LiveCardHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private var favoriteSubscription: Closeable? = null
    private var liveSubscription: Closeable? = null

    fun setupCard(sessionCard: SessionCard) {
        favoriteSubscription?.close()
        liveSubscription?.close()

        with(view) {
            setOnTouchListener { view, event ->
                val action = event.action

                view.live_card.setPressedColor(
                    event,
                    R.color.dark_grey_card,
                    R.color.dark_grey_card_pressed
                )
                if (action == MotionEvent.ACTION_UP) {
                    showActivity<SessionActivity> {
                        putExtra("session", sessionCard.session.id)
                    }
                }

                true
            }

            liveSubscription = sessionCard.isLive.watch {
                live_video_view.tag = it
            }
            live_session_title.text = sessionCard.session.title
            live_session_speakers.text = sessionCard.speakers.joinToString { it.fullName }
            live_location.text = sessionCard.location.displayName()
            live_favorite.setOnClickListener {
                KotlinConf.service.markFavorite(sessionCard.session.id)
            }

            live_time.text = "${KotlinConf.service.minutesLeft(sessionCard)} minutes left"

            favoriteSubscription = sessionCard.isFavorite.watch {
                val image = if (it) {
                    R.drawable.favorite_orange
                } else {
                    R.drawable.favorite_white_empty
                }

                live_favorite.setImageResource(image)
            }
        }
    }

    companion object ThumbnailListener : YouTubeThumbnailView.OnInitializedListener {
        override fun onInitializationFailure(
            view: YouTubeThumbnailView,
            loader: YouTubeInitializationResult
        ) {
        }

        override fun onInitializationSuccess(
            view: YouTubeThumbnailView,
            loader: YouTubeThumbnailLoader
        ) {
            val tag = view.tag as? String ?: return
            loader.setVideo(tag)
        }
    }
}

class RemainderHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private var favoriteSubscription: Closeable? = null

    fun setupCard(sessionCard: SessionCard) {
        favoriteSubscription?.close()

        with(view) {
            setOnTouchListener { view, event ->
                view.speaker_card_remainder.setPressedColor(
                    event,
                    R.color.dark_grey_card,
                    R.color.dark_grey_card_pressed
                )

                if (event.action == MotionEvent.ACTION_UP) {
                    showActivity<SessionActivity> {
                        putExtra("session", sessionCard.session.id)
                    }
                }
                true
            }

            card_session_title.text = sessionCard.session.title
            card_session_speakers.text = sessionCard.speakers.joinToString { it.fullName }
            card_location_label.text = sessionCard.location.displayName()

            card_live_label.isVisible = false
            card_live_icon.isVisible = false

            card_favorite_button.setOnClickListener {
                KotlinConf.service.markFavorite(sessionCard.session.id)
            }

            favoriteSubscription = sessionCard.isFavorite.watch {
                val image = if (it) {
                    R.drawable.favorite_orange
                } else {
                    R.drawable.favorite_white_empty
                }
                card_favorite_button.setImageResource(image)
            }
        }
    }
}

class TweetHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun showPost(post: FeedPost) {
        with(view) {
            Glide.with(view)
                .load(post.user.profile_image_url_https)
                .into(tweet_avatar)

            tweet_account.text = "@${post.user.screen_name}"
            tweet_name.text = post.user.name
            tweet_text.text = post.text
            tweet_time.text = post.displayDate()
        }
    }
}
