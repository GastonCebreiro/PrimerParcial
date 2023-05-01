package com.example.primerparcial.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.MediaController
import android.widget.VideoView
import com.example.primerparcial.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000
    private val STARTING_VIDEO_URL: String =
        "https://img.pikbest.com/video_listen/588ku_mpeg/19/04/03/c2930a89cee61cad9d970ff250db49d5.mp4"
    lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoViewStart)
        setStartingVideo(STARTING_VIDEO_URL)

        Handler().postDelayed(
            {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT)
    }

    private fun setStartingVideo(videoUrl: String){
        val uri = Uri.parse(videoUrl)
        videoView.setVideoURI(uri)
        videoView.start();
    }

}

