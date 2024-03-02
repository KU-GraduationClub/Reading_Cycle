package com.example.reading_cycle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 1500 // 1.5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        // postDelayed 대신에 postDelayed 함수를 사용하여 Handler를 대체합니다.
        window.decorView.postDelayed({
            // 이 부분에서 스플래시 스크린이 끝난 후 표시할 다음 화면의 액티비티로 이동합니다.
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}