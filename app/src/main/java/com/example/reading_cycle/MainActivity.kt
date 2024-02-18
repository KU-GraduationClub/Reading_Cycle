package com.example.reading_cycle

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.reading_cycle.databinding.ActivityMainBinding
import com.example.reading_cycle.post.AddSalePostFragment
import com.example.reading_cycle.post.AddSwapPostFragment
import com.example.reading_cycle.post.PostMainFragment
import com.example.reading_cycle.post.SalePostFragment
import com.example.reading_cycle.post.SwapPostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null
    private lateinit var mainBinding: ActivityMainBinding

    companion object {
        val POST_MAIN_FRAGMENT = "PostMainFragment"
        val ADD_SALE_POST_FRAGMENT = "AddSalePostFragment"
        val ADD_SWAP_POST_FRAGMENT = "AddSwapPostFragment"
        val SALE_POST_FRAGMENT = "SalePostFragment"
        val SWAP_POST_FRAGMENT = "SwapPostFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        // 기본 ActionBar를 숨깁니다.
        supportActionBar?.hide()

        replaceFragment(POST_MAIN_FRAGMENT, false, null)

//        // 네비게이션 바 아이템 클릭 이벤트 처리
//        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.bottom_main -> replaceFragment(POST_MAIN_FRAGMENT, true)
//                R.id.bottom_chat -> replaceFragment(CHAT_FRAGMENT, true)
//                R.id.bottom_frd -> replaceFragment(FRIEND_FRAGMENT, true)
//                R.id.bottom_lib -> replaceFragment(LIBRARY_FRAGMENT, true)
//            }
//            true
//        }

    }

    fun replaceFragment(name: String, addToBackStack: Boolean, bundle: Bundle? = null) {

        SystemClock.sleep(200)

        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
        if (newFragment != null) {
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        newFragment = when (name) {
            POST_MAIN_FRAGMENT -> PostMainFragment()
            ADD_SALE_POST_FRAGMENT -> AddSalePostFragment()
            ADD_SWAP_POST_FRAGMENT -> AddSwapPostFragment()
            SALE_POST_FRAGMENT -> SalePostFragment()
            SWAP_POST_FRAGMENT -> SwapPostFragment()
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

    // Fragment를 BackStack에서 제거.
    fun removeFragment(name: String) {
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}



