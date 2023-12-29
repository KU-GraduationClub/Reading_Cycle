package com.example.reading_cycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null
    companion object{
        val POST_MAIN_FRAGMENT = "PostMainFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 기본 ActionBar를 숨깁니다.
        supportActionBar?.hide()

        replaceFragment(POST_MAIN_FRAGMENT, false, null)
    }

        fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle? = null) {

            SystemClock.sleep(200)

            // Fragment 교체 상태로 설정한다.
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
            if(newFragment != null){
                oldFragment = newFragment
            }

            // 새로운 Fragment를 담을 변수
            newFragment = when(name){
                POST_MAIN_FRAGMENT -> PostMainFragment()
                else -> Fragment()
            }

            // Fragment를 교체한다.
            fragmentTransaction.replace(R.id.hostFragmentMain, newFragment!!)

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령 동작.
            fragmentTransaction.commit()
        }
     }



