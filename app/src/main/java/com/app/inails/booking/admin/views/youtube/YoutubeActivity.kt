package com.app.inails.booking.admin.views.youtube

import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.parcelize.Parcelize


@Parcelize
class YoutubeActivityArgs(
    val path: String
): BundleArgument

class YoutubeActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener{
    lateinit var youtubePlayerView: YouTubePlayerView
    private val args by lazy { argument<YoutubeActivityArgs>() }
    private var mAutoRotation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAutoRotation = Settings.System.getInt(
            contentResolver,
            Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.app.inails.booking.admin.R.layout.activity_youtube)
        youtubePlayerView = findViewById(com.app.inails.booking.admin.R.id.youtubePlayer)
        youtubePlayerView.initialize(getString(com.app.inails.booking.admin.R.string.google_maps_key), this)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer,
                                         wasRestored: Boolean) {
//        youTubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
//        youTubePlayer?.setPlaybackEventListener(playbackEventListener)

        if (mAutoRotation) {
            youTubePlayer.addFullscreenControlFlag(
                YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                        or YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        or YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
                        or YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT
            )
        } else {
            youTubePlayer.addFullscreenControlFlag(
                (YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                        or YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        or YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT)
            )
        }
        youTubePlayer.setFullscreen(true)
        youTubePlayer.setShowFullscreenButton(true)
        if (!wasRestored) {
            youTubePlayer.loadVideo(args.path)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("TAG", "onConfigurationChanged: NamTD8: change")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
}