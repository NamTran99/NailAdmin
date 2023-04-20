package com.app.inails.booking.admin.views.youtube

import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.FragmentActivity
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.parcelize.Parcelize

@Parcelize
data class YoutubeActivityArgs(
    val path: String
): BundleArgument

class YoutubeActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener{
    lateinit var youtubePlayerView: YouTubePlayerView
    private val args by lazy { argument<YoutubeActivityArgs>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)
        youtubePlayerView = findViewById(R.id.youtubePlayer)
        youtubePlayerView.initialize(getString(R.string.google_maps_key), this)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?,
                                         wasRestored: Boolean) {
//        youTubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
//        youTubePlayer?.setPlaybackEventListener(playbackEventListener)
            youTubePlayer?.loadVideo(args.path)
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?,
                                         youTubeInitializationResult: YouTubeInitializationResult?) {
        val REQUEST_CODE = 0

        if (youTubeInitializationResult?.isUserRecoverableError == true) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show()
        } else {
            val errorMessage = "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

//    private val playbackEventListener = object: YouTubePlayer.PlaybackEventListener {
//        override fun onSeekTo(p0: Int) {
//        }
//
//        override fun onBuffering(p0: Boolean) {
//        }
//
//        override fun onPlaying() {
//            Toast.makeText(this@YoutubeActivity, "Good, video is playing ok", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onStopped() {
//            Toast.makeText(this@YoutubeActivity, "Video has stopped", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onPaused() {
//            Toast.makeText(this@YoutubeActivity, "Video has paused", Toast.LENGTH_SHORT).show()
//        }
//    }

//    private val playerStateChangeListener = object: YouTubePlayer.PlayerStateChangeListener {
//        override fun onAdStarted() {
//            Toast.makeText(this@YoutubeActivity, "Click Ad now, make the video creator rich!", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onLoading() {
//        }
//
//        override fun onVideoStarted() {
//            Toast.makeText(this@YoutubeActivity, "Video has started", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onLoaded(p0: String?) {
//        }
//
//        override fun onVideoEnded() {
//            Toast.makeText(this@YoutubeActivity, "Congratulations! You've completed another video.", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onError(p0: YouTubePlayer.ErrorReason?) {
//        }
//    }
}