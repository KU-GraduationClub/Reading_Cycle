package com.example.reading_cycle


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.reading_cycle.Library.FriendMainFragment
import com.example.reading_cycle.Library.LibraryMainFragment


class MainActivity : AppCompatActivity() {

    companion object {
        const val LIBRARY_MAIN_FRAGMENT = "LibraryMainFragment"
        const val FRIEND_MAIN_FRAGMENT = "FriendMainFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 기본 ActionBar를 숨깁니다.
        supportActionBar?.hide()

        replaceFragment(LIBRARY_MAIN_FRAGMENT, false, null)
    }

    fun replaceFragment(name: String, addToBackStack: Boolean, bundle: Bundle? = null) {
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // 이전 Fragment 제거
        supportFragmentManager.findFragmentById(R.id.hostFragmentMain)?.let { oldFragment ->
            fragmentTransaction.remove(oldFragment)
        }

        // 새로운 Fragment를 담을 변수
        val newFragment = when (name) {
            LIBRARY_MAIN_FRAGMENT -> LibraryMainFragment()
            FRIEND_MAIN_FRAGMENT -> FriendMainFragment()
            else -> Fragment()
        }

        // Fragment를 교체한다.
        fragmentTransaction.replace(R.id.hostFragmentMain, newFragment)

        if (addToBackStack == true) {
            // Fragment를 Backstack에 추가하여 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
            fragmentTransaction.addToBackStack(name)
        }

        // 교체 명령 동작.
        fragmentTransaction.commit()
    }
}



