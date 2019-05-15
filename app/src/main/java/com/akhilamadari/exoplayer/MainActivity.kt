package com.akhilamadari.exoplayer

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        invalidateOptionsMenu()
        CastContext.getSharedInstance(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.cast, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
           Log.e("helooo","pressed")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            player?.release()
            player = null
        }
    }

    private fun hideSystemUi() {
        epvVideo.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private var player: SimpleExoPlayer? = null

    private fun initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                requireNotNull(this),
                DefaultTrackSelector().apply {
                    parameters = disableClosedCaptionParams()
                })

        epvVideo.player = player
        val uri = Uri.parse("http://cbsnewshd-lh.akamaihd.net/i/CBSNHD_7@199302/index_700_av-p.m3u8")
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(getUserAgent(this,"exoplayer"))
        val hlsDataSourceFactory = DefaultHlsDataSourceFactory(httpDataSourceFactory)
        val hlsMediaSourceFactory = HlsMediaSource.Factory(hlsDataSourceFactory)
        val hlsMediaSource = hlsMediaSourceFactory.createMediaSource(uri)


        player?.prepare(hlsMediaSource)
        player?.playWhenReady = true
        player?.seekTo(0)

    }
    private fun disableClosedCaptionParams() = DefaultTrackSelector.ParametersBuilder()
            .setRendererDisabled(TRACK_TEXT, true)
            .clearSelectionOverrides()
            .build()

    companion object {
        // TODO Determine why 2. Reference: https://stackoverflow.com/questions/42432371/how-to-turn-on-off-closed-captions-in-hls-streaming-url-in-exoplayer
        private const val TRACK_TEXT = 2
    }

}
